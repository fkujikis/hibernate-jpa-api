// $Id$
package org.hibernate.cfg;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.hibernate.AnnotationException;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.cfg.annotations.Version;
import org.hibernate.cfg.annotations.reflection.EJB3ReflectionManager;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreUpdateEventListener;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostUpdateEventListener;
import org.hibernate.event.PostDeleteEventListener;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.util.JoinedIterator;
import org.hibernate.util.ReflectHelper;
import org.hibernate.util.StringHelper;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Similar to the {@link Configuration} object but handles EJB3 and Hibernate
 * specific annotations as a metadata facility
 *
 * @author Emmanuel Bernard
 */
public class AnnotationConfiguration extends Configuration {
	private static Log log = LogFactory.getLog( AnnotationConfiguration.class );

	static {
		Version.touch(); //touch version
	}

	public static final String ARTEFACT = "hibernate.mapping.precedence";
	public static final String DEFAULT_PRECEDENCE = "hbm, class";

	private Map namedGenerators;
	private Map<String, Map<String, Join>> joins;
	private Map<String, AnnotatedClassType> classTypes;
	private Set<String> defaultNamedQueryNames;
	private Set<String> defaultNamedNativeQueryNames;
	private Set<String> defaultSqlResulSetMappingNames;
	private Set<String> defaultNamedGenerators;
	private Map<String, Properties> generatorTables;
	private Map<Table, List<String[]>> tableUniqueConstraints;
	private Map<String, String> mappedByResolver;
	private Map<String, String> propertyRefResolver;
	private List<XClass> annotatedClasses;
	private Map<String, XClass> annotatedClassEntities;
	private Map<String, Document> hbmEntities;
	private List<CacheHolder> caches;
	private List<Document> hbmDocuments; //user ordering matters, hence the list
	private String precedence = null;
	private boolean inSecondPass = false;
	private transient ReflectionManager reflectionManager;
	private boolean isDefaultProcessed = false;

	public AnnotationConfiguration() {
		super();
	}

	public AnnotationConfiguration(SettingsFactory sf) {
		super( sf );
	}

	protected List<XClass> orderAndFillHierarchy(List<XClass> original) {
		//TODO remove embeddable
		List<XClass> copy = new ArrayList<XClass>( original );
		//for each class, copy all the relevent hierarchy
		for ( XClass clazz : original ) {
			XClass superClass = clazz.getSuperclass();
			while ( superClass != null && ! reflectionManager.equals( superClass, Object.class ) && ! copy.contains( superClass ) ) {
				if ( superClass.isAnnotationPresent( Entity.class )
						|| superClass.isAnnotationPresent( MappedSuperclass.class ) ) {
					copy.add( superClass );
				}
				superClass = superClass.getSuperclass();
			}
		}
		List<XClass> workingCopy = new ArrayList<XClass>( copy );
		List<XClass> newList = new ArrayList<XClass>( copy.size() );
		while ( workingCopy.size() > 0 ) {
			XClass clazz = workingCopy.get( 0 );
			orderHierarchy( workingCopy, newList, copy, clazz );
		}
		return newList;
	}

	private void orderHierarchy(List<XClass> copy, List<XClass> newList, List<XClass> original, XClass clazz) {
		if ( clazz == null || reflectionManager.equals( clazz, Object.class ) ) return;
		//process superclass first
		orderHierarchy( copy, newList, original, clazz.getSuperclass() );
		if ( original.contains( clazz ) ) {
			if ( !newList.contains( clazz ) ) {
				newList.add( clazz );
			}
			copy.remove( clazz );
		}
	}

	/**
	 * Read a mapping from the class annotation metadata (JSR 175).
	 *
	 * @param persistentClass the mapped class
	 * @return the configuration object
	 */
	public AnnotationConfiguration addAnnotatedClass(Class persistentClass) throws MappingException {
		XClass persistentXClass = reflectionManager.toXClass( persistentClass );
		try {
			annotatedClasses.add( persistentXClass );
			return this;
		}
		catch (MappingException me) {
			log.error( "Could not compile the mapping annotations", me );
			throw me;
		}
	}

