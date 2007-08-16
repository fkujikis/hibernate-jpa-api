/*
* JBoss, Home of Professional Open Source
* Copyright 2005, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package javax.persistence.spi;

import java.security.ProtectionDomain;
import java.lang.instrument.IllegalClassFormatException;

/**
 * A persistence provider provides an instance of this interface
 * to the PersistenceUnitInfo.addTransformer method.
 * The supplied transformer instance will get called to transform
 * entity class files when they are loaded and redefined.  The transformation
 * occurs before the class is defined by the JVM
 *
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @version $Revision$
 */
public interface ClassTransformer
{
   /**
    * Invoked when a class is being loaded or redefined to add hooks for persistence bytecode manipulation
    *
    * @param loader the defining class loaderof the class being transformed.  It may be null if using bootstrap loader
    * @param classname The name of the class being transformed
    * @param classBeingRedefined If an already loaded class is being redefined, then pass this as a parameter
    * @param protectionDomain ProtectionDomain of the class being (re)-defined
    * @param classfileBuffer The input byte buffer in class file format
    * @return A well-formed class file that can be loaded
    *
    * @throws IllegalClassFormatException
    */
   byte[] transform(ClassLoader loader,
                    String classname,
                    Class<?> classBeingRedefined,
                    ProtectionDomain protectionDomain,
                    byte[] classfileBuffer)
   throws IllegalClassFormatException;
}
