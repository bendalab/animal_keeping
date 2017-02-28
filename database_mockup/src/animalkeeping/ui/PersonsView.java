package animalkeeping.ui;

import animalkeeping.model.Person;
import animalkeeping.model.Treatment;
import animalkeeping.ui.controller.TimelineController;
import animalkeeping.util.AddDatabaseUserDialog;
import animalkeeping.util.SuperUserDialog;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class PersonsView  extends VBox implements Initializable {
    @FXML private ScrollPane tableScrollPane;
    @FXML private VBox timelineVBox;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField idField;
    @FXML private TextField databaseUserField;
    @FXML private TableView<Treatment> treatmentTable;
    private PersonsTable personsTable;
    private TimelineController timeline;
    private TableColumn<Treatment, Number> idCol;
    private TableColumn<Treatment, String> typeCol;
    private TableColumn<Treatment, Date> startDateCol;
    private TableColumn<Treatment, Date> endDateCol;
    private TableColumn<Treatment, String> subjectCol;
    private VBox controls;
    private Person selectedPerson;
    private ControlLabel editLabel, deleteLabel, addUserLabel;


    public PersonsView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/PersonsView.fxml"));
        loader.setController(this);
        try {
            this.getChildren().add(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        personsTable = new PersonsTable();
        timeline = new TimelineController();
        //personsTable.resize();
        this.tableScrollPane.setContent(personsTable);
        this.timelineVBox.getChildren().add(timeline);
        idField.setEditable(false);
        idField.setText("");
        firstNameField.setText("");
        firstNameField.setEditable(false);
        lastNameField.setEditable(false);
        lastNameField.setText("");
        emailField.setEditable(false);
        emailField.setText("");
        databaseUserField.setEditable(false);
        databaseUserField.setText("");

        personsTable.getSelectionModel().getSelectedItems().addListener(new PersonTableListChangeListener());
        idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        subjectCol = new TableColumn<>("subject");
        subjectCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSubject().getName()));
        typeCol = new TableColumn<>("treatment");
        typeCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getType().getName()));
        startDateCol = new TableColumn<>("start");
        startDateCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getStart()));
        endDateCol = new TableColumn<>("end");
        endDateCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getEnd()));
        treatmentTable.getColumns().clear();
        treatmentTable.getColumns().addAll(idCol, subjectCol, typeCol, startDateCol, endDateCol);

        controls = new VBox();
        controls.setAlignment(Pos.TOP_LEFT);
        //controls.setPadding(new Insets(0.0, 0.0, 10.0, 0.0));
        controls.setSpacing(10);

        editLabel = new ControlLabel("edit person", true);
        editLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                editPerson();
            }
        });
        controls.getChildren().add(editLabel);

        deleteLabel = new ControlLabel("delete person", true);
        deleteLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                deletePerson();
            }
        });
        controls.getChildren().add(deleteLabel);

        addUserLabel = new ControlLabel("add as database user", true);
        addUserLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                addUser();
            }
        });
        controls.getChildren().add(addUserLabel);
    }


    private void personSelected(Person p) {
        selectedPerson = p;
        editLabel.setDisable(p == null);
        deleteLabel.setDisable(p == null);
        addUserLabel.setDisable(p == null);

        if (p != null) {
            idField.setText(p.getId().toString());
            firstNameField.setText(p.getFirstName());
            lastNameField.setText(p.getLastName());
            emailField.setText(p.getEmail());
            databaseUserField.setText(p.getDatabaseUser());
            System.out.println(p.getTreatments().size());
            treatmentTable.getItems().clear();
            treatmentTable.getItems().addAll(p.getTreatments());
            timeline.setTreatments(p.getTreatments());
        } else {
            idField.setText("");
            firstNameField.setText("");
            lastNameField.setText("");
            emailField.setText("");
            databaseUserField.setText("");
            treatmentTable.getItems().clear();
            timeline.setTreatments(null);
        }
    }

    private void addUser(){
        Person p = personsTable.getSelectionModel().getSelectedItem();
        Connection c = SuperUserDialog.openConnection();
        AddDatabaseUserDialog.addDatabaseUser(c, p);
    }

    private void editPerson() {
        System.out.println("edit person: " + (selectedPerson != null ? selectedPerson.toString() : ""));
    }


    private void deletePerson() {
        System.out.println("delete person: " + (selectedPerson != null ? selectedPerson.toString() : ""));
    }


    public VBox getControls() {
        return controls;
    }


    public void setSelectedPerson(Integer id) {
        Session session = Main.sessionFactory.openSession();
        List<Person> persons = null;
        try {
            session.beginTransaction();
            persons = session.createQuery("from Person where id = " + id.toString(), Person.class).list();
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }
        if (persons != null && persons.size() > 0) {
            personsTable.getSelectionModel().select(persons.get(0));
        }
    }


    public void nameFilter(String name) {
        this.personsTable.setNameFilter(name);
    }


    public void idFilter(Long id) {
        this.personsTable.setIdFilter(id);
    }


    public void setSelectedPerson(String name) {
        Session session = Main.sessionFactory.openSession();
        List<Person> persons = null;
        try {
            session.beginTransaction();
            persons = session.createQuery("from Person where concat(first, last) like '%" + name + "%'", Person.class).list();
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }
        if (persons != null && persons.size() > 0) {
            personsTable.getSelectionModel().select(persons.get(0));
        }
    }


    private class PersonTableListChangeListener implements ListChangeListener<Person> {

        @Override
        public void onChanged(Change<? extends Person> c) {
            if (c.getList().size() > 0) {
                personSelected(c.getList().get(0));
            }
        }
    }

}