	/**
	 * Read package level metadata
	 *
	 * @param packageName java package name
	 * @return the configuration object
	 */
	public AnnotationConfiguration addPackage(String packageName) throws MappingException {
		log.info( "Mapping package " + packageName );
		try {
			AnnotationBinder.bindPackage( packageName, createExtendedMappings() );
			return this;
		}
		catch (MappingException me) {
			log.error( "Could not compile the mapping annotations", me );
			throw me;
		}
	}

	public ExtendedMappings createExtendedMappings() {
		return new ExtendedMappings(
				classes,
				collections,
				tables,
				namedQueries,
				namedSqlQueries,
				sqlResultSetMappings,
				defaultNamedQueryNames,
				defaultNamedNativeQueryNames,
				defaultSqlResulSetMappingNames,
				defaultNamedGenerators,
				imports,
				secondPasses,
				propertyReferences,
				namingStrategy,
				typeDefs,
				filterDefinitions,
				namedGenerators,
				joins,
				classTypes,
				extendsQueue,
				tableNameBinding, columnNameBindingPerTable, auxiliaryDatabaseObjects,
				generatorTables,
				tableUniqueConstraints,
				mappedByResolver,
				propertyRefResolver,
				reflectionManager
		);
	}

	@Override
	public void setCacheConcurrencyStrategy(
			String clazz, String concurrencyStrategy, String region, boolean cacheLazyProperty
	) throws MappingException {
		caches.add( new CacheHolder( clazz, concurrencyStrategy, region, true, cacheLazyProperty ) );
	}

	@Override
	public void setCollectionCacheConcurrencyStrategy(String collectionRole, String concurrencyStrategy, String region)
			throws MappingException {
		caches.add( new CacheHolder( collectionRole, concurrencyStrategy, region, false, false ) );
	}

	@Override
	protected void reset() {
		super.reset();
		namedGenerators = new HashMap();
		joins = new HashMap<String, Map<String, Join>>();
		classTypes = new HashMap<String, AnnotatedClassType>();
		generatorTables = new HashMap<String, Properties>();
		defaultNamedQueryNames = new HashSet<String>();
		defaultNamedNativeQueryNames = new HashSet<String>();
		defaultSqlResulSetMappingNames = new HashSet<String>();
		defaultNamedGenerators = new HashSet<String>();
		tableUniqueConstraints = new HashMap<Table, List<String[]>>();
		mappedByResolver = new HashMap<String, String>();
		propertyRefResolver = new HashMap<String, String>();
		annotatedClasses = new ArrayList<XClass>();
		caches = new ArrayList<CacheHolder>();
		hbmEntities = new HashMap<String, Document>();
		annotatedClassEntities = new HashMap<String, XClass>();
		hbmDocuments = new ArrayList<Document>();
		namingStrategy = EJB3NamingStrategy.INSTANCE;
		setEntityResolver( new EJB3DTDEntityResolver() );
		reflectionManager = new EJB3ReflectionManager();
	}

