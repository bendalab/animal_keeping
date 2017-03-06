package animalkeeping.logging;

import animalkeeping.model.Entity;
import animalkeeping.ui.Main;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 * Created by huben on 06.03.17.
 */
public abstract class Communicator {
    public static void pushSaveOrUpdate(Entity E){
        ChangeLogInterceptor interceptorX = new ChangeLogInterceptor();
        Session session = Main.sessionFactory.withOptions().interceptor(interceptorX).openSession();
        interceptorX.setSession(session);
        try {
            session.beginTransaction();
            session.saveOrUpdate(E);

            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }

    }

    public static void pushDelete(Entity E){
        ChangeLogInterceptor interceptorX = new ChangeLogInterceptor();
        Session session = Main.sessionFactory.withOptions().interceptor(interceptorX).openSession();
        interceptorX.setSession(session);
        try {
            session.beginTransaction();
            session.delete(E);

            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }

    }


}
