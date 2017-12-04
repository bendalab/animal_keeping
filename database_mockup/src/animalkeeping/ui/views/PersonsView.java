/******************************************************************************
 Copyright (c) 2017 Neuroethology Lab, University of Tuebingen,
 Jan Grewe <jan.grewe@g-node.org>,
 Dennis Huben <dennis.huben@rwth-aachen.de>

 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this list
 of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice, this
 list of conditions and the following disclaimer in the documentation and/or other
 materials provided with the distribution.

 3. Neither the name of the copyright holder nor the names of its contributors may
 be used to endorse or promote products derived from this software without specific
 prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 DAMAGE.

 * Created by jan on 01.05.17.

 *****************************************************************************/
package animalkeeping.ui.views;

import animalkeeping.logging.Communicator;
import animalkeeping.model.Person;
import animalkeeping.model.Treatment;
import animalkeeping.ui.widgets.ControlLabel;
import animalkeeping.ui.Main;
import animalkeeping.ui.widgets.TimelineController;
import animalkeeping.ui.tables.PersonsTable;
import animalkeeping.ui.tables.TreatmentsTable;
import animalkeeping.util.AddDatabaseUserDialog;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import animalkeeping.util.SuperUserDialog;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
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
        //controls.setSpacing(10);

        ControlLabel newLabel = new ControlLabel("new person", "Create a new person entry.", false);
        newLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                newPerson();
            }
        });
        controls.getChildren().add(newLabel);

        editLabel = new ControlLabel("edit person", "Edit person information (restricted usage).",  true);
        editLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                editPerson();
            }
        });
        controls.getChildren().add(editLabel);

        deleteLabel = new ControlLabel("delete person", "Remove the selected Person entry. Only possible " +
                "if not referenced.", true);
        deleteLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                deletePerson();
            }
        });
        controls.getChildren().add(deleteLabel);

        addUserLabel = new ControlLabel("add as database user", "Create a database user for the selected Person.",
                true);
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
            Task<Void> refreshTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Platform.runLater(() -> {
                        List<Treatment> result = EntityHelper.getEntityList("SELECT t FROM Treatment t JOIN FETCH t.subject WHERE t.person.id = " + p.getId(), Treatment.class);
                        treatmentsTable.setTreatments(result);
                        timeline.setTreatments(result);
                    });
                    return null;
                }
            };
            new Thread(refreshTask).start();
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

    public static Tooltip getToolTip(){
        return new Tooltip("Manage the persons dealing with animals in the laboratory.");
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