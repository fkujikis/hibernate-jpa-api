//$Id: DocumentBuilder.java 10865 2006-11-23 23:30:01 +0100 (jeu., 23 nov. 2006) epbernard $
package org.hibernate.search.engine;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.hibernate.Hibernate;
import org.hibernate.annotations.common.AssertionFailure;
import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.annotations.common.reflection.XAnnotatedElement;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XMember;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.search.SearchException;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Keyword;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.Text;
import org.hibernate.search.annotations.Unstored;
import org.hibernate.search.backend.AddLuceneWork;
import org.hibernate.search.backend.DeleteLuceneWork;
import org.hibernate.search.backend.LuceneWork;
import org.hibernate.search.backend.WorkType;
import org.hibernate.search.bridge.BridgeFactory;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.TwoWayFieldBridge;
import org.hibernate.search.store.DirectoryProvider;
import org.hibernate.search.util.BinderHelper;
import org.hibernate.util.ReflectHelper;

/**
 * Set up and provide a manager for indexes classes
 *
 * @author Gavin King
 * @author Emmanuel Bernard
 * @author Sylvain Vieujot
 * @author Richard Hallier
 */
public class DocumentBuilder<T> {
	private static final Log log = LogFactory.getLog( DocumentBuilder.class );

	private final PropertiesMetadata rootPropertiesMetadata = new PropertiesMetadata();
	private final XClass beanClass;
	private final DirectoryProvider directoryProvider;
	private String idKeywordName;
	private XMember idGetter;
	private final Analyzer analyzer;
	private Float idBoost;
	public static final String CLASS_FIELDNAME = "_hibernate_class";
	private TwoWayFieldBridge idBridge;
	private Set<Class> mappedSubclasses = new HashSet<Class>();
	private ReflectionManager reflectionManager;
	private int level = 0;
	private int maxLevel = Integer.MAX_VALUE;


	public DocumentBuilder(XClass clazz, Analyzer analyzer, DirectoryProvider directory,
						   ReflectionManager reflectionManager) {
		this.beanClass = clazz;
		this.analyzer = analyzer;
		this.directoryProvider = directory;
		//FIXME get rid of it when boost is stored?
		this.reflectionManager = reflectionManager;

		if ( clazz == null ) throw new AssertionFailure( "Unable to build a DocumemntBuilder with a null class" );

		rootPropertiesMetadata.boost = getBoost( clazz );
		Set<XClass> processedClasses = new HashSet<XClass>();
		processedClasses.add( clazz );
		initializeMembers(clazz, rootPropertiesMetadata, true, "", processedClasses );
		//processedClasses.remove( clazz ); for the sake of completness

		if ( idKeywordName == null ) {
			throw new SearchException( "No document id for: " + clazz.getName() );
		}
	}

	private void initializeMembers(XClass clazz, PropertiesMetadata propertiesMetadata, boolean isRoot, String prefix,
								   Set<XClass> processedClasses) {
		for ( XClass currClass = clazz; currClass != null; currClass = currClass.getSuperclass() ) {
			//rejecting non properties because the object is loaded from Hibernate, so indexing a non property does not make sense
			List<XProperty> methods = currClass.getDeclaredProperties( XClass.ACCESS_PROPERTY );
			for ( XProperty method : methods ) {
				initializeMember( method, propertiesMetadata, isRoot, prefix, processedClasses );
			}

			List<XProperty> fields = currClass.getDeclaredProperties( XClass.ACCESS_FIELD );
			for ( XProperty field : fields ) {
				initializeMember( field, propertiesMetadata, isRoot, prefix, processedClasses );
			}
		}
	}

