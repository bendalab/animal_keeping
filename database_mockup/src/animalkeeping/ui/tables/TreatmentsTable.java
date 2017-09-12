package animalkeeping.ui.tables;

import animalkeeping.logging.Communicator;
import animalkeeping.model.Housing;
import animalkeeping.model.Subject;
import animalkeeping.model.Treatment;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import animalkeeping.util.TablePreferences;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
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

public class TreatmentsTable extends TableView<Treatment>{
    private ObservableList<Treatment> masterList = FXCollections.observableArrayList();
    private FilteredList<Treatment> filteredList;
    private MenuItem editItem;
    private MenuItem deleteItem;
    private MenuItem endItem;
    private CheckMenuItem openOnlyItem;
    private Subject subject = null;
    private boolean openOnly = false;
    private boolean manual = false;
    private TablePreferences tableLayout;

    public TreatmentsTable() {
        super();
        initUI();
        //init();
    }

    public TreatmentsTable(boolean openOnly) {
        super();
        this.openOnly = openOnly;
        initUI();
        //init();
        //setOpenFilter(openOnly);
    }

    private void initUI() {
        TableColumn<Treatment, Number> idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        idCol.prefWidthProperty().bind(this.widthProperty().multiply(0.05));

        TableColumn<Treatment, String> subjectCol = new TableColumn<>("subject");
        subjectCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSubject().getName()));
        subjectCol.prefWidthProperty().bind(this.widthProperty().multiply(0.13));

        TableColumn<Treatment, String> personCol = new TableColumn<>("by person");
        personCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPerson().getFirstName() +
                " " + data.getValue().getPerson().getLastName()));
        personCol.prefWidthProperty().bind(this.widthProperty().multiply(0.13));

        TableColumn<Treatment, String> treatmentCol = new TableColumn<>("treatment");
        treatmentCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTreatmentType().getName()));
        treatmentCol.prefWidthProperty().bind(this.widthProperty().multiply(0.17));

        TableColumn<Treatment, Date> startCol = new TableColumn<>("start");
        startCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getStart()));
        startCol.prefWidthProperty().bind(this.widthProperty().multiply(0.19));

        TableColumn<Treatment, Date> endCol = new TableColumn<>("end");
        endCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getEnd()));
        endCol.prefWidthProperty().bind(this.widthProperty().multiply(0.19));

        TableColumn<Treatment, Boolean> finalCol = new TableColumn<>("final");
        finalCol.setCellValueFactory(data -> new ReadOnlyBooleanWrapper(data.getValue().getTreatmentType().isFinalExperiment()));
        finalCol.setCellFactory( tc -> new CheckBoxTableCell<>());
        finalCol.prefWidthProperty().bind(this.widthProperty().multiply(0.05));
        finalCol.setMinWidth(60.);
        finalCol.setMaxWidth(70.);

        TableColumn<Treatment, Boolean> invasiveCol = new TableColumn<>("invasive");
        invasiveCol.setCellValueFactory(data -> new ReadOnlyBooleanWrapper(data.getValue().getTreatmentType().isInvasive()));
        invasiveCol.setCellFactory( tc -> new CheckBoxTableCell<>());
        invasiveCol.prefWidthProperty().bind(this.widthProperty().multiply(0.05));
        invasiveCol.setMinWidth(60.);
        invasiveCol.setMaxWidth(70.);

        this.getColumns().addAll(idCol, subjectCol, personCol, treatmentCol, startCol, endCol, invasiveCol, finalCol);
        this.setRowFactory( tv -> {
            TableRow<Treatment> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Treatment t = row.getItem();
                    t = Dialogs.editTreatmentDialog(t);
                    if (t != null) {
                        refresh();
                        setSelectedTreatment(t);
                    }
                }
            });
            return row ;
        });
        this.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Treatment>) c -> {
            int sel_count = c.getList().size();
            editItem.setDisable(sel_count == 0);
            deleteItem.setDisable(sel_count == 0);
            endItem.setDisable(sel_count == 0 || c.getList().get(0).getEnd() != null);
        });

        ContextMenu cmenu = new ContextMenu();
        MenuItem newItem = new MenuItem("new treatment");
        newItem.setOnAction(event -> editTreatment(null));

        editItem = new MenuItem("edit treatment");
        editItem.setDisable(true);
        editItem.setOnAction(event -> editTreatment(this.getSelectionModel().getSelectedItem()));

        deleteItem = new MenuItem("delete treatment");
        deleteItem.setDisable(true);
        deleteItem.setOnAction(event -> deleteTreatment(this.getSelectionModel().getSelectedItem()));

        endItem = new MenuItem("end treatment");
        endItem.setDisable(true);
        endItem.setOnAction(event -> endTreatment(this.getSelectionModel().getSelectedItem()));

        openOnlyItem  = new CheckMenuItem("show open treatments only");
        openOnlyItem.setSelected(this.openOnly);
        openOnlyItem.setOnAction(event -> setOpenFilter(openOnlyItem.isSelected()));

        cmenu.getItems().addAll(newItem, editItem, deleteItem, endItem, openOnlyItem);
        this.setContextMenu(cmenu);
        filteredList = new FilteredList<>(masterList, p -> true);
        SortedList<Treatment> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(this.comparatorProperty());
        this.setItems(sortedList);

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
        if (!manual) {
            Task<Void> refreshTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    List<Treatment> treatments;
                    if (subject == null) {
                        treatments = EntityHelper.getEntityList("from Treatment", Treatment.class);
                    } else {
                        treatments = new ArrayList<>(subject.getTreatments());
                    }

                    Platform.runLater(() -> {
                        masterList.clear();
                        masterList.addAll(treatments);
                        setOpenFilter(openOnly);
                    });
                    return null;
                }
            };
            new Thread(refreshTask).start();
        }
    }

    public void setTreatments(Collection<Treatment> treatments) {
        this.manual = true;
        masterList.clear();
        if (treatments != null) {
            masterList.addAll(treatments);
        }
    }

    public void setSubject(Subject s) {
        this.subject = s;
        refresh();
    }

    public void refresh() {
        init();
        super.refresh();
    }

    public void setOpenFilter(boolean set) {
        this.openOnly = set;
        if (set) {
            filteredList.setPredicate(treatment -> treatment.getEnd() == null);
        } else {
            filteredList.setPredicate(null);
        }
    }

    public void setSelectedTreatment(Treatment treatment) {
        this.getSelectionModel().select(treatment);
    }

    public void editTreatment(Treatment t) {
        Treatment treatment = Dialogs.editTreatmentDialog(t);
        if(treatment != null) {
            this.refresh();
            this.setSelectedTreatment(treatment);
        }
    }

    public void deleteTreatment(Treatment treatment) {
        if (treatment == null) {
            return;
        }
        Communicator.pushDelete(treatment);
        if (this.manual) {
            masterList.remove(treatment);
        }
        this.refresh();
    }

    public void endTreatment(Treatment t) {
        if (t == null)
            return;
        Pair<Date, Date> interval = Dialogs.getDateTimeInterval(t.getStart(), new Date());
        if (interval != null) {
            t.setStart(interval.getKey());
            t.setEnd(interval.getValue());
            Communicator.pushSaveOrUpdate(t);
            if (t.getTreatmentType().isInvasive()) {
                Housing h = t.getSubject().getCurrentHousing();
                h.setEnd(interval.getValue());
                Communicator.pushSaveOrUpdate(h);
            }
            refresh();
        }
    }
}