	@Override
	protected void secondPassCompile() throws MappingException {
		log.debug( "Execute first pass mapping processing" );
		//build annotatedClassEntities
		{
			List<XClass> tempAnnotatedClasses = new ArrayList<XClass>( annotatedClasses.size() );
			for ( XClass clazz : annotatedClasses ) {
				if ( clazz.isAnnotationPresent( Entity.class ) ) {
					annotatedClassEntities.put( clazz.getName(), clazz );
					tempAnnotatedClasses.add( clazz );
				}
				else if ( clazz.isAnnotationPresent( MappedSuperclass.class ) ) {
					tempAnnotatedClasses.add( clazz );
				}
				//only keep MappedSuperclasses and Entity in this list
			}
			annotatedClasses = tempAnnotatedClasses;
		}

		//process default values first
		if ( ! isDefaultProcessed ) {
			AnnotationBinder.bindDefaults( createExtendedMappings() );
			isDefaultProcessed = true;
		}

		//process entities
		if ( precedence == null ) precedence = getProperties().getProperty( ARTEFACT );
		if ( precedence == null ) precedence = DEFAULT_PRECEDENCE;
		StringTokenizer precedences = new StringTokenizer( precedence, ",; ", false );
		if ( ! precedences.hasMoreElements() ) {
			throw new MappingException( ARTEFACT + " cannot be empty: " + precedence );
		}
		while ( precedences.hasMoreElements() ) {
			String artifact = (String) precedences.nextElement();
			removeConflictedArtifact( artifact );
			processArtifactsOfType( artifact );
		}

		int cacheNbr = caches.size();
		for ( int index = 0; index < cacheNbr ; index++ ) {
			CacheHolder cacheHolder = caches.get( index );
			if ( cacheHolder.isClass ) {
				super.setCacheConcurrencyStrategy(
						cacheHolder.role, cacheHolder.usage, cacheHolder.region, cacheHolder.cacheLazy
				);
			}
			else {
				super.setCollectionCacheConcurrencyStrategy( cacheHolder.role, cacheHolder.usage, cacheHolder.region );
			}
		}
		caches.clear();

		inSecondPass = true;
		processFkSecondPassInOrder();
		Iterator iter = secondPasses.iterator();
		while ( iter.hasNext() ) {
			SecondPass sp = (SecondPass) iter.next();
			//do the second pass of fk before the others and remove them
			if ( sp instanceof CreateKeySecondPass ) {
				sp.doSecondPass( classes );
				iter.remove();
			}
		}

		//process OneToManySecondPass in order: first
		iter = secondPasses.iterator();
		while ( iter.hasNext() ) {
			SecondPass sp = (SecondPass) iter.next();

			if ( sp instanceof CreateKeySecondPass ) {
				sp.doSecondPass( classes );
				iter.remove();
			}
		}
		super.secondPassCompile();
		inSecondPass = false;
		Iterator tables = (Iterator<Map.Entry<Table, List<String[]>>>) tableUniqueConstraints.entrySet().iterator();
		Table table;
		Map.Entry entry;
		String keyName;
		int uniqueIndexPerTable;
		while ( tables.hasNext() ) {
			entry = (Map.Entry) tables.next();
			table = (Table) entry.getKey();
			List<String[]> uniqueConstraints = (List<String[]>) entry.getValue();
			uniqueIndexPerTable = 0;
			for ( String[] columnNames : uniqueConstraints ) {
				keyName = "key" + uniqueIndexPerTable++;
				buildUniqueKeyFromColumnNames( columnNames, table, keyName );
			}
		}
		boolean applyOnDdl = getProperties().getProperty(
				"hibernate.validator.apply_to_ddl", //org.hibernate.validator.Environment.APPLY_TO_DDL
				"true" )
				.equalsIgnoreCase( "true" );

		Constructor validatorCtr = null;
		Method applyMethod = null;
		try {
			Class classValidator = ReflectHelper.classForName("org.hibernate.validator.ClassValidator", this.getClass() );
			Class messageInterpolator = ReflectHelper.classForName("org.hibernate.validator.MessageInterpolator", this.getClass() );
			validatorCtr = classValidator.getDeclaredConstructor( new Class[] {
					Class.class, ResourceBundle.class, messageInterpolator, Map.class, ReflectionManager.class
					}
			);
			applyMethod = classValidator.getMethod( "apply", PersistentClass.class );
		}
		catch (ClassNotFoundException e) {
			log.info( "Hibernate Validator not found: ignoring");
		}
		catch (NoSuchMethodException e) {
			throw new AnnotationException(e);
		}
		if ( applyMethod != null && applyOnDdl) {
			for ( PersistentClass persistentClazz : (Collection<PersistentClass>) classes.values() ) {
				//integrate the validate framework
				String className = persistentClazz.getClassName();
				if ( StringHelper.isNotEmpty( className ) ) {
					try {
						Object validator = validatorCtr.newInstance(
								ReflectHelper.classForName( className ), null, null, null, reflectionManager
						);
						applyMethod.invoke( validator, persistentClazz );
					}
					catch (Exception e) {
						log.warn("Unable to apply constraints on DDL for " + className, e);
					}
				}
			}
		}
	}

