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
        this.count = initCount();
    }

    public SpeciesType(String name, String trivial) {
        this.name = name;
        this.trivial = trivial;
        this.count = initCount();
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
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer initCount() {
        Session session2 = Main.sessionFactory.openSession();
        int numberAlive = 0;
        try {
            session2.beginTransaction();

            List<Subject> result = session2.createQuery("from Subject").list();


            for (Subject currentSubject : result) {

                Long index = currentSubject.getId();
                System.out.println(index);

                List<Housing> houseList = session2.createQuery("from Housing where subject_id = " + index.toString()).list();
                Boolean alive = new Boolean(false);
                for (Housing house : houseList) {
                    if (house.getEnd() == null)
                        alive = true;
                }
                if (!alive) {
                    System.out.println("The fish is deceased");
                } else {
                    System.out.println(houseList);
                    numberAlive += 1;
                }


            }
            session2.getTransaction().commit();
            session2.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session2.isOpen()) {
                session2.close();
            }
        }
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

