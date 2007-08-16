package org.hibernate.cfg.reveng;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.hibernate.util.StringHelper;

public class DefaultReverseEngineeringStrategy implements ReverseEngineeringStrategy {

	static final private Log log = LogFactory.getLog(DefaultReverseEngineeringStrategy.class);
	
	private static Set AUTO_OPTIMISTICLOCK_COLUMNS;

	private ReverseEngineeringSettings settings = new ReverseEngineeringSettings(this);

	private ReverseEngineeringRuntimeInfo runtimeInfo;
	static {
		AUTO_OPTIMISTICLOCK_COLUMNS = new HashSet();
		AUTO_OPTIMISTICLOCK_COLUMNS.add("version");
		AUTO_OPTIMISTICLOCK_COLUMNS.add("timestamp");
	}
	
		
	public DefaultReverseEngineeringStrategy() {
		super();
	}
	
	public String columnToPropertyName(TableIdentifier table, String columnName) {
		String decapitalize = Introspector.decapitalize( toUpperCamelCase(columnName) );
		
		return keywordCheck( decapitalize );
	}

	private String keywordCheck(String possibleKeyword) {
		if(ReverseEngineeringStrategyUtil.isReservedJavaKeyword(possibleKeyword)) {
			possibleKeyword = possibleKeyword + "_";
		}
		return possibleKeyword;
	}
	
	protected String toUpperCamelCase(String s) {
		return ReverseEngineeringStrategyUtil.toUpperCamelCase(s);
	}
	
	/**
	 * Does some crude english pluralization
	 * TODO: are the from/to names correct ?
	 */
    public String foreignKeyToCollectionName(String keyname, TableIdentifier fromTable, List fromColumns, TableIdentifier referencedTable, List referencedColumns, boolean uniqueReference) {
		String propertyName = Introspector.decapitalize( StringHelper.unqualify( getRoot().tableToClassName(fromTable) ) );
		propertyName = pluralize( propertyName );
		
		if(!uniqueReference) {
        	if(fromColumns!=null && fromColumns.size()==1) {
        		String columnName = ( (Column) fromColumns.get(0) ).getName();
        		propertyName = propertyName + "For" + toUpperCamelCase(columnName);
        	} 
        	else { // composite key or no columns at all safeguard
        		propertyName = propertyName + "For" + toUpperCamelCase(keyname); 
        	}
        }
        return propertyName;
    }

	protected String pluralize(String singular) {
		return ReverseEngineeringStrategyUtil.simplePluralize(singular);
	}

    public String foreignKeyToEntityName(String keyname, TableIdentifier fromTable, List fromColumnNames, TableIdentifier referencedTable, List referencedColumnNames, boolean uniqueReference) {
        String propertyName = Introspector.decapitalize( StringHelper.unqualify( getRoot().tableToClassName(referencedTable) ) );
        
        if(!uniqueReference) {
        	if(fromColumnNames!=null && fromColumnNames.size()==1) {
        		String columnName = ( (Column) fromColumnNames.get(0) ).getName();
        		propertyName = propertyName + "By" + toUpperCamelCase(columnName);
        	} 
        	else { // composite key or no columns at all safeguard
        		propertyName = propertyName + "By" + toUpperCamelCase(keyname); 
        	}
        }
        
        return propertyName;
    }
	
	public String columnToHibernateTypeName(TableIdentifier table, String columnName, int sqlType, int length, int precision, int scale, boolean nullable, boolean generatedIdentifier) {
		String preferredHibernateType = JDBCToHibernateTypeHelper.getPreferredHibernateType(sqlType, length, precision, scale, nullable, generatedIdentifier);
		
		String location = "<no info>";
		if(log.isDebugEnabled()) {
			String info = " t:" + JDBCToHibernateTypeHelper.getJDBCTypeName( sqlType ) + " l:" + length + " p:" + precision + " s:" + scale + " n:" + nullable + " id:" + generatedIdentifier;
			if(table!=null) {
				location = Table.qualify(table.getCatalog(), table.getSchema(), table.getName() ) + "." + columnName + info;
			} else {
				
				location += " Column: " + columnName + info;
			}			
		}
		if(preferredHibernateType==null) {
			log.debug("No default type found for [" + location + "] falling back to [serializable]");
			return "serializable";
		} else {
			log.debug("Default type found for [" + location + "] to [" + preferredHibernateType + "]");		
			return preferredHibernateType;
		}		
	}

	public boolean excludeTable(TableIdentifier ti) {
		if(ti.getName().startsWith("BIN$")) return true; // hard code oracle recycle bin names. Better than requiring users to do it manually. TODO: make it dependent on dialect.
		return false;
	}
	
	public boolean excludeColumn(TableIdentifier identifier, String columnName) {
		return false;
	}

