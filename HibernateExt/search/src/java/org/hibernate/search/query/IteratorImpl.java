//$Id: $
package org.hibernate.search.query;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.hibernate.Session;

/**
 * @author Emmanuel Bernard
 */
//TODO load the next batch-size elements to benefit from batch-size 
public class IteratorImpl implements Iterator {

	private final List<EntityInfo> entityInfos;
	private final Session session;
	private int index = 0;
	private final int size;
   private Object next;
   private int nextObjectIndex = -1;

   public IteratorImpl(List<EntityInfo> entityInfos, Session session) {
		this.entityInfos = entityInfos;
		this.session = session;
		this.size = entityInfos.size();
	}

   //side effect is to set up next
   public boolean hasNext() {
      if (nextObjectIndex == index) return next != null;
      next = null;
      nextObjectIndex = -1;
      do {
         if ( index >= size ) {
            nextObjectIndex = index;
            next = null;
            return false;
         }
         next = session.get( entityInfos.get( index ).clazz, entityInfos.get( index ).id );
         if (next == null) {
            index++;
         }
         else {
            nextObjectIndex = index;
         }
      }
      while( next == null );
      return true;
   }

	public Object next() {
      //hasNext() has side effect
      if ( ! hasNext() ) throw new NoSuchElementException("Out of boundaries");
      index++;
      return next;
	}

	public void remove() {
		//TODO this is theorically doable
		throw new UnsupportedOperationException( "Cannot remove from a lucene query iterator" );
	}
}
