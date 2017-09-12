package animalkeeping.ui.views;

import animalkeeping.logging.Communicator;
import animalkeeping.model.Gender;
import animalkeeping.model.License;
import animalkeeping.model.Quota;
import animalkeeping.ui.Main;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import animalkeeping.util.TablePreferences;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
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

public class QuotaView extends VBox implements Initializable {
    @FXML TableView<Quota> quotaTable;
    @FXML private TableColumn<Quota, String> idCol, speciesCol;
    @FXML private TableColumn<Quota, Number> numberCol;
    @FXML private TableColumn<Quota, Number> usedCol;
    @FXML private TableColumn<Quota, Double> progressCol;
    @FXML private TableColumn<Quota, String> genderCol;
    private MenuItem newItem, editItem, deleteItem;
    License license = null;
    private TablePreferences tableLayout;


    public QuotaView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/animalkeeping/ui/fxml/QuotaTable.fxml"));
        loader.setController(this);
        try {
            this.getChildren().add(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getId().toString()));
        idCol.prefWidthProperty().bind(this.widthProperty().multiply(0.1));

        speciesCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSpeciesType().getName()));
        speciesCol.prefWidthProperty().bind(this.widthProperty().multiply(0.2));

        genderCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getGender().toString()));
        genderCol.prefWidthProperty().bind(this.widthProperty().multiply(0.1));

        numberCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getNumber() != null ? data.getValue().getNumber() : 0));
        numberCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        usedCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getUsed() != null ? data.getValue().getUsed() : 0));
        usedCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        progressCol.setCellValueFactory(param -> {
            if (param.getValue() == null || param.getValue().getNumber() == null || param.getValue().getNumber() <= 0) {
                return  new ReadOnlyObjectWrapper<>(0.0);
            }
            return new ReadOnlyObjectWrapper<>(param.getValue().getAvailableFraction());
        });
        progressCol.setCellFactory(ProgressBarTableCell.forTableColumn());
        progressCol.prefWidthProperty().bind(this.widthProperty().multiply(0.29));

        quotaTable.prefWidthProperty().bind(this.widthProperty());
        quotaTable.setRowFactory( tv -> {
            TableRow<Quota> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Quota q = row.getItem();
                    q = Dialogs.editQuotaDialog(q);
                    if (q != null) {
                        refresh();
                        setSelectedQuota(q);
                    }
                }
            });
            return row ;
        });

        this.quotaTable.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Quota>() {
            @Override
            public void onChanged(Change<? extends Quota> c) {
                int sel_count = c.getList().size();
                editItem.setDisable(sel_count == 0);
                deleteItem.setDisable(sel_count == 0);
            }
        });

        ContextMenu cmenu = new ContextMenu();
        newItem = new MenuItem("new quota");
        newItem.setOnAction(event -> editQuota(null));

        editItem = new MenuItem("edit quota");
        editItem.setDisable(true);
        editItem.setOnAction(event -> editQuota(quotaTable.getSelectionModel().getSelectedItem()));

        deleteItem = new MenuItem("delete quota");
        deleteItem.setDisable(true);
        deleteItem.setOnAction(event -> deleteQuota(quotaTable.getSelectionModel().getSelectedItem()));
        cmenu.getItems().addAll(newItem, editItem, deleteItem);
        this.quotaTable.setContextMenu(cmenu);

        quotaTable.getColumns().addListener((ListChangeListener) c -> tableLayout.storeLayout());
        quotaTable.setTableMenuButtonVisible(true);
        quotaTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        for (TableColumn tc : quotaTable.getColumns()) {
            tc.visibleProperty().addListener((observable, oldValue, newValue) -> tableLayout.storeLayout());
        }
        this.tableLayout = new TablePreferences(quotaTable);
        tableLayout.applyLayout();
    }

    public void setQuota(Collection<Quota> quota) {
        quotaTable.getItems().clear();
        quotaTable.getItems().addAll(quota);
    }

    public Boolean removeItem(Quota q) {
        return quotaTable.getItems().remove(q);
    }

    public Quota getSelectedItem() {
        Quota q = null;
        if (!quotaTable.getSelectionModel().isEmpty()) {
            q = quotaTable.getSelectionModel().getSelectedItem();
        }
        return q;
    }

    public void setLicense(License license) {
        this.setSelectedQuota(null);
        this.license = license;
        quotaTable.getItems().clear();
        quotaTable.getSelectionModel().clearSelection();
        if (license != null) {
            EntityHelper.refreshEntity(license);
            setQuota(license.getQuotas());
        }
        quotaTable.refresh();
    }

    public void refresh() {
        this.setLicense(this.license);
    }

    public void setSelectedQuota(Quota q) {
        if (q != null)
            quotaTable.getSelectionModel().select(q);
        else
            quotaTable.getSelectionModel().clearSelection();
    }

    private void editQuota(Quota q) {
        Quota quota;
        if (q == null)
            quota = Dialogs.editQuotaDialog(license);
        else
            quota = Dialogs.editQuotaDialog(q);
        if (quota != null) {
            setLicense(this.license);
            setSelectedQuota(quota);
        }
    }

    private void deleteQuota(Quota q) {
        if (q.getUsed() > 0) {
            Dialogs.showInfo("Cannot delete Quota " + q.getSpeciesType().getName().substring(0, 20) + " since it is referenced by treatments!");
            return;
        }
        Communicator.pushDelete(q);
        refresh();
    }


}