	public String tableToClassName(TableIdentifier tableIdentifier) {
		
		String pkgName = settings.getDefaultPackageName();
		String className = toUpperCamelCase( tableIdentifier.getName() );
		
		if(pkgName.length()>0) {			
			return StringHelper.qualify(pkgName, className);
		}
		else {
			return className;
		}
		
	}

	public List getForeignKeys(TableIdentifier referencedTable) {
		return Collections.EMPTY_LIST;
	}

	public String getTableIdentifierStrategyName(TableIdentifier identifier) {
		return null;
	}

	public Properties getTableIdentifierProperties(TableIdentifier identifier) {
		return null;
	}

	public List getPrimaryKeyColumnNames(TableIdentifier identifier) {
		return null;
	}

	public String classNameToCompositeIdName(String className) {
		return className + "Id"; 
	}

	public void configure(ReverseEngineeringRuntimeInfo rti) {
		this.runtimeInfo = rti;		
	}

	public void close() {
		runtimeInfo = null;
	}

	/** Return explicit which column name should be used for optimistic lock */
	public String getOptimisticLockColumnName(TableIdentifier identifier) {
		return null;
	}

	public boolean useColumnForOptimisticLock(TableIdentifier identifier, String column) {
		if(settings.getDetectOptimsticLock()) {
			return AUTO_OPTIMISTICLOCK_COLUMNS.contains(column.toLowerCase())?true:false;
		} else {
			return false;
		}
	}

	public List getSchemaSelections() {
		return null;
	}

	public String tableToIdentifierPropertyName(TableIdentifier tableIdentifier) {
		return null;
	}

	public String tableToCompositeIdName(TableIdentifier identifier) {
		return null;
	}

	public boolean excludeForeignKeyAsCollection(String keyname, TableIdentifier fromTable, List fromColumns, TableIdentifier referencedTable, List referencedColumns) {
		return !settings.createCollectionForForeignKey();		
	}

	public boolean excludeForeignKeyAsManytoOne(String keyname, TableIdentifier fromTable, List fromColumns, TableIdentifier referencedTable, List referencedColumns) {
		return !settings.createManyToOneForForeignKey();
	}

	public boolean isForeignKeyCollectionInverse(String name, TableIdentifier foreignKeyTable, List columns, TableIdentifier foreignKeyReferencedTable, List referencedColumns) {
		return true;
	}

	public boolean isForeignKeyCollectionLazy(String name, TableIdentifier foreignKeyTable, List columns, TableIdentifier foreignKeyReferencedTable, List referencedColumns) {
		return true;
	}

	public void setSettings(ReverseEngineeringSettings settings) {
		this.settings = settings;		
	}

	public boolean isManyToManyTable(Table table) {
		if(settings.getDetectManyToMany()) {
			Iterator foreignKeyIterator = table.getForeignKeyIterator();
			List foreignKeys = new ArrayList();
			while ( foreignKeyIterator.hasNext() ) {
				ForeignKey fkey = (ForeignKey) foreignKeyIterator.next();
				foreignKeys.add( fkey );
				if(foreignKeys.size()>2) {
					return false; // early exit if we have more than two fk.
				}
			}
			if(foreignKeys.size()!=2) {
				return false;
			}
			
			Set columns = new HashSet();
			Iterator columnIterator = table.getColumnIterator();
			while ( columnIterator.hasNext() ) {
				Column column = (Column) columnIterator.next();
				columns.add(column);
			}
		
			foreignKeyIterator = table.getForeignKeyIterator();
			while ( !columns.isEmpty() && foreignKeyIterator.hasNext() ) {
				ForeignKey element = (ForeignKey) foreignKeyIterator.next();				
				columns.removeAll( element.getColumns() );				
			}
			// what if one of the columns is not the primary key?
			return columns.isEmpty();
		} else {
			return false;
		}
	}

	protected ReverseEngineeringStrategy getRoot() {
		return settings.getRootStrategy();
	}
	
	public String foreignKeyToManyToManyName(ForeignKey fromKey, TableIdentifier middleTable, ForeignKey toKey, boolean uniqueReference) {
		String propertyName = Introspector.decapitalize( StringHelper.unqualify( getRoot().tableToClassName(TableIdentifier.create( toKey.getReferencedTable()) )) );
		propertyName = pluralize( propertyName );
		
		if(!uniqueReference) {
			//TODO: maybe use the middleTable name here ?
        	if(toKey.getColumns()!=null && toKey.getColumns().size()==1) {
        		String columnName = ( (Column) toKey.getColumns().get(0) ).getName();
        		propertyName = propertyName + "For" + toUpperCamelCase(columnName);
        	} 
        	else { // composite key or no columns at all safeguard
        		propertyName = propertyName + "For" + toUpperCamelCase(toKey.getName()); 
        	}
        }
        return propertyName;      
	}

	public Map tableToMetaAttributes(TableIdentifier tableIdentifier) {
		return null;
	}

	public Map columnToMetaAttributes(TableIdentifier identifier, String column) {
		return null;
	}
	
}
