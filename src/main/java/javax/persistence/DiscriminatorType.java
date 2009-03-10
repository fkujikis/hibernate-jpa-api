//$Id$
package javax.persistence;

/**
 * Defines supported types of the discriminator column
 *
 * @author Emmanuel Bernard
 */
public enum DiscriminatorType {
	/**
	 * String as the discriminator type
	 */
	STRING,
	/**
	 * Single character as the discriminator type
	 */
	CHAR,
	/**
	 * Integer as the discriminator type
	 */
	INTEGER
}
