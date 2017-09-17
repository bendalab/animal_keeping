package animalkeeping.ui.tables;

import animalkeeping.logging.Communicator;
import animalkeeping.model.Housing;
import animalkeeping.model.HousingUnit;
import animalkeeping.model.Subject;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import animalkeeping.util.TablePreferences;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.util.Collection;
import java.util.Date;

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

public class HousingTable extends TableView<Housing>{
    private MenuItem editItem, deleteItem, moveItem;
    private Subject subject = null;
    private HousingUnit housingUnit = null;
    private boolean showCurrentOnly = true;
    private TablePreferences tableLayout;

    public HousingTable() {
        initTable();
    }

    public HousingTable(HousingUnit unit) {
        this(unit, true);
    }


    public HousingTable(HousingUnit unit, Boolean showAll) {
        this();
        setHousingUnit(unit, showAll);
    }

    public HousingTable(Subject subject) {
        this();
        setSubject(subject);
    }


    private void initTable() {
        TableColumn<Housing, Number> idCol = new TableColumn<Housing, Number>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        idCol.prefWidthProperty().bind(this.widthProperty().multiply(0.05));

        TableColumn<Housing, String> housingUnitNameCol = new TableColumn<Housing, String>("housing unit");
        housingUnitNameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getHousing().getName()));
        housingUnitNameCol.prefWidthProperty().bind(this.widthProperty().multiply(0.16));

        TableColumn<Housing, String> subjectNameCol = new TableColumn<Housing, String>("subject");
        subjectNameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSubject().getName()));
        subjectNameCol.prefWidthProperty().bind(this.widthProperty().multiply(0.16));

        TableColumn<Housing, String> subjectSpeciesCol = new TableColumn<Housing, String>("species");
        subjectSpeciesCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSubject().getSpeciesType().getName()));
        subjectSpeciesCol.prefWidthProperty().bind(this.widthProperty().multiply(0.17));

        TableColumn<Housing, Date> startCol = new TableColumn<Housing, Date>("from");
        startCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Date>(data.getValue().getStart()));
        startCol.prefWidthProperty().bind(this.widthProperty().multiply(0.22));

        TableColumn<Housing, Date> endCol = new TableColumn<Housing, Date>("until");
        endCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Date>(data.getValue().getEnd()));
        endCol.prefWidthProperty().bind(this.widthProperty().multiply(0.22));

        this.getColumns().addAll(idCol, housingUnitNameCol, subjectNameCol, subjectSpeciesCol, startCol, endCol);

        this.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 2) {
                Housing h = getSelectionModel().getSelectedItem();
                h = Dialogs.editHousing(h);
                if (h != null) {
                    refresh();
                    getSelectionModel().select(h);
                }
            }
            event.consume();
        });
        this.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Housing>) c -> {
            int sel_count = c.getList().size();
            editItem.setDisable(sel_count == 0);
            deleteItem.setDisable(sel_count == 0);
            moveItem.setDisable(sel_count == 0 || c.getList().get(0).getEnd() != null);
        });


        ContextMenu cmenu = new ContextMenu();

        editItem = new MenuItem("edit housing entry");
        editItem.setDisable(true);
        editItem.setOnAction(event -> editHousing(this.getSelectionModel().getSelectedItem()));

        deleteItem = new MenuItem("delete housing entry");
        deleteItem.setDisable(true);
        deleteItem.setOnAction(event -> deleteHousing(this.getSelectionModel().getSelectedItem()));

        moveItem = new MenuItem("move subject");
        moveItem.setDisable(true);
        moveItem.setOnAction(event -> moveSubject(this.getSelectionModel().getSelectedItem()));

        cmenu.getItems().addAll(editItem, deleteItem, moveItem);
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


    public void setHousingUnit(HousingUnit unit) {
        setHousingUnit(unit, true);
    }


    public void setHousingUnit(HousingUnit unit, Boolean showCurrentOnly) {
        this.subject = null;
        this.housingUnit = unit;
        this.showCurrentOnly = showCurrentOnly;
        this.refresh();
    }


    public void clear() {
        this.getItems().clear();
    }


    public void setSubject(Subject subject) {
        this.housingUnit = null;
        this.showCurrentOnly = true;
        this.subject = subject;
        this.refresh();
    }


    public void refresh() {
        if (this.subject != null) {
            EntityHelper.refreshEntity(this.subject);
            setHousings(this.subject.getHousings());
        } else if (this.housingUnit != null) {
            EntityHelper.refreshEntity(this.housingUnit);
            if (showCurrentOnly) {
                setHousings(EntityHelper.getEntityList("From Housing where end_datetime is null and type_id = " + housingUnit.getId(), Housing.class));
            } else {
                setHousings(EntityHelper.getEntityList("From Housing where end_datetime is null and type_id = " + housingUnit.getId(), Housing.class));
            }
        } else {
            setHousings(EntityHelper.getEntityList("From Housing where end_datetime is null", Housing.class));
        }
    }

    private void setHousings(Collection<Housing> housings) {
        this.getItems().clear();
        if (housings != null) {
            this.getItems().addAll(housings);
        }
    }

    public void editHousing(Housing housing) {
        Housing h = Dialogs.editHousing(housing);
        if (h != null) {
            refresh();
            getSelectionModel().select(h);
        }
    }

    public void deleteHousing(Housing housing) {
        if (Communicator.pushDelete(housing))
            refresh();
    }

    public void moveSubject(Housing housing) {
        if (housing == null) {
            return;
        }

        Subject sbj = Dialogs.relocateSubjectDialog(housing.getSubject());
        if (sbj != null) {
            refresh();
        }
    }
}
