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

import animalkeeping.model.HousingType;
import animalkeeping.model.HousingUnit;
import animalkeeping.ui.*;
import animalkeeping.ui.widgets.PopulationChart;
import animalkeeping.ui.tables.HousingTable;
import animalkeeping.ui.tables.HousingTypeTable;
import animalkeeping.ui.tables.HousingUnitTable;
import animalkeeping.ui.widgets.ControlLabel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import org.hibernate.Session;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static animalkeeping.util.Dialogs.*;


public class HousingView extends AbstractView implements Initializable {
    @FXML ScrollPane tableScrollPane;
    @FXML private Tab populationTab, historyTab, currentHousingTab;
    @FXML private TabPane plotTabPane;
    @FXML private ScrollPane typesScrollPane;
    @FXML private SplitPane unitsSplit;
    @FXML private VBox tabVBox;

    private PopulationChart populationChart;
    private HousingTypeTable housingTypes;
    private HousingUnitTable housingUnitTable;
    private HousingTable housingTable;
    private VBox controls;
    private ControlLabel editUnitLabel, deleteUnitLabel, appendUnitLabel;
    private ControlLabel editTypeLabel, deleteTypeLabel;
    private ControlLabel importSubjectsLabel, batchTreatmentLabel;


    public HousingView () {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/animalkeeping/ui/fxml/HousingView.fxml"));
        loader.setController(this);
        try {
            this.getChildren().add(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.setFillWidth(true);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        housingUnitTable = new HousingUnitTable();
        housingTable = new HousingTable();

        tableScrollPane.setContent(housingUnitTable);
        tabVBox.prefHeightProperty().bind(unitsSplit.prefHeightProperty().subtract(unitsSplit.getDividers().get(0).positionProperty().multiply(unitsSplit.getPrefHeight())));
        housingUnitTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> setSelectedUnit(newSelection != null ? newSelection.getValue() : null));
        housingUnitTable.prefWidthProperty().bind(this.prefWidthProperty().multiply(0.95));
        plotTabPane.prefWidthProperty().bind(this.prefWidthProperty().multiply(0.95));
        plotTabPane.prefHeightProperty().bind(tabVBox.prefHeightProperty());
        populationChart = new PopulationChart();
        populationChart.prefHeightProperty().bind(plotTabPane.prefHeightProperty().multiply(0.8));
        populationChart.prefWidthProperty().bind(this.prefWidthProperty().multiply(0.95));
        populationTab.setContent(populationChart);
        housingTable.prefWidthProperty().bind(plotTabPane.maxWidthProperty());
        currentHousingTab.setContent(housingTable);
        unitsSplit.prefHeightProperty().bind(this.prefHeightProperty().multiply(0.6));
        unitsSplit.prefWidthProperty().bind(this.prefWidthProperty());

        typesScrollPane.setContent(null);
        typesScrollPane.prefHeightProperty().bind(this.prefHeightProperty().multiply(0.3));
        housingTypes = new HousingTypeTable();
        housingTypes.prefWidthProperty().bind(this.prefWidthProperty());
        housingTypes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        housingTypes.getSelectionModel().selectedItemProperty().addListener((ov, old_val, new_val) -> setSelectedType(ov != null ? ov.getValue() : null));
        typesScrollPane.setContent(housingTypes);

        controls = new VBox();
        controls.setAlignment(Pos.TOP_LEFT);
        ControlLabel newUnitLabel = new ControlLabel("new housing unit", "Create new housing unit.", false);
        newUnitLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                newHousingUnit();
            }
        });
        controls.getChildren().add(newUnitLabel);

        appendUnitLabel = new ControlLabel("append housing unit", "Append a new housing unit to the selected one.", true);
        appendUnitLabel.setDisable(true);
        appendUnitLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                housingUnitTable.appendHousingUnit(housingUnitTable.getSelectedUnit());
            }
        });
        controls.getChildren().add(appendUnitLabel);

        editUnitLabel = new ControlLabel("edit housing unit", "Edit the information of the selected Housing unit.", true);
        editUnitLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                editHousingUnit();
            }
        });
        controls.getChildren().add(editUnitLabel);

        deleteUnitLabel = new ControlLabel("delete housing unit", "Remove the selected Housing unit (only possible if not referenced).", true);
        deleteUnitLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                deleteHousingUnit();
            }
        });
        controls.getChildren().add(deleteUnitLabel);

        controls.getChildren().add(new Separator(Orientation.HORIZONTAL));

        ControlLabel newTypeLabel = new ControlLabel("new housing type", "Create new type of Housing unit.", false);
        newTypeLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                newHousingType();
            }
        });
        controls.getChildren().add(newTypeLabel);

        editTypeLabel = new ControlLabel("edit housing type", "Edit type of Hounsing unit.", true);
        editTypeLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                editHousingType();
            }
        });
        controls.getChildren().add(editTypeLabel);

        deleteTypeLabel = new ControlLabel("delete housing type", "Delete the selected type (only possible if not used).", true);
        deleteTypeLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                deleteHousingType();
            }
        });
        controls.getChildren().add(deleteTypeLabel);
        controls.getChildren().add(new Separator(Orientation.HORIZONTAL));

        importSubjectsLabel = new ControlLabel("import subjects", "Import a subject into the selected Housing unit.", true);
        importSubjectsLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                importSubjects();
            }
        });
        controls.getChildren().add(importSubjectsLabel);

        batchTreatmentLabel = new ControlLabel("batch treatment", "Treat all subjects in the selected Housing unit at once.", true);
        batchTreatmentLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                batchTreatment();
            }
        });
        controls.getChildren().add(batchTreatmentLabel);
    }


    private void setSelectedUnit(HousingUnit unit) {
        Platform.runLater(() -> {
            populationChart.listPopulation(unit);
            deleteUnitLabel.setDisable(unit == null);
            editUnitLabel.setDisable(unit == null);
            importSubjectsLabel.setDisable(unit == null);
            batchTreatmentLabel.setDisable(unit == null);
            appendUnitLabel.setDisable(unit == null);
            housingTable.setHousingUnit(unit);
        });
    }


    private void setSelectedType(HousingType ht) {
        deleteTypeLabel.setDisable(ht == null);
        editTypeLabel.setDisable(ht == null);
    }


    @Override
    public void refresh() {
        housingUnitTable.refresh();
        housingTypes.refresh();
    }


    private void editHousingUnit() {
        housingUnitTable.editHousingUnit(housingUnitTable.getSelectionModel().getSelectedItem().getValue());
    }



    private void newHousingUnit() {
        housingUnitTable.editHousingUnit(null);
    }


    private void editHousingType() {
        HousingType ht = housingTypes.getSelectionModel().getSelectedItem();
        editHousingTypeDialog(ht);
        housingTypes.refresh();
    }


    private void newHousingType() {
        editHousingTypeDialog(null);
        housingTypes.refresh();
    }


    private void deleteHousingUnit() {
        housingUnitTable.deleteHousingUnit(housingUnitTable.getSelectionModel().getSelectedItem().getValue());
    }


    private void deleteHousingType() {
        HousingType ht = housingTypes.getSelectionModel().getSelectedItem();
        if (!ht.getHousingUnits().isEmpty()) {
            showInfo("Cannot delete housing type " + ht.getName() + " since it is referenced by " +
                    Integer.toString(ht.getHousingUnits().size()) + " housing units!");
        } else {
            Session session = Main.sessionFactory.openSession();
            session.beginTransaction();
            session.delete(ht);
            session.getTransaction().commit();
            session.close();
        }
        housingTypes.getSelectionModel().select(null);
        housingTypes.refresh();
    }


    private void importSubjects() {
        TreeItem<HousingUnit> item = housingUnitTable.getSelectionModel().getSelectedItem();
        HousingUnit unit = item.getValue();
        importSubjectsDialog(unit);
        housingUnitTable.refresh();
        housingUnitTable.getSelectionModel().select(item);
    }


    private void batchTreatment() {
        HousingUnit unit = housingUnitTable.getSelectionModel().getSelectedItem().getValue();
        batchTreatmentDialog(unit);
    }


    @Override
    public VBox getControls() {
        return controls;
    }

    public static Tooltip getToolTip(){
        return new Tooltip("Define the animal housing. Batch import and treatment options.");
    }
}
