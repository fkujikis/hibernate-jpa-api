//$Id: Lob.java 11171 2007-02-08 03:40:51Z epbernard $
//EJB3 Specification Copyright 2004-2006 Sun Microsystems, Inc.
package javax.persistence;

import static java.lang.annotation.ElementType.METHOD;

import static java.lang.annotation.ElementType.FIELD;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;

/**
 * Specifies that a persistent property or field should be persisted as a large object to a
 * database-supported large object type. The Lob annotation may be used in conjunction with
 * the Basic annotation. A Lob may be either a binary or character type.
 *
 * The Lob type is inferred from the type of the persistent field or property, and except
 * for string and character-based types defaults to Blob.
 *  
 * @author Emmanuel Bernard
 */
@Target({METHOD, FIELD}) @Retention(RUNTIME)
public @interface Lob {}