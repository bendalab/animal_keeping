package animalkeeping.ui;

import animalkeeping.model.Person;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;


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
        idCol.prefWidthProperty().bind(this.widthProperty().multiply(0.09));

        firstNameCol = new TableColumn<Person, String>("first name");
        firstNameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getFirstName()));
        firstNameCol.prefWidthProperty().bind(this.widthProperty().multiply(0.20));

        lastNameCol = new TableColumn<Person, String>("last name");
        lastNameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getLastName()));
        lastNameCol.prefWidthProperty().bind(this.widthProperty().multiply(0.20));

        emailCol= new TableColumn<Person, String>("email");
        emailCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getEmail()));
        emailCol.prefWidthProperty().bind(this.widthProperty().multiply(0.50));

        this.getColumns().addAll(idCol, firstNameCol, lastNameCol, emailCol);
        this.setRowFactory( tv -> {
            TableRow<Person> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Person p = row.getItem();
                    p = Dialogs.editPersonDialog(p);
                    if (p != null) {
                        refresh();
                        setSelectedPerson(p);
                    }
                }
            });
            return row ;
        });
        init();
    }

   /* public PersonsTable(ObservableList<Person> items) {
        this();
        this.setItems(items);
    }*/

    private void init() {
        List<Person> result = EntityHelper.getEntityList("from Person", Person.class);
        masterList.addAll(result);
        filteredList = new FilteredList<>(masterList, p -> true);
        SortedList<Person> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(this.comparatorProperty());
        this.setItems(sortedList);
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


    public void refresh() {
        masterList.clear();
        masterList.addAll(EntityHelper.getEntityList("from Person", Person.class));
        super.refresh();
    }

    public void setIdFilter(Long id) {
        filteredList.setPredicate(person -> id == null || id.equals(person.getId()));
    }

    public void setSelectedPerson(Person p) {
        this.getSelectionModel().select(p);
    }
}
