package animalkeeping.util;

import animalkeeping.ui.Main;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import java.util.List;

/**
 * Created by jan on 22.02.17.
 */
public class EntityHelper {


    public static <T> List<T> getEntityList(String query, Class<T> c) {
        List<T> list = null;
        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            list = session.createQuery(query, c).list();
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return list;
    }

    public static <T> boolean deleteEntity(T entity) {
        Session session = Main.sessionFactory.openSession();
        boolean success = true;
        try {
            session.beginTransaction();
            session.delete(entity);
            session.getTransaction().commit();
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            session.close();
        }
        return success;
    }

    public static <T> void refreshEntity(T entity) {
        if (entity == null)
            return;
        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.refresh(entity);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
