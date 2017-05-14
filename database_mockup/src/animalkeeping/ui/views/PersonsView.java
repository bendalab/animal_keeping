package animalkeeping.ui.views;

import animalkeeping.logging.Communicator;
import animalkeeping.model.Person;
import animalkeeping.ui.widgets.ControlLabel;
import animalkeeping.ui.Main;
import animalkeeping.ui.widgets.TimelineController;
import animalkeeping.ui.tables.PersonsTable;
import animalkeeping.ui.tables.TreatmentsTable;
import animalkeeping.util.AddDatabaseUserDialog;
import animalkeeping.util.Dialogs;
import animalkeeping.util.SuperUserDialog;
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
import java.util.List;
import java.util.ResourceBundle;

public class PersonsView  extends AbstractView implements Initializable {
    @FXML private ScrollPane tableScrollPane;
    @FXML private VBox timelineVBox;
    @FXML private Label firstnameLabel;
    @FXML private Label lastnameLabel;
    @FXML private Label emailLabel;
    @FXML private Label idLabel;
    @FXML private Label usernameLabel;
    @FXML private Label userroleLabel;
    @FXML private VBox treatmentTableBox;

    private PersonsTable personsTable;
    private TreatmentsTable treatmentsTable;
    private TimelineController timeline;
    private VBox controls;
    private Person selectedPerson;
    private ControlLabel editLabel;
    private ControlLabel deleteLabel;
    private ControlLabel addUserLabel;


    public PersonsView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/animalkeeping/ui/fxml/PersonsView.fxml"));
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
        treatmentsTable = new TreatmentsTable();
        if (treatmentTableBox != null)
            treatmentTableBox.getChildren().add(treatmentsTable);
        timeline = new TimelineController();
        this.tableScrollPane.setContent(personsTable);
        this.tableScrollPane.prefHeightProperty().bind(this.heightProperty());
        this.tableScrollPane.prefWidthProperty().bind(this.widthProperty());
        this.timelineVBox.getChildren().add(timeline);
        idLabel.setText("");
        firstnameLabel.setText("");
        lastnameLabel.setText("");
        emailLabel.setText("");
        usernameLabel.setText("");
        userroleLabel.setText("");
        personsTable.getSelectionModel().getSelectedItems().addListener(new PersonTableListChangeListener());
        personsTable.prefHeightProperty().bind(tableScrollPane.heightProperty());

        controls = new VBox();
        controls.setAlignment(Pos.TOP_LEFT);
        controls.setSpacing(10);

        ControlLabel newLabel = new ControlLabel("new person", false);
        newLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                newPerson();
            }
        });
        controls.getChildren().add(newLabel);

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
        addUserLabel.setDisable(p != null && p.getUser() != null);

        if (p != null) {
            idLabel.setText(p.getId().toString());
            firstnameLabel.setText(p.getFirstName());
            lastnameLabel.setText(p.getLastName());
            emailLabel.setText(p.getEmail());
            usernameLabel.setText(p.getUser() != null ? p.getUser().getName() : "");
            userroleLabel.setText(p.getUser() != null ? p.getUser().getType().getName() : "");
            treatmentsTable.setTreatments(p.getTreatments());
            timeline.setTreatments(p.getTreatments());
        } else {
            idLabel.setText("");
            firstnameLabel.setText("");
            lastnameLabel.setText("");
            emailLabel.setText("");
            usernameLabel.setText("");
            treatmentsTable.setTreatments(null);
            userroleLabel.setText("");
            timeline.setTreatments(null);
        }
    }

    private void addUser(){
        Person p = personsTable.getSelectionModel().getSelectedItem();
        Connection c = SuperUserDialog.openConnection();
        if (c == null) {
            Dialogs.showInfo("Connection refused!");
            return;
        }
        AddDatabaseUserDialog.addDatabaseUser(c, p);
        personsTable.refresh();
    }


    private void newPerson() {
        Person p = Dialogs.editPersonDialog(null);
        personsTable.refresh();
        personsTable.setSelectedPerson(p);
    }


    private void editPerson() {
        Dialogs.editPersonDialog(selectedPerson);
        personsTable.refresh();

    }


    private void deletePerson() {
         if (selectedPerson != null) {
             Communicator.pushDelete(selectedPerson);
         }
    }


    @Override
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


    @Override
    public void refresh() {
        personsTable.refresh();
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