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
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }
        return list;
    }



}
