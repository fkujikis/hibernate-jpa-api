// $Id$
package javax.persistence.metamodel;

/**
 * A member of a Java type
 *
 * @param <X> The represented type that contains the member
 * @param <Y> The type of the represented member
 */
public interface Member<X, Y> {
    //String getName(); //TODO: do we need this? 
					// the java.lang.reflect.Member has it

    /**
     *  Return the managed type representing the type in which 
     *  the member was declared.
     *  @return declaring type
     */
    ManagedType<X> getDeclaringType();

    /**
     *  Return the Java type of the represented member.
     *  @return Java type
     */
    Class<Y> getMemberJavaType();

    /**
     *  Return the java.lang.reflect.Member for the represented 
     *  member.
     *  @return corresponding java.lang.reflect.Member
     */
    java.lang.reflect.Member getJavaMember();

    /**
     *  Is the member an association
     *  @return whether an association
     */
    boolean isAssociation();

    /**
     *  Is the member collection-valued
     *  @return whether a collection
     */
    boolean isCollection();
	
    //TODO: fetch type
}