	private void initializeMember(XProperty member, PropertiesMetadata propertiesMetadata, boolean isRoot,
								  String prefix, Set<XClass> processedClasses) {
		Keyword keywordAnn = member.getAnnotation( Keyword.class );
		if ( keywordAnn != null ) {
			String name = prefix + BinderHelper.getAttributeName( member, keywordAnn.name() );
			if ( isRoot && keywordAnn.id() ) {
				idKeywordName = name;
				idBoost = getBoost( member );
				FieldBridge fieldBridge = BridgeFactory.guessType( member );
				if ( fieldBridge instanceof TwoWayFieldBridge ) {
					idBridge = (TwoWayFieldBridge) fieldBridge;
				}
				else {
					throw new SearchException(
							"Bridge for document id does not implement IdFieldBridge: " + member.getName() );
				}
				setAccessible( member );
				idGetter = member;
			}
			else {
				setAccessible( member );
				propertiesMetadata.keywordGetters.add( member );
				propertiesMetadata.keywordNames.add( name );
				propertiesMetadata.keywordBridges.add( BridgeFactory.guessType( member ) );
			}
		}

		Unstored unstoredAnn = member.getAnnotation( Unstored.class );
		if ( unstoredAnn != null ) {
			setAccessible( member );
			propertiesMetadata.unstoredGetters.add( member );
			propertiesMetadata.unstoredNames.add( prefix + BinderHelper.getAttributeName( member, unstoredAnn.name() ) );
			propertiesMetadata.unstoredBridges.add( BridgeFactory.guessType( member ) );
		}

		Text textAnn = member.getAnnotation( Text.class );
		if ( textAnn != null ) {
			setAccessible( member );
			propertiesMetadata.textGetters.add( member );
			propertiesMetadata.textNames.add( prefix + BinderHelper.getAttributeName( member, textAnn.name() ) );
			propertiesMetadata.textBridges.add( BridgeFactory.guessType( member ) );
		}

		DocumentId documentIdAnn = member.getAnnotation( DocumentId.class );
		if ( isRoot && documentIdAnn != null ) {
			if ( idKeywordName != null ) {
				throw new AssertionFailure( "Two document id assigned: "
						+ idKeywordName + " and " + BinderHelper.getAttributeName( member, documentIdAnn.name() ) );
			}
			idKeywordName = prefix + BinderHelper.getAttributeName( member, documentIdAnn.name() );
			FieldBridge fieldBridge = BridgeFactory.guessType( member );
			if ( fieldBridge instanceof TwoWayFieldBridge ) {
				idBridge = (TwoWayFieldBridge) fieldBridge;
			}
			else {
				throw new SearchException(
						"Bridge for document id does not implement IdFieldBridge: " + member.getName() );
			}
			idBoost = getBoost( member );
			setAccessible( member );
			idGetter = member;
		}

		org.hibernate.search.annotations.Field fieldAnn =
				member.getAnnotation( org.hibernate.search.annotations.Field.class );
		if ( fieldAnn != null ) {
			setAccessible( member );
			propertiesMetadata.fieldGetters.add( member );
			propertiesMetadata.fieldNames.add( prefix + BinderHelper.getAttributeName( member, fieldAnn.name() ) );
			propertiesMetadata.fieldStore.add( getStore( fieldAnn.store() ) );
			propertiesMetadata.fieldIndex.add( getIndex( fieldAnn.index() ) );
			propertiesMetadata.fieldBridges.add( BridgeFactory.guessType( member ) );
		}

		IndexedEmbedded embeddedAnn = member.getAnnotation( IndexedEmbedded.class );
		if ( embeddedAnn != null ) {
			int oldMaxLevel = maxLevel;
			maxLevel = embeddedAnn.depth() + level > maxLevel ? maxLevel : embeddedAnn.depth() + level;
			level++;

			if ( maxLevel == Integer.MAX_VALUE //infinite
					&& processedClasses.contains( member.getClassOrElementClass() ) ) {
				throw new SearchException(
						"Circular reference. Duplicate use of "
						+ member.getClassOrElementClass().getName()
						+ " in root entity " + beanClass.getName()
						+ "#" + buildEmbeddedPrefix( prefix, embeddedAnn, member )
				);
			}
			if (level <= maxLevel) {
				processedClasses.add( member.getClassOrElementClass() ); //push

				setAccessible( member );
				propertiesMetadata.embeddedGetters.add( member );
				PropertiesMetadata metadata = new PropertiesMetadata();
				propertiesMetadata.embeddedPropertiesMetadata.add(metadata);
				metadata.boost = getBoost( member );
				String localPrefix = buildEmbeddedPrefix( prefix, embeddedAnn, member );
				initializeMembers( member.getClassOrElementClass(), metadata, false, localPrefix, processedClasses);

				processedClasses.remove( member.getClassOrElementClass() ); //pop
			}
			else if ( log.isTraceEnabled() ) {
				String localPrefix = buildEmbeddedPrefix( prefix, embeddedAnn, member );
				log.trace( "depth reached, ignoring " + localPrefix );
			}

			level--;
			maxLevel = oldMaxLevel; //set back the the old max level
		}

		ContainedIn containedAnn = member.getAnnotation( ContainedIn.class );
		if ( containedAnn != null ) {
			setAccessible( member );
			propertiesMetadata.containedInGetters.add( member );
		}
	}