	private void processFkSecondPassInOrder() {
		log.debug( "processing manytoone fk mappings" );
		Iterator iter = secondPasses.iterator();
		/* We need to process FKSecond pass trying to resolve any
		 * graph circularity (ie PK made of a many to one linking to
		 * an entity having a PK made of a ManyToOne ...
		 */
		SortedSet<FkSecondPass> fkSecondPasses = new TreeSet<FkSecondPass>(
				new Comparator() {
					//The comparator implementation has to respect the compare=0 => equals() = true for sets
					public int compare(Object o1, Object o2) {
						if (! (o1 instanceof FkSecondPass &&  o2 instanceof FkSecondPass) ) {
							throw new AssertionFailure("comparint FkSecondPass with non FkSecondPass");
						}
						FkSecondPass f1 = (FkSecondPass) o1;
						FkSecondPass f2 = (FkSecondPass) o2;
						int compare = f1.getValue().getTable().getQuotedName().compareTo(
								f2.getValue().getTable().getQuotedName()
						);
						if (compare == 0) {
							//same table, we still need to differenciate true equality
							if ( f1.hashCode() < f2.hashCode() ) {
								compare = -1;
							}
							else if ( f1.hashCode() == f2.hashCode() ) {
								compare = 0;
							}
							else {
								compare = 1;
							}
						}
						return compare;
					}
				}
		);
		while ( iter.hasNext() ) {
			SecondPass sp = (SecondPass) iter.next();
			//do the second pass of fk before the others and remove them
			if ( sp instanceof FkSecondPass ) {
				fkSecondPasses.add( (FkSecondPass) sp );
				iter.remove();
			}
		}
		if ( fkSecondPasses.size() > 0 ) {
			Map<String, Set<String>> isADependencyOf = new HashMap<String, Set<String>>();
			List orderedFkSecondPasses = new ArrayList( fkSecondPasses.size() );
			List endOfQueueFkSecondPasses = new ArrayList( fkSecondPasses.size() );
			List orderedTable = new ArrayList( fkSecondPasses.size() );
			Iterator it = fkSecondPasses.iterator();
			while ( it.hasNext() ) {
				FkSecondPass sp = (FkSecondPass) it.next();
				String referenceEntityName = sp.getValue().getReferencedEntityName();
				PersistentClass classMapping = getClassMapping( referenceEntityName );
				if ( sp.isInPrimaryKey() ) {
					String dependentTable = classMapping.getTable().getQuotedName();
					if ( ! isADependencyOf.containsKey( dependentTable ) ) {
						isADependencyOf.put( dependentTable, new HashSet<String>() );
					}
					String table = sp.getValue().getTable().getQuotedName();
					isADependencyOf.get( dependentTable ).add( table );
					int beAfter = orderedTable.indexOf( dependentTable );
					int beBefore = orderedFkSecondPasses.size();
					Set<String> dependencies = isADependencyOf.get( table );
					if ( dependencies != null ) {
						for ( String tableDep : dependencies ) {
							//for each declared dependency take the lowest index
							int index = orderedTable.indexOf( tableDep );
							//index = -1 when we have a self dependency
							beBefore = index != -1 && index < beBefore ? index : beBefore;
						}
					}
					int currentIndex = orderedTable.indexOf( table );
					if ( beBefore < beAfter ||
							( currentIndex != -1 && ( currentIndex < beAfter || currentIndex > beBefore ) )
							) {
						StringBuilder sb = new StringBuilder(
								"Foreign key circularity dependency involving the following tables: "
						);
						//TODO deduplicate tables
						sb.append( table );
						if ( beAfter > -1 ) sb.append( ", " ).append( dependentTable );
						if ( beBefore < orderedFkSecondPasses.size() ) {
							sb.append( ", " ).append( orderedTable.get( beBefore ) );
						}
						throw new AnnotationException( sb.toString() );
					}
					currentIndex = currentIndex == -1 ? beBefore : currentIndex;
					orderedTable.add( currentIndex, table );
					orderedFkSecondPasses.add( currentIndex, sp );
				}
				else {
					endOfQueueFkSecondPasses.add( sp );
				}
			}
			it = orderedFkSecondPasses.listIterator();
			while ( it.hasNext() ) {
				( (SecondPass) it.next() ).doSecondPass( classes );
			}
			it = endOfQueueFkSecondPasses.listIterator();
			while ( it.hasNext() ) {
				( (SecondPass) it.next() ).doSecondPass( classes );
			}
		}
	}

