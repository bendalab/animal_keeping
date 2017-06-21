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
package animalkeeping.ui.tables;

import animalkeeping.logging.Communicator;
import animalkeeping.model.HousingUnit;
import animalkeeping.ui.views.ViewEvent;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.List;

import static animalkeeping.util.Dialogs.showInfo;

public class HousingUnitTable extends TreeTableView<HousingUnit> {
    private MenuItem editItem, deleteItem, appendItem;
    private TreeItem<HousingUnit> root = new TreeItem<>();

    public HousingUnitTable () {
        //super();
        initialize();
    }

    public void initialize() {
        TreeTableColumn<HousingUnit, String> unitsColumn = new TreeTableColumn<>("housing unit");
        unitsColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("Name"));
        unitsColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.25));

        TreeTableColumn<HousingUnit, String> typeColumn = new TreeTableColumn<>("type");
        typeColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("HousingType"));
        typeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HousingUnit, String> hu) ->
                new ReadOnlyStringWrapper(hu.getValue().getValue() != null ? hu.getValue().getValue().getHousingType().getName() : ""));
        typeColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.2));

        TreeTableColumn<HousingUnit, String> dimensionColumn = new TreeTableColumn<>("dimension");
        dimensionColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("Dimensions"));
        dimensionColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        TreeTableColumn<HousingUnit, String> descriptionColumn = new TreeTableColumn<>("description");
        descriptionColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("Description"));
        descriptionColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.25));

        TreeTableColumn<HousingUnit, Number> populationColumn = new TreeTableColumn<>("population");
        populationColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("AllHousings"));
        populationColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        this.getColumns().addAll(unitsColumn, typeColumn, dimensionColumn, descriptionColumn, populationColumn);
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 2) {
                HousingUnit hu = getSelectionModel().getSelectedItem().getValue();
                hu = Dialogs.editHousingUnitDialog(hu);
                if (hu != null) {
                    refresh();
                    getSelectionModel().select(new TreeItem<>(hu));
                }
            }
            event.consume();
        });
        this.getSelectionModel().getSelectedItems().addListener((ListChangeListener<TreeItem<HousingUnit>>) c -> {
            int sel_count = c.getList().size();
            for (MenuItem item : getContextMenu().getItems()) {
                item.setDisable(sel_count == 0);
            }
        });

        ContextMenu cmenu = new ContextMenu();
        MenuItem newItem = new MenuItem("new housing unit");
        newItem.setOnAction(event -> editHousingUnit(null));

        editItem = new MenuItem("edit housing unit");
        editItem.setDisable(true);
        editItem.setOnAction(event -> editHousingUnit(getSelectionModel().getSelectedItem().getValue()));

        deleteItem = new MenuItem("delete housing unit");
        deleteItem.setDisable(true);
        deleteItem.setOnAction(event -> deleteHousingUnit(getSelectionModel().getSelectedItem().getValue()));

        appendItem = new MenuItem("append housing unit");
        appendItem.setDisable(true);
        appendItem.setOnAction(event -> appendHousingUnit(getSelectionModel().getSelectedItem().getValue()));

        cmenu.getItems().addAll(newItem, appendItem, editItem, deleteItem);
        setContextMenu(cmenu);
        this.setRoot(root);
        this.setShowRoot(false);
    }

    @Override
    public void refresh() {
        root.getChildren().clear();
        HashMap<String, Boolean> folding = getFoldingState();
        TreeItem<HousingUnit> seletedItem = getSelectionModel().getSelectedItem();
        Task<Void> refresh_task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<HousingUnit> housingUnits = EntityHelper.getEntityList("from HousingUnit where parentUnit is null", HousingUnit.class);
                for (HousingUnit hu : housingUnits) {
                    TreeItem<HousingUnit> child = new TreeItem<>(hu);
                    root.getChildren().add(child);
                    fillRecursive(hu, child);
                }
                return  null;
            }
        };
        refresh_task.setOnSucceeded(event -> {
            setFoldingState(folding);
            getSelectionModel().select(seletedItem);
            fireEvent(new ViewEvent(ViewEvent.REFRESHED));
        });
        new Thread(refresh_task).run();
    }

    private HashMap<String, Boolean> getFoldingState() {
        HashMap<String, Boolean> states = new HashMap<>();
        int row = 0;
        while (true) {
            TreeItem<HousingUnit> item = getTreeItem(row);
            if (item == null) {
                break;
            } else {
                states.put(item.getValue().getName(), item.isExpanded());
            }
            row++;
        }
        return states;
    }


    private void setFoldingState(HashMap<String, Boolean> state) {
        int row = 0;
        while (true) {
            TreeItem<HousingUnit> item = getTreeItem(row);
            if (item == null) {
                break;
            } else {
                if (state.containsKey(item.getValue().getName())) {
                    item.setExpanded(state.get(item.getValue().getName()));
                }
            }
            row++;
        }
    }


    private void fillRecursive(HousingUnit unit, TreeItem<HousingUnit> item) {
        for (HousingUnit hu : unit.getChildHousingUnits()) {
            TreeItem<HousingUnit> it = new TreeItem<>(hu);
            item.getChildren().add(it);
            fillRecursive(hu, it);
        }
    }

    public HousingUnit getSelectedUnit() {
        if (this.getSelectionModel().isEmpty()) {
            return null;
        }
        return this.getSelectionModel().getSelectedItem().getValue();
    }

    public void editHousingUnit(HousingUnit unit) {
        HousingUnit hu = Dialogs.editHousingUnitDialog(unit);
        if (hu != null) {
            refresh();
            getSelectionModel().select(new TreeItem<>(hu));
        }
    }

    public void deleteHousingUnit(HousingUnit unit) {
        if (unit == null)
            return;
        if (!unit.getHousings().isEmpty() || !unit.getChildHousingUnits().isEmpty()) {
            showInfo("Cannot delete housing unit " + unit.getName() + " since there are referring housing entries or child housing units!");
        } else {
            Communicator.pushDelete(unit);
        }
        getSelectionModel().select(null);
        refresh();
    }

    public void appendHousingUnit(HousingUnit parent) {
        HousingUnit hu = Dialogs.editHousingUnitDialog(null, parent);
        if (hu != null) {
            refresh();
            getSelectionModel().select(new TreeItem<>(hu));
        }
    }

}
