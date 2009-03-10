// $Id$
package javax.persistence.metamodel;

/**
 * Instances of the type Bindable represent object or attribute types 
 * that can be bound into the from clause.
 *
 * @param <T>  The type of the represented object or attribute
 */
public interface Bindable<T> {
	
	public static enum BindableType { 
		ATTRIBUTE, COLLECTION, MANAGED_TYPE
	}

    /**
     *  Return the bindable type of the represented object
     *  @return bindable type
     */	
    BindableType getBindableType();
	
    /**
     * Return the Java type of the represented object
     * @return Java type
     */
    Class<T> getJavaType();
}
