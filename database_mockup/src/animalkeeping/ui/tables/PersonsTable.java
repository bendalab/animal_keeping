package animalkeeping.ui.tables;

import animalkeeping.logging.Communicator;
import animalkeeping.model.Person;
import animalkeeping.ui.Main;
import animalkeeping.util.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;


import java.sql.Connection;
import java.util.List;
/******************************************************************************
 animalBase
 animalkeeping.util

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

 * Created by jan on 01.01.17.

 *****************************************************************************/

public class PersonsTable extends TableView<Person> {
    private ObservableList<Person> masterList = FXCollections.observableArrayList();
    private FilteredList<Person> filteredList;
    private MenuItem editItem;
    private MenuItem deleteItem;
    private MenuItem addToDBItem;
    private MenuItem toggleActiveItem;
    private CheckMenuItem showAllItem;
    private TablePreferences tableLayout;

    public PersonsTable() {
        super();
        TableColumn<Person, Number> idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        idCol.prefWidthProperty().bind(this.widthProperty().multiply(0.09));

        TableColumn<Person, String> firstNameCol = new TableColumn<>("first name");
        firstNameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getFirstName()));
        firstNameCol.prefWidthProperty().bind(this.widthProperty().multiply(0.20));

        TableColumn<Person, String> lastNameCol = new TableColumn<>("last name");
        lastNameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getLastName()));
        lastNameCol.prefWidthProperty().bind(this.widthProperty().multiply(0.20));

        TableColumn<Person, String> emailCol = new TableColumn<>("email");
        emailCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getEmail()));
        emailCol.prefWidthProperty().bind(this.widthProperty().multiply(0.45));

        TableColumn<Person, Boolean> activeCol = new TableColumn<>("active");
        activeCol.setCellValueFactory(data -> new ReadOnlyBooleanWrapper(data.getValue().getActive()));
        activeCol.setCellFactory(tc -> new CheckBoxTableCell<>());
        activeCol.prefWidthProperty().bind(this.widthProperty().multiply(0.05));
        activeCol.setMaxWidth(70);
        activeCol.setMinWidth(50);

        this.getColumns().addAll(idCol, firstNameCol, lastNameCol, emailCol, activeCol);

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

        this.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Person>) c -> {
            int sel_count = c.getList().size();
            editItem.setDisable(sel_count == 0);
            deleteItem.setDisable(sel_count == 0);
            toggleActiveItem.setDisable(sel_count == 0);
            boolean hasUser = false;
            if (sel_count > 0) {
                Person p = c.getList().get(0);
                hasUser = p.getUser() != null;
            }
            addToDBItem.setDisable(sel_count == 0 && hasUser);
        });
        ContextMenu cmenu = new ContextMenu();
        MenuItem newItem = new MenuItem("new person");
        newItem.setOnAction(event -> editPerson(null));

        editItem = new MenuItem("edit person");
        editItem.setDisable(true);
        editItem.setOnAction(event -> editPerson(this.getSelectionModel().getSelectedItem()));

        deleteItem = new MenuItem("delete person");
        deleteItem.setDisable(true);
        deleteItem.setOnAction(event -> deletePerson(this.getSelectionModel().getSelectedItem()));

        addToDBItem = new MenuItem("add database user to person");
        addToDBItem.setDisable(true);
        addToDBItem.setOnAction(event -> addToDatabase(this.getSelectionModel().getSelectedItem()));

        toggleActiveItem = new MenuItem("toggle active state");
        toggleActiveItem.setDisable(true);
        toggleActiveItem.setOnAction(event -> toggleActive(this.getSelectionModel().getSelectedItem()));

        showAllItem = new CheckMenuItem("show all persons");
        showAllItem.setSelected(!Main.getSettings().getBoolean("app_settings_activePersonView", true));
        showAllItem.setOnAction(event -> setActiveFilter(showAllItem.isSelected()));

        cmenu.getItems().addAll(newItem, editItem, deleteItem, addToDBItem, toggleActiveItem, new SeparatorMenuItem(), showAllItem);
        this.setContextMenu(cmenu);

        this.getColumns().addListener((ListChangeListener) c -> tableLayout.storeLayout());
        this.setTableMenuButtonVisible(true);
        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        for (TableColumn tc : this.getColumns()) {
            tc.visibleProperty().addListener((observable, oldValue, newValue) -> tableLayout.storeLayout());
        }
        this.tableLayout = new TablePreferences(this);
        tableLayout.applyLayout();
    }

    private void init() {
        Task<Void> refreshTask = new Task<Void>() {
            Person p = getSelectionModel().getSelectedItem();
            @Override
            protected Void call() throws Exception {
                List<Person> result = EntityHelper.getEntityList("from Person", Person.class);
                Platform.runLater(() -> {
                    masterList.clear();
                    masterList.addAll(result);
                    filteredList = new FilteredList<>(masterList, p -> true);
                    SortedList<Person> sortedList = new SortedList<>(filteredList);
                    sortedList.comparatorProperty().bind(comparatorProperty());
                    setItems(sortedList);
                    getSelectionModel().select(p);
                    showAllItem.setSelected(!Main.getSettings().getBoolean("app_settings_activePersonView", true));
                    setActiveFilter(showAllItem.isSelected());
                });
                return null;
            }
        };
        new Thread(refreshTask).start();
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
        init();
        super.refresh();
    }

    public void setIdFilter(Long id) {
        filteredList.setPredicate(person -> id == null || id.equals(person.getId()));
    }

    public void setActiveFilter(Boolean showAll) {
        filteredList.setPredicate(person -> showAll || person.getActive());
    }

    public void setSelectedPerson(Person p) {
        this.getSelectionModel().select(p);
    }

    private void editPerson(Person p) {
        Person prsn = Dialogs.editPersonDialog(p);
        if (prsn != null) {
            refresh();
            setSelectedPerson(prsn);
        }
    }

    private void deletePerson(Person p) {
        if (p == null)
            return;
        if (p.getTreatments().size() > 0) {
            Dialogs.showInfo("Cannot delete Person " + p.getLastName() + " since it is referenced by treatments!");
            return;
        }
        Communicator.pushDelete(p);
    }

    private void addToDatabase(Person p) {
        Connection c = SuperUserDialog.openConnection();
        if (c == null) {
            Dialogs.showInfo("Connection refused!");
            return;
        }
        AddDatabaseUserDialog.addDatabaseUser(c, p);
        refresh();
        setSelectedPerson(p);
    }

    private void toggleActive(Person p) {
        p.setActive(!p.getActive());
        Communicator.pushSaveOrUpdate(p);
        refresh();
    }
}
