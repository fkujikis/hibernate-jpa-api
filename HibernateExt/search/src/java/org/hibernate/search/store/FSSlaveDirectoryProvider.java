//$Id: $
package org.hibernate.search.store;

import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.File;
import java.io.IOException;

import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.AssertionFailure;
import org.hibernate.search.util.FileHelper;
import org.hibernate.search.util.DirectoryProviderHelper;
import org.hibernate.search.SearchFactory;

/**
 * File based directory provider that takes care of geting a version of the index
 * from a given source
 * The base directory is represented by hibernate.search.<index>.indexBase
 * The index is created in <base directory>/<index name>
 * The source (aka copy) directory is built from <sourceBase>/<index name>
 *
 * A copy is triggered every refresh seconds
 *
 * @author Emmanuel Bernard
 */
public class FSSlaveDirectoryProvider implements DirectoryProvider<FSDirectory> {
	private static Log log = LogFactory.getLog( FSSlaveDirectoryProvider.class );
	private FSDirectory directory1;
	private FSDirectory directory2;
	private int current;
	private String indexName;
	private Timer timer;

	public void initialize(String directoryProviderName, Properties properties, SearchFactory searchFactory) {
		//source guessing
		String source = DirectoryProviderHelper.getSourceDirectory( "sourceBase", "source", directoryProviderName, properties );
		if (source == null)
			throw new IllegalStateException("FSSlaveDirectoryProvider requires a viable source directory");
		if ( ! new File(source, "current1").exists() && ! new File(source, "current2").exists() ) {
			throw new IllegalStateException("No current marker in source directory");
		}
		log.debug( "Source directory: " + source );
		File indexDir = DirectoryProviderHelper.determineIndexDir( directoryProviderName, properties );
		log.debug( "Index directory: " + indexDir.getPath() );
		String refreshPeriod = properties.getProperty( "refresh", "3600" );
		long period = Long.parseLong( refreshPeriod );
		log.debug("Refresh period " + period + " seconds");
		period *= 1000; //per second
		try {
			boolean create = !indexDir.exists();
			indexName = indexDir.getCanonicalPath();
			if (create) {
				indexDir.mkdir();
				log.debug("Initializing index directory " + indexName);
			}

			File subDir = new File( indexName, "1" );
			create = ! subDir.exists();
			directory1 = FSDirectory.getDirectory( subDir.getCanonicalPath(), create );
			if ( create ) {
				IndexWriter iw = new IndexWriter( directory1, new StandardAnalyzer(), create );
				iw.close();
			}

			subDir = new File( indexName, "2" );
			create = ! subDir.exists();
			directory2 = FSDirectory.getDirectory( subDir.getCanonicalPath(), create );
			if ( create ) {
				IndexWriter iw = new IndexWriter( directory2, new StandardAnalyzer(), create );
				iw.close();
			}
			File currentMarker = new File(indexName, "current1");
			File current2Marker = new File(indexName, "current2");
			if ( currentMarker.exists() ) {
				current = 1;
			}
			else if ( current2Marker.exists() ) {
				current = 2;
			}
			else {
				//no default
				log.debug( "Setting directory 1 as current");
				current = 1;
				File sourceFile = new File(source);
				File destinationFile = new File(indexName, Integer.valueOf(current).toString() );
				int sourceCurrent;
				if ( new File(sourceFile, "current1").exists() ) {
					sourceCurrent = 1;
				}
				else if ( new File(sourceFile, "current2").exists() ) {
					sourceCurrent = 2;
				}
				else {
					throw new AssertionFailure("No current file marker found in source directory: " + source);
				}
				try {
					FileHelper.synchronize( new File(sourceFile, String.valueOf(sourceCurrent) ), destinationFile, true);
				}
				catch (IOException e) {
					throw new HibernateException("Umable to synchonize directory: " + indexName, e);
				}
				if (! currentMarker.createNewFile() ) {
					throw new HibernateException("Unable to create the directory marker file: " + indexName);
				}
			}
			log.debug( "Current directory: " + current);
		}
		catch (IOException e) {
			throw new HibernateException( "Unable to initialize index: " + directoryProviderName, e );
		}
		timer = new Timer();
		TimerTask task = new TriggerTask(source, indexName);
		timer.scheduleAtFixedRate( task, period, period );
	}

	public FSDirectory getDirectory() {
		if (current == 1) {
			return directory1;
		}
		else if (current == 2) {
			return directory2;
		}
		else {
			throw new AssertionFailure("Illegal current directory: " + current);
		}
	}

	@Override
	public boolean equals(Object obj) {
		// this code is actually broken since the value change after initialize call
		// but from a practical POV this is fine since we only call this method
		// after initialize call
		if ( obj == this ) return true;
		if ( obj == null || !( obj instanceof FSSlaveDirectoryProvider ) ) return false;
		return indexName.equals( ( (FSSlaveDirectoryProvider) obj ).indexName );
	}

	@Override
	public int hashCode() {
		// this code is actually broken since the value change after initialize call
		// but from a practical POV this is fine since we only call this method
		// after initialize call
		int hash = 11;
		return 37 * hash + indexName.hashCode();
	}

	class TriggerTask extends TimerTask {

		private ExecutorService executor;
		private CopyDirectory copyTask;

		public TriggerTask(String source, String destination) {
			executor = Executors.newSingleThreadExecutor();
			copyTask = new CopyDirectory( source, destination  );
		}

		public void run() {
			if (!copyTask.inProgress) {
				executor.execute( copyTask );
			}
			else {
				log.trace( "Skipping directory synchronization, previous work still in progress: " + indexName);
			}
		}
	}

	class CopyDirectory implements Runnable {
		private String source;
		private String destination;
		private volatile boolean inProgress;

		public CopyDirectory(String source, String destination) {
			this.source = source;
			this.destination = destination;
		}

		public void run() {
			long start = System.currentTimeMillis();
			try {
				inProgress = true;
				int oldIndex = current;
				int index = current == 1 ? 2 : 1;
				File sourceFile;
				if ( new File( source, "current1" ).exists() ) {
					sourceFile = new File(source, "1");
				}
				else if ( new File( source, "current2" ).exists() ) {
					sourceFile = new File(source, "2");
				}
				else {
					log.error("Unable to determine current in source directory");
					inProgress = false;
					return;
				}

				File destinationFile = new File(destination, Integer.valueOf(index).toString() );
				//TODO make smart a parameter
				try {
					log.trace("Copying " + sourceFile + " into " + destinationFile);
					FileHelper.synchronize( sourceFile, destinationFile, true);
					current = index;
				}
				catch (IOException e) {
					//don't change current
					log.error( "Unable to synchronize " + indexName, e);
					inProgress = false;
					return;
				}
				if ( ! new File(indexName, "current" + oldIndex).delete() ) {
					log.warn( "Unable to remove previous marker file in " + indexName );
				}
				try {
					new File(indexName, "current" + index).createNewFile();
				}
				catch( IOException e ) {
					log.warn( "Unable to create current marker file in " + indexName, e );
				}
			}
			finally {
				inProgress = false;
			}
			log.trace( "Copy for " + indexName + " took " + (System.currentTimeMillis() - start) + " ms");
		}
	}

	public void finalize() throws Throwable {
		super.finalize();
		timer.cancel();
		//TODO find a better cycle from Hibernate core
	}
}
