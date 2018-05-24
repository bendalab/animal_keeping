package animalkeeping.ui.tables;

import animalkeeping.logging.Communicator;
import animalkeeping.model.Subject;
import animalkeeping.ui.Main;
import animalkeeping.ui.views.ViewEvent;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import animalkeeping.util.TablePreferences;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

public class SubjectsTable extends TableView<SubjectsTable.SubjectTableItem> {
    private final ObservableList<SubjectTableItem> masterList = FXCollections.observableArrayList();
    private FilteredList<SubjectTableItem> filteredList;
    private MenuItem editItem;
    private MenuItem deleteItem;
    private MenuItem addTreatmentItem;
    private MenuItem reportDeadItem;
    private MenuItem observationItem;
    private MenuItem moveItem;
    private CheckMenuItem showAllItem;
    private TablePreferences tableLayout;
    private SimpleBooleanProperty refreshRunning = new SimpleBooleanProperty(false);
    private final Session session = Main.sessionFactory.openSession();

    public SubjectsTable() {
        super();
        TableColumn<SubjectTableItem, Number> idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        idCol.prefWidthProperty().bind(this.widthProperty().multiply(0.03));

        TableColumn<SubjectTableItem, String> nameCol = new TableColumn<>("name");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        nameCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        TableColumn<SubjectTableItem, String> aliasCol = new TableColumn<>("alias");
        aliasCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getAlias()));
        aliasCol.prefWidthProperty().bind(this.widthProperty().multiply(0.08));

        TableColumn<SubjectTableItem, String> personCol = new TableColumn<>("resp. person");
        personCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getResponsiblePerson()));
        personCol.prefWidthProperty().bind(this.widthProperty().multiply(0.08));

        TableColumn<SubjectTableItem, String> genderCol = new TableColumn<>("gender");
        genderCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getGender()));
        genderCol.prefWidthProperty().bind(this.widthProperty().multiply(0.1));

        TableColumn<SubjectTableItem, String> birthdateCol = new TableColumn<>("birth date");
        birthdateCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getBirth()));
        birthdateCol.prefWidthProperty().bind(this.widthProperty().multiply(0.08));

        TableColumn<SubjectTableItem, String> speciesCol = new TableColumn<>("species");
        speciesCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSpecies()));
        speciesCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        TableColumn<SubjectTableItem, String> housingCol = new TableColumn<>("housing");
        housingCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getCurrentHousingUnit()));
        housingCol.prefWidthProperty().bind(this.widthProperty().multiply(0.1));

        TableColumn<SubjectTableItem, String> subjectCol = new TableColumn<>("type");
        subjectCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSubjectType()));
        subjectCol.prefWidthProperty().bind(this.widthProperty().multiply(0.08));

        TableColumn<SubjectTableItem, String> supplierCol = new TableColumn<>("supplier");
        supplierCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSupplierName()));
        supplierCol.prefWidthProperty().bind(this.widthProperty().multiply(0.125));

        this.getColumns().addAll(idCol, nameCol, aliasCol, birthdateCol, genderCol, speciesCol, personCol, housingCol, subjectCol, supplierCol);
        this.setRowFactory( tv -> {
            TableRow<SubjectTableItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    //Subject s = row.getItem();
                    //s = Dialogs.editSubjectDialog(s);
                    //if (s != null) {
                    //    refresh();
                    //    setSelectedSubject(s);
                    //} FIXME
                }
            });
            return row ;
        });

        this.getSelectionModel().getSelectedItems().addListener((ListChangeListener<SubjectTableItem>) c -> {
            int sel_count = c.getList().size();
            editItem.setDisable(sel_count == 0);
            deleteItem.setDisable(sel_count == 0);
            addTreatmentItem.setDisable(sel_count == 0);
            observationItem.setDisable(sel_count == 0);
            if (sel_count > 0) {
                //Subject s = c.getList().get(0);
                //boolean alive = s.getCurrentHousing() != null;
                //moveItem.setDisable(!alive);
                //addTreatmentItem.setDisable(!alive);
                //reportDeadItem.setDisable(!alive); FIXME
            }
        });

        ContextMenu cmenu = new ContextMenu();
        MenuItem newItem = new MenuItem("new subject");
        newItem.setOnAction(event -> editSubject(null));

        editItem = new MenuItem("edit subject");
        editItem.setDisable(true);
        //editItem.setOnAction(event -> editSubject(this.getSelectionModel().getSelectedItem()));

        deleteItem = new MenuItem("delete subject");
        deleteItem.setDisable(true);
        //deleteItem.setOnAction(event -> deleteSubject(this.getSelectionModel().getSelectedItem()));

        addTreatmentItem = new MenuItem("add treatment");
        addTreatmentItem.setDisable(true);
        //addTreatmentItem.setOnAction(event -> addTreatment(this.getSelectionModel().getSelectedItem()));

        observationItem = new MenuItem("add observation");
        observationItem.setDisable(true);
        //observationItem.setOnAction(event -> addObservation(this.getSelectionModel().getSelectedItem()));

        reportDeadItem = new MenuItem("report subject dead");
        reportDeadItem.setDisable(true);
        //reportDeadItem.setOnAction(event -> reportSubjectDead(this.getSelectionModel().getSelectedItem()));

        moveItem = new MenuItem("move subject");
        moveItem.setDisable(true);
        //moveItem.setOnAction(event -> moveSubject(this.getSelectionModel().getSelectedItem()));

        showAllItem = new CheckMenuItem("show also past subjects");
        showAllItem.setSelected(!Main.getSettings().getBoolean("app_settings_availableSubjectsView", true));
        showAllItem.setOnAction(event -> setAliveFilter(!showAllItem.isSelected()));

        cmenu.getItems().addAll(newItem, editItem, deleteItem, new SeparatorMenuItem(), addTreatmentItem, observationItem,
                new SeparatorMenuItem(), reportDeadItem, moveItem, new SeparatorMenuItem(), showAllItem);
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

    public Long getSelectedSubjectId() {
        if (! getSelectionModel().isEmpty()) {
            return getSelectionModel().getSelectedItem().getId();
        }
        return null;
    }


    public void setAliveFilter(Boolean set) {
        if (set) {
            filteredList.setPredicate(subject -> !subject.getCurrentHousingUnit().isEmpty());
        } else {
            filteredList.setPredicate(null);
        }
    }

    public void setNameFilter(String name) {
        filteredList.setPredicate(subject -> {
            if (name == null || name.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = name.toLowerCase();
            if (subject.getName().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else if (subject.getAlias() != null && subject.getAlias().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }
            return false;
        });
    }


    public void refresh() {
        if (refreshRunning.get()) {
            return;
        }
        fireEvent(new ViewEvent(ViewEvent.REFRESHING));

        masterList.clear();
        filteredList = new FilteredList<>(masterList, p -> true);
        SortedList<SubjectTableItem> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(comparatorProperty());
        setItems(sortedList);
        showAllItem.setSelected(!Main.getSettings().getBoolean("app_settings_availableSubjectsView", true));
        setAliveFilter(!showAllItem.isSelected());

        RefreshTask refresh_task = new RefreshTask();
        refresh_task.setOnSucceeded(event -> {
            fireEvent(new ViewEvent(ViewEvent.REFRESHED));
        });

        Platform.runLater(() -> {
            refreshRunning.unbind();
            refreshRunning.bind(refresh_task.runningProperty());
        });

        new Thread(refresh_task).start();
    }


    public void setSelectedSubject(Subject s) {
        //this.getSelectionModel().select(s); FIXME
    }


    public void setIdFilter(Long id) {
        filteredList.setPredicate(subject -> id == null || id.equals(subject.getId()));
    }


    public void editSubject(Subject s) {
        Subject sbct = Dialogs.editSubjectDialog(s);
        if (sbct != null) {
            System.out.println("Subject is not null, refreshing");
            refresh();
            setSelectedSubject(s);
        }
     }

    public void deleteSubject(Subject s) {
        if (!s.getTreatments().isEmpty()) {
            Dialogs.showInfo("Cannot delete subject " + s.getName() + " since it is referenced by " +
                    Integer.toString(s.getTreatments().size()) + " treatment entries! Delete them first.");
        } else {
            Communicator.pushDelete(s);
            refresh();
            setSelectedSubject(null);
        }
    }

    public void addTreatment(Subject s) {
        Dialogs.editTreatmentDialog(s);
        refresh();
        setSelectedSubject(s);
    }

    public void addObservation(Subject s) {
        Dialogs.editSubjectNoteDialog(s);

    }

    public void reportSubjectDead(Subject s) {
        if (s == null) {
            return;
        }
        Subject subject = Dialogs.reportSubjectDead(s);
        if (subject != null) {
            refresh();
            setSelectedSubject(subject);
        }
    }

    public void moveSubject(Subject s) {
        if (s == null) {
            return;
        }
        Subject subject = Dialogs.relocateSubjectDialog(s);
        if (subject != null) {
            refresh();
            setSelectedSubject(subject);
        }
    }


    class RefreshTask extends Task<Void> {

        RefreshTask() {
            super();
        }

        @Override
        protected Void call() throws Exception {
            Session session = Main.sessionFactory.openSession();
            Query countQuery = session.createQuery("SELECT COUNT(*) from Subject s");
            Number maxCount = (Number)countQuery.getSingleResult();
            Query idQuery = session.createQuery("SELECT MAX(s.id) from Subject s");
            Number max_id = (Number)idQuery.getSingleResult();
            int batchSize = 50, min_id = 0;
            Query<Subject> q = session.createQuery("FROM Subject s WHERE s.id > :min_id AND s.id <= :max_id", Subject.class);
            q.setReadOnly(true);
            q.setCacheable(false);
            int no_fetched = 0;
            for (int i=0; i < max_id.intValue() / batchSize; i++) {
                min_id = i * batchSize;
                q.setParameter("min_id", (long)min_id);
                q.setParameter("max_id", (long)(min_id + batchSize));

                List<Subject> ls = q.getResultList();

                for (Subject s : ls) {
                    no_fetched++;
                    final SubjectTableItem si = new SubjectTableItem(s);
                    Platform.runLater(() -> masterList.add(si));
                    updateProgress(no_fetched, maxCount.intValue());
                }
            }

            session.close();
            return null;
        }
    }

    public class SubjectTableItem {
        private String currentHousingUnit;
        private String supplierName;
        private String responsiblePerson;
        private String species;
        private String name;
        private Long id;
        private String alias;
        private String subjectType;
        private String birth;
        private String gender;
        private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD");

        SubjectTableItem(Subject s) {

            this.alias = s.getAlias();
            this.name = s.getName();
            this.birth = s.getBirthday() != null ? sdf.format(s.getBirthday()) : "";
            this.supplierName = s.getSupplier().getName();
            this.species = s.getSpeciesType().getName();
            this.subjectType = s.getType();
            this.id = s.getId();
            this.responsiblePerson = s.getResponsiblePerson() != null ?
                    (s.getResponsiblePerson().getFirstName() + " " +
                    s.getResponsiblePerson().getLastName()) : "";
            this.currentHousingUnit = s.getCurrentHousing() != null ? s.getCurrentHousing().getHousing().getName() : "";
            this.gender = s.getGender().toString();
        }

        String getCurrentHousingUnit() {
            return currentHousingUnit;
        }

        String getSupplierName() {
            return supplierName;
        }

        String getResponsiblePerson() {
            return responsiblePerson;
        }

        public String getSpecies() {
            return species;
        }

        public String getName() {
            return name;
        }

        public Long getId() {
            return id;
        }

        public String getAlias() {
            return alias;
        }

        String getSubjectType() {
            return subjectType;
        }

        String getBirth() {
            return birth;
        }

        public String getGender() {
            return gender;
        }
    }
}

