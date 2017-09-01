package animalkeeping.ui.tables;

import animalkeeping.logging.Communicator;
import animalkeeping.model.TreatmentType;
import animalkeeping.ui.Main;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import animalkeeping.util.TablePreferences;
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

import java.util.Collection;
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

 * Created by jan on 19.02.17.

 *****************************************************************************/


public class TreatmentTypeTable extends TableView<TreatmentType> {
    private MenuItem editItem, deleteItem;
    private CheckMenuItem showAllItem;
    private ObservableList<TreatmentType> masterList = FXCollections.observableArrayList();
    private FilteredList<TreatmentType> filteredList;
    private TablePreferences tableLayout;


    public TreatmentTypeTable() {
        super();
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        TableColumn<TreatmentType, Number> idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        idCol.prefWidthProperty().bind(this.widthProperty().multiply(0.04));

        TableColumn<TreatmentType, String> nameCol = new TableColumn<>("name");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        nameCol.prefWidthProperty().bind(this.widthProperty().multiply(0.20));

        TableColumn<TreatmentType, String> licenseCol = new TableColumn<>("license");
        licenseCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getLicense() != null ? data.getValue().getLicense().getName() : ""));
        licenseCol.prefWidthProperty().bind(this.widthProperty().multiply(0.20));

        TableColumn<TreatmentType, Boolean> invasiveCol = new TableColumn<>("invasive");
        invasiveCol.setCellValueFactory(data -> new ReadOnlyBooleanWrapper(data.getValue().isInvasive()));
        invasiveCol.setCellFactory(tc -> new CheckBoxTableCell<>());
        invasiveCol.setMinWidth(60.);
        invasiveCol.setMaxWidth(70.);
        invasiveCol.prefWidthProperty().bind(this.widthProperty().multiply(0.07));

        TableColumn<TreatmentType, Boolean> finalCol = new TableColumn<>("final");
        finalCol.setCellValueFactory(data -> new ReadOnlyBooleanWrapper(data.getValue().isFinalExperiment()));
        finalCol.prefWidthProperty().bind(this.widthProperty().multiply(0.07));
        finalCol.setMinWidth(60.);
        finalCol.setMaxWidth(70.);
        finalCol.setCellFactory(tc -> new CheckBoxTableCell<>());

        TableColumn<TreatmentType, String> targetCol = new TableColumn<>("target");
        targetCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTarget().toString()));
        targetCol.prefWidthProperty().bind(this.widthProperty().multiply(0.1));

        TableColumn<TreatmentType, String> descriptionCol = new TableColumn<>("description");
        descriptionCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDescription() != null ?
                data.getValue().getDescription() : ""));
        descriptionCol.prefWidthProperty().bind(this.widthProperty().multiply(0.33));

        this.getColumns().addAll(idCol, nameCol, licenseCol, invasiveCol, finalCol, targetCol, descriptionCol);
        this.setRowFactory( tv -> {
            TableRow<TreatmentType> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    TreatmentType tt = row.getItem();
                    tt = Dialogs.editTreatmentTypeDialog(tt);
                    if (tt != null) {
                        refresh();
                        getSelectionModel().select(tt);
                    }
                }
            });
            return row ;
        });

        this.getSelectionModel().getSelectedItems().addListener((ListChangeListener<TreatmentType>) c -> {
            int sel_count = c.getList().size();
            editItem.setDisable(sel_count == 0);
            deleteItem.setDisable(sel_count == 0);
        });
        ContextMenu cmenu = new ContextMenu();
        MenuItem newItem = new MenuItem("new treatment type");
        newItem.setOnAction(event -> editTreatmentType(null));

        editItem = new MenuItem("edit treatment type");
        editItem.setDisable(true);
        editItem.setOnAction(event -> editTreatmentType(this.getSelectionModel().getSelectedItem()));

        deleteItem = new MenuItem("delete treatment type");
        deleteItem.setDisable(true);
        deleteItem.setOnAction(event -> deleteTreatmentType(this.getSelectionModel().getSelectedItem()));

        showAllItem = new CheckMenuItem("show all types");
        showAllItem.setSelected(!Main.getSettings().getBoolean("app_settings_validTreatmentsView", true));
        showAllItem.setOnAction(event -> setValidFilter(showAllItem.isSelected()));

        cmenu.getItems().addAll(newItem, editItem, deleteItem, new SeparatorMenuItem(), showAllItem);
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

    public TreatmentTypeTable(ObservableList<TreatmentType> items) {
        this();
        this.setItems(items);
    }

    private void init() {
        Task<Void> refreshTask = new Task<Void>() {
            TreatmentType t  = getSelectionModel().getSelectedItem();
            @Override
            protected Void call() throws Exception {
                List<TreatmentType> tts = EntityHelper.getEntityList("from TreatmentType", TreatmentType.class);
                Platform.runLater(() -> {
                    masterList.clear();
                    masterList.addAll(tts);
                    filteredList = new FilteredList<>(masterList, p -> true);
                    SortedList<TreatmentType> sortedList = new SortedList<>(filteredList);
                    sortedList.comparatorProperty().bind(comparatorProperty());
                    setItems(sortedList);
                    getSelectionModel().select(t);
                    showAllItem.setSelected(!Main.getSettings().getBoolean("app_settings_validTreatmentsView", true));
                    setValidFilter(showAllItem.isSelected());
                });
                return null;
            }
        };
        new Thread(refreshTask).start();
    }

    public void setTreatmentTypes(Collection<TreatmentType> types) {
        this.getItems().clear();
        this.getItems().addAll(types);
    }

    @Override
    public void refresh() {
        init();
        super.refresh();
    }

    public void editTreatmentType(TreatmentType type) {
        TreatmentType t = Dialogs.editTreatmentTypeDialog(type);
        if (t != null) {
            refresh();
            getSelectionModel().select(t);
        }
    }

    public void deleteTreatmentType(TreatmentType type) {
        if (type.getTreatments().size() > 0) {
            Dialogs.showInfo("Can not delete treatment type since it is referenced by treatment entries!");
            return;
        }
        Communicator.pushDelete(type);
        refresh();
    }

    public void setValidFilter(Boolean showAll) {
        filteredList.setPredicate(treatmentType -> showAll || treatmentType.isValid());
    }

    /*
    public void setSelectedTreatmentType(TreatmentType treatmentType) {
        System.out.println(this.getItems().contains(treatmentType));
        int index = getItems().indexOf(treatmentType);
        this.getSelectionModel().select(index);
    }
    */
}

