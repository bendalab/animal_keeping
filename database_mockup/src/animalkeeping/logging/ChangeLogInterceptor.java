package animalkeeping.logging;

import animalkeeping.*;
import org.hibernate.CallbackException;
import org.hibernate.Session;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by huben on 30.01.17.
 */
public class ChangeLogInterceptor {


Session session;
private Set inserts = new HashSet();
private Set updates = new HashSet();
private Set deletes = new HashSet();

public void setSession(Session session) {
    this.session=session;
}

public boolean onSave(Object entity,Serializable id,
                      Object[] state,String[] propertyNames,Type[] types)
        throws CallbackException {

    System.out.println("onSave");

    if (entity instanceof ChangeLog){
        inserts.add(entity);
    }
    return false;

}

public boolean onFlushDirty(Object entity,Serializable id,
                            Object[] currentState,Object[] previousState,
                            String[] propertyNames,Type[] types)
        throws CallbackException {

    System.out.println("onFlushDirty");

    if (entity instanceof ChangeLog){
        updates.add(entity);
    }
    return false;

}

public void onDelete(Object entity, Serializable id,
                     Object[] state, String[] propertyNames,
                     Type[] types) {

    System.out.println("onDelete");

    if (entity instanceof ChangeLog){
        deletes.add(entity);
    }
}

//called before commit into database
public void preFlush(Iterator iterator) {
    System.out.println("preFlush");
}

//called after committed into database
public void postFlush(Iterator iterator) {
    System.out.println("postFlush");

    try{

        for (Iterator it = inserts.iterator(); it.hasNext();) {
            ChangeLogInterface entity = (ChangeLogInterface) it.next();
            System.out.println("postFlush - insert");
            ChangeLogUtil.LogIt("Saved",entity);
        }

        for (Iterator it = updates.iterator(); it.hasNext();) {
            ChangeLogInterface entity = (ChangeLogInterface) it.next();
            System.out.println("postFlush - update");
            ChangeLogUtil.LogIt("Updated",entity);
        }

        for (Iterator it = deletes.iterator(); it.hasNext();) {
            ChangeLogInterface entity = (ChangeLogInterface) it.next();
            System.out.println("postFlush - delete");
            ChangeLogUtil.LogIt("Deleted",entity);
        }

    } finally {
        inserts.clear();
        updates.clear();
        deletes.clear();
    }
}
}

