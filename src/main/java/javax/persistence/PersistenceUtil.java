// $Id$
/*
* JBoss, Home of Professional Open Source
* Copyright 2008, Red Hat Middleware LLC, and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package javax.persistence;

/**
 * @author Hardy Ferentschik
 */

/**
 * Utility interface between the application and the persistence
 * provider(s).
 */
public interface PersistenceUtil {
	/**
	 * Determine the load state of a given persistent attribute
	 * regardless of the persistence provider that created the
	 * containing entity. * @param attributeName name of attribute whose load state is * to be determined
	 *
	 * @return false if entity's state has not been loaded or
	 *         if the attribute state has not been loaded, otherwise true
	 */
	public boolean isLoaded(Object entity, String attributeName);

	/**
	 * Determine the load state of an entity regardless
	 * of the persistence provider that created it.
	 * This method can be used to determine the load state
	 * of an entity passed as a reference. An entity is
	 * considered loaded if all attributes for which FetchType
	 * EAGER has been specified have been loaded.
	 * The isLoaded(Object, String) method should be used to
	 * determine the load state of an attribute.
	 * Not doing so might lead to unintended loading of state.
	 *
	 * @return false if the entity has not be loaded, otherwise
	 *         true.
	 */
	public boolean isLoaded(Object object);
}