	private void processArtifactsOfType(String artifact) {
		if ( "hbm".equalsIgnoreCase( artifact ) ) {
			log.debug( "Process hbm files" );
			for ( Document document : hbmDocuments ) {
				super.add( document );
			}
			hbmDocuments.clear();
			hbmEntities.clear();
		}
		else if ( "class".equalsIgnoreCase( artifact ) ) {
			log.debug( "Process annotated classes" );
			//bind classes in the correct order calculating some inheritance state
			List<XClass> orderedClasses = orderAndFillHierarchy( annotatedClasses );
			Map<XClass, InheritanceState> inheritanceStatePerClass = AnnotationBinder.buildInheritanceStates(
					orderedClasses, reflectionManager
			);
			ExtendedMappings mappings = createExtendedMappings();
			for ( XClass clazz : orderedClasses ) {
				//todo use the same extended mapping
				AnnotationBinder.bindClass( clazz, inheritanceStatePerClass, mappings );
			}
			annotatedClasses.clear();
			annotatedClassEntities.clear();
		}
		else {
			log.warn( "Unknown artifact: " + artifact );
		}
	}

	private void removeConflictedArtifact(String artifact) {
		if ( "hbm".equalsIgnoreCase( artifact ) ) {
			for ( String entity : hbmEntities.keySet() ) {
				if ( annotatedClassEntities.containsKey( entity ) ) {
					annotatedClasses.remove( annotatedClassEntities.get( entity ) );
					annotatedClassEntities.remove( entity );
				}
			}
		}
		else if ( "class".equalsIgnoreCase( artifact ) ) {
			for ( String entity : annotatedClassEntities.keySet() ) {
				if ( hbmEntities.containsKey( entity ) ) {
					hbmDocuments.remove( hbmEntities.get( entity ) );
					hbmEntities.remove( entity );
				}
			}
		}
	}

	private void buildUniqueKeyFromColumnNames(String[] columnNames, Table table, String keyName) {
		UniqueKey uc;
		int size = columnNames.length;
		Column[] columns = new Column[size];
		Set<Column> unbound = new HashSet<Column>();
		Set<Column> unboundNoLogical = new HashSet<Column>();
		ExtendedMappings mappings = createExtendedMappings();
		for ( int index = 0; index < size ; index++ ) {
			String columnName;
			try {
				columnName = mappings.getPhysicalColumnName( columnNames[index], table );
				columns[index] = new Column( columnName );
				unbound.add( columns[index] );
				//column equals and hashcode is based on column name
			}
			catch( MappingException e ) {
				unboundNoLogical.add( new Column( columnNames[index] ) );
			}
		}
		for ( Column column : columns ) {
			if ( table.containsColumn( column ) ) {
				uc = table.getOrCreateUniqueKey( keyName );
				uc.addColumn( table.getColumn( column ) );
				unbound.remove( column );
			}
		}
		if ( unbound.size() > 0 || unboundNoLogical.size() > 0 ) {
			StringBuilder sb = new StringBuilder( "Unable to create unique key constraint (" );
			for ( String columnName : columnNames ) {
				sb.append( columnName ).append( ", " );
			}
			sb.setLength( sb.length() - 2 );
			sb.append( ") on table " ).append( table.getName() ).append( ": " );
			for ( Column column : unbound ) {
				sb.append( column.getName() ).append( ", " );
			}
			for ( Column column : unboundNoLogical ) {
				sb.append( column.getName() ).append( ", " );
			}
			sb.setLength( sb.length() - 2 );
			sb.append( " not found" );
			throw new AnnotationException( sb.toString() );
		}
	}