	private String buildEmbeddedPrefix(String prefix, IndexedEmbedded embeddedAnn, XProperty member) {
		String localPrefix = prefix;
		if ( ".".equals( embeddedAnn.prefix() ) ) {
			//default to property name
			localPrefix += member.getName() + '.';
		}
		else {
			localPrefix += embeddedAnn.prefix();
		}
		return localPrefix;
	}

	private Field.Store getStore(Store store) {
		switch (store) {
			case NO:
				return Field.Store.NO;
			case YES:
				return Field.Store.YES;
			case COMPRESS:
				return Field.Store.COMPRESS;
			default:
				throw new AssertionFailure( "Unexpected Store: " + store );
		}
	}

	private Field.Index getIndex(Index index) {
		switch (index) {
			case NO:
				return Field.Index.NO;
			case NO_NORMS:
				return Field.Index.NO_NORMS;
			case TOKENIZED:
				return Field.Index.TOKENIZED;
			case UN_TOKENIZED:
				return Field.Index.UN_TOKENIZED;
			default:
				throw new AssertionFailure( "Unexpected Index: " + index );
		}
	}

	private Float getBoost(XAnnotatedElement element) {
		if ( element == null ) return null;
		Boost boost = element.getAnnotation( Boost.class );
		return boost != null ?
				boost.value() :
				null;
	}

	private Object getMemberValue(Object bean, XMember getter) {
		Object value;
		try {
			value = getter.invoke( bean );
		}
		catch (Exception e) {
			throw new IllegalStateException( "Could not get property value", e );
		}
		return value;
	}

	public void addWorkToQueue(T entity, Serializable id, WorkType workType, List<LuceneWork> queue, SearchFactory searchFactory) {
		Class entityClass = Hibernate.getClass( entity );
		//TODO with the caller loop we are in a n^2: optimize it using a HashMap for work recognition 
		for ( LuceneWork luceneWork : queue) {
			//whatever the actual work, we should ignore
			if ( luceneWork.getEntityClass() == entityClass
					&& luceneWork.getId().equals( id ) ) {//find a way to use Type.equals(x,y)
				return;
			}

		}
		boolean searchForContainers = false;
		if ( workType == WorkType.ADD ) {
			Document doc = getDocument( entity, id );
			queue.add( new AddLuceneWork( id, entityClass, doc ) );
			searchForContainers = true;
		}
		else if ( workType == WorkType.DELETE ) {
			queue.add( new DeleteLuceneWork(id, entityClass) );
		}
		else if ( workType == WorkType.UPDATE ) {
			Document doc = getDocument( entity, id );
			/**
			 * even with Lucene 2.1, use of indexWriter to update is not an option
			 * We can only delete by term, and the index doesn't have a term that
			 * uniquely identify the entry.
			 * But essentially the optimization we are doing is the same Lucene is doing, the only extra cost is the
			 * double file opening.
			 */
			queue.add( new DeleteLuceneWork(id, entityClass) );
			queue.add( new AddLuceneWork( id, entityClass, doc ) );
			searchForContainers = true;
		}
		else {
			throw new AssertionFailure("Unknown WorkType: " + workType);
		}

		/**
		 * When references are changed, either null or another one, we expect dirty checking to be triggered (both sides
		 * have to be updated)
		 * When the internal object is changed, we apply the {Add|Update}Work on containedIns
		 */
		if (searchForContainers) {
			processContainedIn(entity, queue, rootPropertiesMetadata, searchFactory);
		}
	}

