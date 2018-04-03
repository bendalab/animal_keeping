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

 * Created by jan on 14.01.17.

 *****************************************************************************/
package animalkeeping.ui.views;

import animalkeeping.model.*;
import animalkeeping.ui.Main;
import animalkeeping.ui.tables.HousingTable;
import animalkeeping.ui.tables.TreatmentsTable;
import animalkeeping.ui.widgets.ControlLabel;
import animalkeeping.ui.widgets.PopulationChart;
import animalkeeping.ui.widgets.PopulationStackedChart;
import animalkeeping.util.EntityHelper;
import animalkeeping.util.XlsxExport;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class InventoryController extends AbstractView implements Initializable {
    @FXML private VBox unitsBox;
    @FXML private ListView<String> unitsList;
    @FXML private VBox chartVbox;
    @FXML private VBox popHistoryVBox;
    @FXML private VBox currentHousingsBox;
    @FXML private ScrollPane tableScrollPane;
    @FXML private Tab populationHistory;
    @FXML private Tab populationPieTab;

    private PopulationStackedChart populationHistoryChart;
    private PopulationChart populationChart;
    private HousingTable housingTable;
    private TreatmentsTable treatmentsTable;
    private VBox controls;
    private HashMap<String, HousingUnit> unitsHashMap;
    private ControlLabel endTreatmentLabel;


    public InventoryController() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/animalkeeping/ui/fxml/Inventory.fxml"));
        loader.setController(this);
        try {
            this.getChildren().add(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        unitsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        unitsList.getItems().add("all");
        unitsList.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                if (c.getList().size() > 0) {
                    listPopulation(c.getList().get(0));
                }
            }
        });

        unitsHashMap = new HashMap<>();

        housingTable = new HousingTable();
        treatmentsTable = new TreatmentsTable(true);
        treatmentsTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Treatment>) c -> {
            if (c.getList().size() > 0) {
                treatmentSelected(c.getList().get(0));
            }
        });
        currentHousingsBox.getChildren().add(housingTable);
        tableScrollPane.setContent(treatmentsTable);

        controls = new VBox();
        ControlLabel animalUseLabel = new ControlLabel("export animal use", false);
        animalUseLabel.setTooltip(new Tooltip("Export excel sheet containing the animal use per license."));
        animalUseLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                fireEvent(new ViewEvent(ViewEvent.EXPORTING));
                XlsxExport.exportAnimalUse();
                fireEvent(new ViewEvent(ViewEvent.DONE));
            }
        });
        ControlLabel exportStock = new ControlLabel("export stock list", false);
        exportStock.setTooltip(new Tooltip("Export current stock list to excel sheet."));
        exportStock.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                fireEvent(new ViewEvent(ViewEvent.EXPORTING));
                XlsxExport.exportStockList();
                fireEvent(new ViewEvent(ViewEvent.DONE));
            }
        });

        endTreatmentLabel  = new ControlLabel("end treatment", true);
        endTreatmentLabel.setTooltip(new Tooltip("End an open treatment."));
        endTreatmentLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                endTreatment();
            }
        });
        controls.getChildren().add(animalUseLabel);
        controls.getChildren().add(exportStock);
        controls.getChildren().add(new Separator(Orientation.HORIZONTAL));
        controls.getChildren().add(endTreatmentLabel);

        populationChart = new PopulationChart();
        populationPieTab.setContent(populationChart);

        populationHistoryChart = new PopulationStackedChart();
        populationHistory.setContent(populationHistoryChart);
        populationHistoryChart.setFillWidth(true);
    }

    private void fillList() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<HousingUnit> result = EntityHelper.getEntityList("from HousingUnit where parent_unit_id is NULL", HousingUnit.class);
                Platform.runLater(() -> {
                    unitsList.getItems().clear();
                    unitsList.getItems().add("all");
                    if (result != null) {
                        for (HousingUnit h : result) {
                            unitsHashMap.put(h.getName(), h);
                            unitsList.getItems().add(h.getName());
                        }
                    }
                });
                return null;
            }
        };
        new Thread(task).start();
    }

    private void refreshOpenTreatments() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<Treatment> treatments = EntityHelper.getEntityList("from Treatment where end_datetime is NULL", Treatment.class);
                Platform.runLater(() -> {
                    treatmentsTable.getSelectionModel().select(null);
                    treatmentsTable.setTreatments(treatments);
                });
                return null;
            }
        };
        new Thread(task).start();
    }

    @FXML
    private void listAllPopulation() {
        populationChart.listPopulation(null);
        populationHistoryChart.setHousingUnit(null);
    }


    private void listPopulation(String unitName) {
        if (unitName.toLowerCase().equals("all")) {
            listAllPopulation();
            return;
        }
        HousingUnit housingUnit = unitsHashMap.get(unitName);
        populationHistoryChart.setHousingUnit(housingUnit);
        populationChart.listPopulation(housingUnit);
        housingTable.setHousingUnit(housingUnit);
    }

    @Override
    public  VBox getControls() {
        return controls;
    }

    @Override
    public void refresh() {
        fillList();
        refreshOpenTreatments();
        listAllPopulation();
    }

    private void handleEvents(Event event) {
        if (event.getEventType() == WorkerStateEvent.WORKER_STATE_SCHEDULED) {
            fireEvent(new ViewEvent(ViewEvent.REFRESHING));
        } else if (event.getEventType() == WorkerStateEvent.WORKER_STATE_SUCCEEDED) {
            fireEvent(new ViewEvent(ViewEvent.REFRESHED));
        } else if (event.getEventType() == WorkerStateEvent.WORKER_STATE_FAILED) {
            fireEvent(new ViewEvent(ViewEvent.REFRESH_FAIL));
        }
    }

    private void collectSubjects(Set<Subject> subjects, HousingUnit h) {
        collectSubjects(subjects, h, true);
    }

    private void collectSubjects(Set<Subject> subjects, HousingUnit h, Boolean currentOnly) {
        for (Housing housing : h.getAllHousings(true)) {
            subjects.add(housing.getSubject());

        }
    }

    private void endTreatment() {
        treatmentsTable.endTreatment(treatmentsTable.getSelectionModel().getSelectedItem());
        refreshOpenTreatments();
    }

    private void treatmentSelected(Treatment t) {
        endTreatmentLabel.setDisable(t == null);
    }

    public static Tooltip getToolTip() {
        return new Tooltip("Gives an overview over the animal population. Offers export functions.");
    }
}


