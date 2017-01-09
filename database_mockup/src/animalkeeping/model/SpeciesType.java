package animalkeeping.model;

import animalkeeping.ui.Main;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.List;

/**
 * Created by jan on 27.12.16.
 */
public class SpeciesType {
    private Long id;
    private String name;
    private String trivial;
    private Integer count;

    public SpeciesType() {
    }

    public SpeciesType(String name, String trivial) {
        this.name = name;
        this.trivial = trivial;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTrivial() {
        return trivial;
    }

    public void setTrivial(String trivial) {
        this.trivial = trivial;
    }

    public Integer getCount() {
        if(true)
        return getCountInit();

        else
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getCountInit() {
        Session session = Main.sessionFactory.openSession();
        int numberAlive = 0;
        try {
            session.beginTransaction();

            List<Subject> result = session.createQuery("from Subject where species_id = " + this.getId().toString()).list();


            for (Subject currentSubject : result) {
                Long index = currentSubject.getId();
                //System.out.println(index);

                List<Housing> houseList = session.createQuery("from Housing where subject_id = " + index.toString()).list();
                Boolean alive = new Boolean(false);
                for (Housing house : houseList) {
                    if (house.getEnd() == null)
                        alive = true;
                }

                if(alive)
                    numberAlive += 1;
                }



            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }
        this.count = numberAlive;
        return  numberAlive;
    }



    @Override
    public String toString() {
        return "SpeciesType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", trivial='" + trivial + '\'' +
                '}';
    }
}