	private void processContainedIn(Object instance, List<LuceneWork> queue, PropertiesMetadata metadata, SearchFactory searchFactory) {
		for ( int i = 0; i < metadata.containedInGetters.size(); i++ ) {
			XMember member = metadata.containedInGetters.get( i );
			Object value = getMemberValue( instance, member );
			if (value == null) continue;
			Class valueClass = Hibernate.getClass( value );
			DocumentBuilder builder = searchFactory.getDocumentBuilders().get( valueClass );
			if (builder == null) continue;
			if ( member.isArray() ) {
				for ( Object arrayValue : (Object[]) value ) {
					processContainedInValue( arrayValue, member, queue, valueClass, builder, searchFactory );
				}
			}
			else if ( member.isCollection() ) {
				Collection collection;
				if ( Map.class.equals( member.getCollectionClass() ) ) {
					//hum
					collection = ( (Map) value ).values();
				}
				else {
					collection = (Collection) value;
				}
				for ( Object collectionValue : collection ) {
					processContainedInValue( collectionValue, member, queue, valueClass, builder, searchFactory );
				}
			}
			else {
				processContainedInValue( value, member, queue, valueClass, builder, searchFactory );
			}
		}
		//an embedded cannot have a useful @ContainedIn (no shared reference)
		//do not walk through them
	}

	private void processContainedInValue(Object value, XMember member, List<LuceneWork> queue, Class valueClass,
										 DocumentBuilder builder, SearchFactory searchFactory) {
		Serializable id = (Serializable) builder.getMemberValue( value, builder.idGetter );
		builder.addWorkToQueue( value, id, WorkType.UPDATE, queue, searchFactory );
	}

	public Document getDocument(T instance, Serializable id) {
		Document doc = new Document();
		XClass instanceClass = reflectionManager.toXClass( instance.getClass() );
		if ( rootPropertiesMetadata.boost != null ) {
			doc.setBoost( rootPropertiesMetadata.boost );
		}
		{
			Field classField =
					new Field( CLASS_FIELDNAME, instanceClass.getName(), Field.Store.YES, Field.Index.UN_TOKENIZED );
			doc.add( classField );
			idBridge.set( idKeywordName, id, doc, Field.Store.YES, Field.Index.UN_TOKENIZED, idBoost );
		}
		buildDocumentFields( instance, doc, rootPropertiesMetadata );
		return doc;
	}

	private void buildDocumentFields(Object instance, Document doc, PropertiesMetadata propertiesMetadata) {
		if (instance == null) return;

		for ( int i = 0; i < propertiesMetadata.keywordNames.size(); i++ ) {
			XMember member = propertiesMetadata.keywordGetters.get( i );
			Object value = getMemberValue( instance, member );
			propertiesMetadata.keywordBridges.get( i ).set(
					propertiesMetadata.keywordNames.get( i ), value, doc, Field.Store.YES,
					Field.Index.UN_TOKENIZED, getBoost( member )
			);
		}
		for ( int i = 0; i < propertiesMetadata.textNames.size(); i++ ) {
			XMember member = propertiesMetadata.textGetters.get( i );
			Object value = getMemberValue( instance, member );
			propertiesMetadata.textBridges.get( i ).set(
					propertiesMetadata.textNames.get( i ), value, doc, Field.Store.YES,
					Field.Index.TOKENIZED, getBoost( member )
			);
		}
		for ( int i = 0; i < propertiesMetadata.unstoredNames.size(); i++ ) {
			XMember member = propertiesMetadata.unstoredGetters.get( i );
			Object value = getMemberValue( instance, member );
			propertiesMetadata.unstoredBridges.get( i ).set(
					propertiesMetadata.unstoredNames.get( i ), value, doc, Field.Store.NO,
					Field.Index.TOKENIZED, getBoost( member )
			);
		}
		for ( int i = 0; i < propertiesMetadata.fieldNames.size(); i++ ) {
			XMember member = propertiesMetadata.fieldGetters.get( i );
			Object value = getMemberValue( instance, member );
			propertiesMetadata.fieldBridges.get( i ).set(
					propertiesMetadata.fieldNames.get( i ), value, doc, propertiesMetadata.fieldStore.get( i ),
					propertiesMetadata.fieldIndex.get( i ), getBoost( member )
			);
		}
		for ( int i = 0; i < propertiesMetadata.embeddedGetters.size(); i++ ) {
			XMember member = propertiesMetadata.embeddedGetters.get( i );
			Object value = getMemberValue( instance, member );
			//if ( ! Hibernate.isInitialized( value ) ) continue; //this sounds like a bad idea 
			//TODO handle boost at embedded level: already stored in propertiesMedatada.boost
			buildDocumentFields( value, doc, propertiesMetadata.embeddedPropertiesMetadata.get( i ) );
		}
	}