	@Override
	protected void parseMappingElement(Element subelement, String name) {
		Attribute rsrc = subelement.attribute( "resource" );
		Attribute file = subelement.attribute( "file" );
		Attribute jar = subelement.attribute( "jar" );
		Attribute pckg = subelement.attribute( "package" );
		Attribute clazz = subelement.attribute( "class" );
		if ( rsrc != null ) {
			log.debug( name + "<-" + rsrc );
			addResource( rsrc.getValue() );
		}
		else if ( jar != null ) {
			log.debug( name + "<-" + jar );
			addJar( new File( jar.getValue() ) );
		}
		else if ( file != null ) {
			log.debug( name + "<-" + file );
			addFile( file.getValue() );
		}
		else if ( pckg != null ) {
			log.debug( name + "<-" + pckg );
			addPackage( pckg.getValue() );
		}
		else if ( clazz != null ) {
			log.debug( name + "<-" + clazz );
			Class loadedClass = null;
			try {
				loadedClass = ReflectHelper.classForName( clazz.getValue() );
			}
			catch (ClassNotFoundException cnf) {
				throw new MappingException(
						"Unable to load class declared as <mapping class=\"" + clazz.getValue() + "\"/> in the configuration:",
						cnf
				);
			}
			catch (NoClassDefFoundError ncdf) {
				throw new MappingException(
						"Unable to load class declared as <mapping class=\"" + clazz.getValue() + "\"/> in the configuration:",
						ncdf
				);
			}

			addAnnotatedClass( loadedClass );
		}
		else {
			throw new MappingException( "<mapping> element in configuration specifies no attributes" );
		}
	}

	@Override
	protected void add(org.dom4j.Document doc) throws MappingException {
		boolean ejb3Xml = "entity-mappings".equals( doc.getRootElement().getName() );
		if ( inSecondPass ) {
			//if in second pass bypass the queueing, getExtendedQueue reuse this method
			if ( !ejb3Xml ) super.add( doc );
		}
		else {
			if ( ! ejb3Xml ) {
				final Element hmNode = doc.getRootElement();
				Attribute packNode = hmNode.attribute( "package" );
				String defaultPackage = packNode != null
						? packNode.getValue()
						: "";
				Set<String> entityNames = new HashSet<String>();
				findClassNames( defaultPackage, hmNode, entityNames );
				for ( String entity : entityNames ) {
					hbmEntities.put( entity, doc );
				}
				hbmDocuments.add( doc );
			}
			else {
				List<String> classnames = ( (EJB3ReflectionManager) reflectionManager ).getXMLContext().addDocument( doc );
				for ( String classname : classnames ) {
					try {
						annotatedClasses.add( reflectionManager.classForName( classname, this.getClass() ) );
					}
					catch (ClassNotFoundException e) {
						throw new AnnotationException( "Unable to load class defined in XML: " + classname, e );
					}
				}
			}
		}
	}

	private static void findClassNames(
			String defaultPackage, final Element startNode,
			final java.util.Set names
	) {
		// if we have some extends we need to check if those classes possibly could be inside the
		// same hbm.xml file...
		Iterator[] classes = new Iterator[4];
		classes[0] = startNode.elementIterator( "class" );
		classes[1] = startNode.elementIterator( "subclass" );
		classes[2] = startNode.elementIterator( "joined-subclass" );
		classes[3] = startNode.elementIterator( "union-subclass" );

		Iterator classIterator = new JoinedIterator( classes );
		while ( classIterator.hasNext() ) {
			Element element = (Element) classIterator.next();
			String entityName = element.attributeValue( "entity-name" );
			if ( entityName == null ) entityName = getClassName( element.attribute( "name" ), defaultPackage );
			names.add( entityName );
			findClassNames( defaultPackage, element, names );
		}
	}

	private static String getClassName(Attribute name, String defaultPackage) {
		if ( name == null ) return null;
		String unqualifiedName = name.getValue();
		if ( unqualifiedName == null ) return null;
		if ( unqualifiedName.indexOf( '.' ) < 0 && defaultPackage != null ) {
			return defaultPackage + '.' + unqualifiedName;
		}
		return unqualifiedName;
	}

