/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package javax.persistence;

/**
 * Comment
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @version $Revision$
 */
public class PersistenceException extends RuntimeException
{
   public PersistenceException()
   {
   }

   public PersistenceException(String message)
   {
      super(message);
   }

   public PersistenceException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public PersistenceException(Throwable cause)
   {
      super(cause);
   }
}
