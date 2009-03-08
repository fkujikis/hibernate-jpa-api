package javax.persistence.metamodel;

/**
 * Instances of the type Attribute represents persistent 
 * non-collection-valued properties or fields.
 *
 * @param <X> The represented type containing the attribute
 * @param <T> The type of the represented attribute
 */
public interface Attribute<X, T> 
		extends Member<X, T>, Bindable<T> {
	
	public static enum Multiplicity {
		MANY_TO_ONE, ONE_TO_ONE, EMBEDDED, BASIC
	}
	
    /**
     *  Return the multiplicity of the attribute.
     *  @return multiplicity
     */
    Multiplicity getMultiplicity();

    /**
     *  Is the attribute an id attribute.
     *  @return boolean indicating whether or not an id
     */
    boolean isId();

    /**
     *  Is the attribute a version attribute.
     *  @return boolean indicating whether or not a version attribute
     */
    boolean isVersion();

    /** 
     *  Can the attribute be null.
     *  @return boolean indicating whether or not the attribute can
     * 				be null
     */
    boolean isOptional();

    /**
     * Return the type that represents the type of the attribute.
     * @return type of attribute
     */
    Type<T> getAttributeType();
}