	public void setPrecedence(String precedence) {
		this.precedence = precedence;
	}

	private static class CacheHolder {
		public CacheHolder(String role, String usage, String region, boolean isClass, boolean cacheLazy) {
			this.role = role;
			this.usage = usage;
			this.region = region;
			this.isClass = isClass;
			this.cacheLazy = cacheLazy;
		}

		public String role;
		public String usage;
		public String region;
		public boolean isClass;
		public boolean cacheLazy;
	}

	@Override
	public Configuration addInputStream(InputStream xmlInputStream) throws MappingException {
		try {
			List errors = new ArrayList();
			SAXReader saxReader = xmlHelper.createSAXReader( "XML InputStream", errors, getEntityResolver() );
			try {
				saxReader.setFeature( "http://apache.org/xml/features/validation/schema", true );
				//saxReader.setFeature( "http://apache.org/xml/features/validation/dynamic", true );
				//set the default schema locators
				saxReader.setProperty(
						"http://apache.org/xml/properties/schema/external-schemaLocation",
						"http://java.sun.com/xml/ns/persistence/orm orm_1_0.xsd"
				);
			}
			catch (SAXException e) {
				saxReader.setValidation( false );
			}
			org.dom4j.Document doc = saxReader
					.read( new InputSource( xmlInputStream ) );

			if ( errors.size() != 0 ) {
				throw new MappingException( "invalid mapping", (Throwable) errors.get( 0 ) );
			}
			add( doc );
			return this;
		}
		catch (DocumentException e) {
			throw new MappingException( "Could not parse mapping document in input stream", e );
		}
		finally {
			try {
				xmlInputStream.close();
			}
			catch (IOException ioe) {
				log.warn( "Could not close input stream", ioe );
			}
		}
	}

