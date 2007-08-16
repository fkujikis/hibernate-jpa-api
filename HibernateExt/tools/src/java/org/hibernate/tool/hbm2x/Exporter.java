/*
 * Created on 2004-12-01
 */
package org.hibernate.tool.hbm2x;

import java.io.File;
import java.util.Properties;

import org.hibernate.cfg.Configuration;

/**
 * @author max and david
 *
 */
public interface Exporter {
		
	/** 
	 * @param cfg An Hibernate {@link org.hibernate.Configuration} or subclass instance that defines the hibernate meta model to be exported.
	 */
	public void setConfiguration(Configuration cfg);

	/**
	 * @param file basedirectory to be used for generated files.
	 */
	public void setOutputDirectory(File file);

	/**
	 * @param templatePath array of directories used sequentially to lookup templates
	 */
	public void setTemplatePath(String[] templatePath);
	
	/**
	 * @param templatePrefix template prefix to be used. e.g. if set to "dao/", "dao/" will be prefixed all lookups before the simple name will looked up. Used to allow seperation of templates within a templatepath.  
	 */
//	public void setTemplatePrefix(String templatePrefix);

	/**
	 * 
	 * @param properties set of properties to be used by exporter.
	 */
	public void setProperties(Properties properties);
	
	
	/**
	 * 
	 * @param collector Instance to be consulted when adding a new file.
	 */
	public void setArtifactCollector(ArtifactCollector collector);
	
	/**
	 * 
	 * @return artifact collector
	 */
	public ArtifactCollector getArtifactCollector();
	
	/**
	 * Called when exporter should start generating its output
	 */
	public void start();
	
}
