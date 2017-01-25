package animalkeeping.ui;

import animalkeeping.model.Person;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.List;

public class PersonsTable extends TableView<Person> {
    private TableColumn<Person, Number> idCol;
    private TableColumn<Person, String> firstNameCol;
    private TableColumn<Person, String> lastNameCol;
    private TableColumn<Person, String> emailCol;
    private ObservableList<Person> masterList = FXCollections.observableArrayList();
    private FilteredList<Person> filteredList;

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
            List<Person> result = session.createQuery("from Person").list();
            // this.getItems().addAll(result);
            session.getTransaction().commit();
            session.close();
            masterList.addAll(result);
            filteredList = new FilteredList<>(masterList, p -> true);
            SortedList<Person> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(this.comparatorProperty());
            this.setItems(sortedList);

            //result);
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }
    }

    public void setNameFilter(String name) {
        filteredList.setPredicate(person -> {
            // If filter text is empty, display all persons.
            if (name == null || name.isEmpty()) {
                return true;
            }

            // Compare first name and last name of every person with filter text.
            String lowerCaseFilter = name.toLowerCase();

            if (person.getFirstName().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches first name.
            } else if (person.getLastName().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches last name.
            }
            return false; // Does not match.
        });
    }


    public void setIdFilter(Long id) {
        filteredList.setPredicate(person -> id == null || id.equals(person.getId()));
    }
}
