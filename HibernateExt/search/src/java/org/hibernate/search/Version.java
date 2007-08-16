//$Id: $
package org.hibernate.search;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Emmanuel Bernard
 */
public class Version {
	public static final String VERSION = "3.2.2.beta1";
	private static Log log = LogFactory.getLog( Version.class );

	static {
		log.info( "Hibernate Search " + VERSION );
	}

	public static void touch() {
	}
}