/*
 * Created on 13-Feb-2005
 *
 */
package org.hibernate.tool.ant;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.hbm2x.Exporter;

/**
 * @author max
 *
 */
public class Hbm2DDLExporterTask extends ExporterTask {

	boolean exportToDatabase = true; 
	boolean scriptToConsole = true;
	boolean schemaUpdate = false;
	String delimiter = ";"; 
	boolean drop = false;
	boolean create = true;
	boolean format = false;
	
	String outputfileName = null;
	private boolean haltOnError = false;
	
	public Hbm2DDLExporterTask(HibernateToolTask parent) {
		super(parent);
	}
	
	public void execute() {
		if(schemaUpdate) {
			SchemaUpdate update = new SchemaUpdate(parent.getConfiguration() );
			update.execute(scriptToConsole, exportToDatabase);			
		} 
		else {
			SchemaExport export = new SchemaExport(parent.getConfiguration() );
			if(outputfileName!=null) {
				export.setOutputFile(new File(getDestdir(),outputfileName).toString() );
			}
			if(delimiter!=null) {
				export.setDelimiter(delimiter);
			}
			export.setHaltOnError(haltOnError);
			export.setFormat(format);
			if(drop && create) {
				export.create(scriptToConsole, exportToDatabase);
			} else {
				export.execute(scriptToConsole, exportToDatabase, drop, create);
			}
			
			if(export.getExceptions().size()>0) {
				Iterator iterator = export.getExceptions().iterator();
				int cnt=1;
				parent.log(export.getExceptions().size() + " errors occurred while performing <hbm2ddl>.", Project.MSG_WARN);
				while ( iterator.hasNext() ) {
					Throwable throwable = (Throwable) iterator.next();
					parent.log("Error #" + cnt + ": " + throwable.toString(), Project.MSG_WARN);
					StringWriter sw = new StringWriter();
					throwable.printStackTrace(new PrintWriter(sw));
					parent.log(sw.getBuffer().toString(), Project.MSG_VERBOSE);
					
				}
				if(haltOnError) {
					throw new BuildException("Errors while performing <hbm2ddl>");				
				}
			}
		}
	}
	
	public void setExport(boolean export) {
		exportToDatabase = export;
	}
	
	/**
	 * Run SchemaUpdate instead of SchemaExport
	 */
	public void setUpdate(boolean update) {
		this.schemaUpdate = update;
	}
	
	/**
	 * Output sql to console ? (default true)
	 */
	public void setConsole(boolean console) {
		this.scriptToConsole = console;
	}
	
	/**
	 * Format the generated sql
	 */
	public void setFormat(boolean format) {
		this.format = format;
	}
	
	/**
	 * File out put name (default: empty) 
	 */
	public void setOutputFileName(String fileName) {
		outputfileName = fileName;
	}

	public void setDrop(boolean drop) {
		this.drop = drop;
	}
	
	public void setCreate(boolean create) {
		this.create = create;
	}
	
	public String getName() {
		return "hbm2ddl (Generates database schema)";
	}
	
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	
	public String getDelimiter() {
		return delimiter;
	}
	
	public void setHaltonerror(boolean haltOnError) {
		this.haltOnError  = haltOnError;
	}

	protected Exporter createExporter() {
		throw new IllegalStateException("Should not call create exporter on hbm2ddl");
	}
}
