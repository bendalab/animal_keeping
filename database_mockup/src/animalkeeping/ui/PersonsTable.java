package animalkeeping.ui;

import animalkeeping.model.Person;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.List;

public class PersonsTable extends TableView {
    private TableColumn<Person, Number> idCol;
    private TableColumn<Person, String> firstNameCol;
    private TableColumn<Person, String> lastNameCol;
    private TableColumn<Person, String> emailCol;

    public PersonsTable() {
        super();
        idCol = new TableColumn<Person, Number>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        firstNameCol = new TableColumn<Person, String>("first name");
        firstNameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getFirstName()));
        lastNameCol = new TableColumn<Person, String>("last name");
        lastNameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getLastName()));
        emailCol= new TableColumn<Person, String>("email");
        emailCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getEmail()));
        this.getColumns().addAll(idCol, firstNameCol, lastNameCol, emailCol);
        init();
    }

   /* public PersonsTable(ObservableList<Person> items) {
        this();
        this.setItems(items);
    }*/

    private void init() {

        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            List result = session.createQuery("from Person").list();

            this.getItems().addAll(result);
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
