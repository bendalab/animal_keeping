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
import animalkeeping.model.Treatment;
import animalkeeping.model.TreatmentType;
import animalkeeping.ui.widgets.ControlLabel;
import animalkeeping.ui.Main;
import animalkeeping.ui.widgets.TimelineController;
import animalkeeping.ui.tables.TreatmentTypeTable;
import animalkeeping.ui.tables.TreatmentsTable;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TreatmentsView extends AbstractView implements Initializable {
    @FXML private ScrollPane tableScrollPane;
    @FXML private Label idLabel;
    @FXML private Label nameLabel;
    @FXML private Label licenseLabel;
    @FXML private Label isFinalLabel;
    @FXML private Label isInvasiveLabel;
    @FXML private Label targetLabel;
    @FXML private Label descriptionLabel;
    @FXML private VBox timelineBox;
    @FXML private VBox treatmentsBox;
    @FXML private TabPane tabPane;

    private TreatmentTypeTable typeTable;
    private TreatmentsTable treatmentsTable;
    private TimelineController timeline;
    private ControlLabel editTreatmentTypeLabel;
    private ControlLabel deleteTreatmentTypeLabel;
    private ControlLabel editTreatmentLabel;
    private ControlLabel deleteTreatmentLabel;
    private VBox controls;
    private TreatmentType selectedType;


    public TreatmentsView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/animalkeeping/ui/fxml/TreatmentsView.fxml"));
        loader.setController(this);
        try {
            this.getChildren().add(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabPane.prefWidthProperty().bind(prefWidthProperty());
        typeTable = new TreatmentTypeTable();
        typeTable.prefWidthProperty().bind(widthProperty());
        typeTable.prefHeightProperty().bind(heightProperty());
        typeTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<TreatmentType>) c -> {
            if (c.getList().size() > 0) {
                typeSelected(c.getList().get(0));
            } else {
                typeSelected(null);
            }
        });
        tableScrollPane.setContent(typeTable);
        tableScrollPane.prefHeightProperty().bind(this.heightProperty());
        treatmentsTable = new TreatmentsTable();
        treatmentsTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Treatment>) c -> {
            if (c.getList().size() > 0) {
                treatmentSelected(c.getList().get(0));
            } else {
                treatmentSelected(null);
            }
        });
        treatmentsTable.prefWidthProperty().bind(prefWidthProperty());
        treatmentsBox.getChildren().add(treatmentsTable);

        timeline = new TimelineController();
        this.timelineBox.getChildren().add(timeline);

        controls = new VBox();
        ControlLabel newTreatmentTypeLabel = new ControlLabel("new treatment type", "Define a new treatment type entry.", false);
        newTreatmentTypeLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                editTreatmentType(null);
            }
        });
        controls.getChildren().add(newTreatmentTypeLabel);

        editTreatmentTypeLabel = new ControlLabel("edit treatment type", "Edit the selected treatment type.", true);
        editTreatmentTypeLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                editTreatmentType(typeTable.getSelectionModel().getSelectedItem());
            }
        });
        controls.getChildren().add(editTreatmentTypeLabel);

        deleteTreatmentTypeLabel = new ControlLabel("delete treatment type", "Delete a treatment type (only possible if not referenced). ", true);
        deleteTreatmentTypeLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                deleteType(typeTable.getSelectionModel().getSelectedItem());
            }
        });
        controls.getChildren().add(deleteTreatmentTypeLabel);
        controls.getChildren().add(new Separator(Orientation.HORIZONTAL));

        ControlLabel newTreatmentLabel = new ControlLabel("new treatment", "Create a new treatment entry. Relates to a single subject.", false);
        newTreatmentLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                newTreatment();
            }
        });
        controls.getChildren().add(newTreatmentLabel);

        editTreatmentLabel = new ControlLabel("edit treatment", "Edit the treatment information.", true);
        editTreatmentLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                editTreatment(treatmentsTable.getSelectionModel().getSelectedItem());
            }
        });
        controls.getChildren().add(editTreatmentLabel);

        deleteTreatmentLabel = new ControlLabel("delete treatment", "Delete a treatment entry.", true);
        deleteTreatmentLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                deleteTreatment(treatmentsTable.getSelectionModel().getSelectedItem());
            }
        });
        controls.getChildren().add(deleteTreatmentLabel);

        ControlLabel batchTreatmentLabel = new ControlLabel("new batch treatment", "Create a new treatment for all subjects in a housing unit.", false);
        batchTreatmentLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                Dialogs.batchTreatmentDialog(null);
            }
        });
        controls.getChildren().add(new Separator(Orientation.HORIZONTAL));
        controls.getChildren().add(batchTreatmentLabel);
    }


    private void typeSelected(TreatmentType type) {
        selectedType = type;
        idLabel.setText(type == null ? "" : type.getId().toString());
        nameLabel.setText(type == null ? "" : type.getName());
        descriptionLabel.setText(type == null ? "" : type.getDescription());
        licenseLabel.setText((type != null) && (type.getLicense() != null) ? type.getLicense().getName() : "");
        isFinalLabel.setText(type != null ? type.isFinalExperiment() ? "True" : "False" : "");
        isInvasiveLabel.setText(type != null ? type.isInvasive() ? "True" : "False" : "");
        targetLabel.setText(type != null ? type.getTarget().toString() : "");

        editTreatmentTypeLabel.setDisable(type == null);
        deleteTreatmentTypeLabel.setDisable(type == null);

        treatmentsTable.setTreatments(type != null ? type.getTreatments() : null);
        timeline.setTreatments(type != null ? type.getTreatments() : null);
    }


    private void treatmentSelected(Treatment t) {
        editTreatmentLabel.setDisable(t == null);
        deleteTreatmentLabel.setDisable(t == null);
    }

    private void editTreatmentType(TreatmentType type) {
        typeTable.editTreatmentType(type);
    }

    private void deleteType(TreatmentType type) {
        typeTable.deleteTreatmentType(type);
    }


    private void newTreatment() {
        Dialogs.editTreatmentDialog(selectedType);
        EntityHelper.refreshEntity(selectedType);
        treatmentsTable.setTreatments(selectedType == null ? null : selectedType.getTreatments());
    }


    private void editTreatment(Treatment t) {
        Dialogs.editTreatmentDialog(t);
        EntityHelper.refreshEntity(t);
        treatmentsTable.getItems().remove(t);
        treatmentsTable.getItems().add(t);
        treatmentsTable.getSelectionModel().select(t);
    }


    private void deleteTreatment(Treatment t) {
        Communicator.pushDelete(t);
        EntityHelper.refreshEntity(selectedType);
        treatmentsTable.setTreatments(selectedType.getTreatments());
    }


    public VBox getControls() {
        return  controls;
    }

    @Override
    public void refresh() {
        typeTable.refresh();
    }

    public static Tooltip getToolTip() {
        return new Tooltip("View treatment types, manage actual animal treatments.");
    }
}