	public SessionFactory buildSessionFactory() throws HibernateException {
		//add validator events if the jar is available
		boolean enableValidatorListeners = ! "false".equalsIgnoreCase( getProperty( "hibernate.validator.autoregister_listeners" ) );
		Class validateEventListenerClass = null;
		try {
			validateEventListenerClass = ReflectHelper.classForName(
					"org.hibernate.validator.event.ValidateEventListener",
					AnnotationConfiguration.class );
		}
		catch (ClassNotFoundException e) {
			//validator is not present
			log.debug( "Validator not present in classpath, ignoring event listener registration" );
		}
		if (enableValidatorListeners && validateEventListenerClass != null) {
			//TODO so much duplication
			Object validateEventListener;
			try {
				validateEventListener = validateEventListenerClass.newInstance();
			}
			catch (Exception e) {
				throw new AnnotationException("Unable to load Validator event listener", e );
			}
			{
				boolean present = false;
				PreInsertEventListener[] listeners = getEventListeners().getPreInsertEventListeners();
				if (listeners != null) {
					for ( Object eventListener : listeners ) {
						//not isAssignableFrom since the user could subclass
						present = present || validateEventListenerClass == eventListener.getClass();
					}
					if (!present) {
						int length = listeners.length + 1;
						PreInsertEventListener[] newListeners = new PreInsertEventListener[length];
						for ( int i = 0 ; i < length - 1 ; i++ ) {
							newListeners[i] = listeners[i];
						}
						newListeners[length-1] = (PreInsertEventListener) validateEventListener;
						getEventListeners().setPreInsertEventListeners(newListeners);
					}
				}
				else {
					getEventListeners().setPreInsertEventListeners(
							new PreInsertEventListener[] { (PreInsertEventListener) validateEventListener }
					);
				}
			}

			//update event listener
			{
				boolean present = false;
				PreUpdateEventListener[] listeners = getEventListeners().getPreUpdateEventListeners();
				if (listeners != null) {
					for ( Object eventListener : listeners ) {
						//not isAssignableFrom since the user could subclass
						present = present || validateEventListenerClass == eventListener.getClass();
					}
					if (!present) {
						int length = listeners.length + 1;
						PreUpdateEventListener[] newListeners = new PreUpdateEventListener[length];
						for ( int i = 0 ; i < length - 1 ; i++ ) {
							newListeners[i] = listeners[i];
						}
						newListeners[length-1] = (PreUpdateEventListener) validateEventListener;
						getEventListeners().setPreUpdateEventListeners(newListeners);
					}
				}
				else {
					getEventListeners().setPreUpdateEventListeners(
							new PreUpdateEventListener[] { (PreUpdateEventListener) validateEventListener }
					);
				}
			}
		}

		//add search events if the jar is available
		boolean enableSearchListeners = ! "false".equalsIgnoreCase( getProperty( "hibernate.search.autoregister_listeners" ) );
		Class searchEventListenerClass = null;
		try {
			searchEventListenerClass = ReflectHelper.classForName(
					"org.hibernate.search.event.FullTextIndexEventListener",
					AnnotationConfiguration.class );
		}
		catch (ClassNotFoundException e) {
			//search is not present
			log.debug( "Search not present in classpath, ignoring event listener registration" );
		}
		if (enableSearchListeners && searchEventListenerClass != null) {
			//TODO so much duplication
			Object searchEventListener;
			try {
				searchEventListener = searchEventListenerClass.newInstance();
			}
			catch (Exception e) {
				throw new AnnotationException("Unable to load Search event listener", e );
			}
			{
				boolean present = false;
				PostInsertEventListener[] listeners = getEventListeners().getPostInsertEventListeners();
				if (listeners != null) {
					for ( Object eventListener : listeners ) {
						//not isAssignableFrom since the user could subclass
						present = present || searchEventListenerClass == eventListener.getClass();
					}
					if (!present) {
						int length = listeners.length + 1;
						PostInsertEventListener[] newListeners = new PostInsertEventListener[length];
						for ( int i = 0 ; i < length - 1 ; i++ ) {
							newListeners[i] = listeners[i];
						}
						newListeners[length-1] = (PostInsertEventListener) searchEventListener;
						getEventListeners().setPostInsertEventListeners(newListeners);
					}
				}
				else {
					getEventListeners().setPostInsertEventListeners(
							new PostInsertEventListener[] { (PostInsertEventListener) searchEventListener }
					);
				}
			}
			{
				boolean present = false;
				PostUpdateEventListener[] listeners = getEventListeners().getPostUpdateEventListeners();
				if (listeners != null) {
					for ( Object eventListener : listeners ) {
						//not isAssignableFrom since the user could subclass
						present = present || searchEventListenerClass == eventListener.getClass();
					}
					if (!present) {
						int length = listeners.length + 1;
						PostUpdateEventListener[] newListeners = new PostUpdateEventListener[length];
						for ( int i = 0 ; i < length - 1 ; i++ ) {
							newListeners[i] = listeners[i];
						}
						newListeners[length-1] = (PostUpdateEventListener) searchEventListener;
						getEventListeners().setPostUpdateEventListeners(newListeners);
					}
				}
				else {
					getEventListeners().setPostUpdateEventListeners(
							new PostUpdateEventListener[] { (PostUpdateEventListener) searchEventListener }
					);
				}
			}
			{
				boolean present = false;
				PostDeleteEventListener[] listeners = getEventListeners().getPostDeleteEventListeners();
				if (listeners != null) {
					for ( Object eventListener : listeners ) {
						//not isAssignableFrom since the user could subclass
						present = present || searchEventListenerClass == eventListener.getClass();
					}
					if (!present) {
						int length = listeners.length + 1;
						PostDeleteEventListener[] newListeners = new PostDeleteEventListener[length];
						for ( int i = 0 ; i < length - 1 ; i++ ) {
							newListeners[i] = listeners[i];
						}
						newListeners[length-1] = (PostDeleteEventListener) searchEventListener;
						getEventListeners().setPostDeleteEventListeners(newListeners);
					}
				}
				else {
					getEventListeners().setPostDeleteEventListeners(
							new PostDeleteEventListener[] { (PostDeleteEventListener) searchEventListener }
					);
				}
			}
		}
		return super.buildSessionFactory();
	}

	//not a public API
	public ReflectionManager getReflectionManager() {
		return reflectionManager;
	}
}