	public Term getTerm(Serializable id) {
		return new Term( idKeywordName, idBridge.objectToString( id ) );
	}

	public DirectoryProvider getDirectoryProvider() {
		return directoryProvider;
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	private static void setAccessible(XMember member) {
		if ( !Modifier.isPublic( member.getModifiers() ) ) {
			member.setAccessible( true );
		}
	}

	public TwoWayFieldBridge getIdBridge() {
		return idBridge;
	}

	public String getIdKeywordName() {
		return idKeywordName;
	}

	public static Class getDocumentClass(Document document) {
		String className = document.get( DocumentBuilder.CLASS_FIELDNAME );
		try {
			return ReflectHelper.classForName( className );
		}
		catch (ClassNotFoundException e) {
			throw new SearchException( "Unable to load indexed class: " + className, e );
		}
	}

	public static Serializable getDocumentId(SearchFactory searchFactory, Class clazz, Document document) {
		DocumentBuilder builder = searchFactory.getDocumentBuilders().get( clazz );
		if ( builder == null ) throw new SearchException( "No Lucene configuration set up for: " + clazz.getName() );
		return (Serializable) builder.getIdBridge().get( builder.getIdKeywordName(), document );
	}

	public void postInitialize(Set<Class> indexedClasses) {
		//this method does not requires synchronization
		Class plainClass = reflectionManager.toClass( beanClass );
		Set<Class> tempMappedSubclasses = new HashSet<Class>();
		//together with the caller this creates a o(2), but I think it's still faster than create the up hierarchy for each class
		for ( Class currentClass : indexedClasses ) {
			if ( plainClass.isAssignableFrom( currentClass ) ) tempMappedSubclasses.add( currentClass );
		}
		mappedSubclasses = Collections.unmodifiableSet( tempMappedSubclasses );
	}


	public Set<Class> getMappedSubclasses() {
		return mappedSubclasses;
	}

	private static class PropertiesMetadata {
		public Float boost = null;
		public final List<XMember> keywordGetters = new ArrayList<XMember>();
		public final List<String> keywordNames = new ArrayList<String>();
		public final List<FieldBridge> keywordBridges = new ArrayList<FieldBridge>();
		public final List<XMember> unstoredGetters = new ArrayList<XMember>();
		public final List<String> unstoredNames = new ArrayList<String>();
		public final List<FieldBridge> unstoredBridges = new ArrayList<FieldBridge>();
		public final List<XMember> textGetters = new ArrayList<XMember>();
		public final List<String> textNames = new ArrayList<String>();
		public final List<FieldBridge> textBridges = new ArrayList<FieldBridge>();
		public final List<String> fieldNames = new ArrayList<String>();
		public final List<XMember> fieldGetters = new ArrayList<XMember>();
		public final List<FieldBridge> fieldBridges = new ArrayList<FieldBridge>();
		public final List<Field.Store> fieldStore = new ArrayList<Field.Store>();
		public final List<Field.Index> fieldIndex = new ArrayList<Field.Index>();
		public final List<XMember> embeddedGetters = new ArrayList<XMember>();
		public final List<PropertiesMetadata> embeddedPropertiesMetadata = new ArrayList<PropertiesMetadata>();
		public final List<XMember> containedInGetters = new ArrayList<XMember>();
	}
}
