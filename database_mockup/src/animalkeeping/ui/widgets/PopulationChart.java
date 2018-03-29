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
package animalkeeping.ui.widgets;

import animalkeeping.model.Housing;
import animalkeeping.model.HousingUnit;
import animalkeeping.model.Subject;
import animalkeeping.util.EntityHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PopulationChart extends VBox {
    private VBox vbox;
    private PieChart populationChart;
    private Label label;

    public PopulationChart() {
        label = new Label("There is no population to show...");
        label.setVisible(false);

        populationChart = new PieChart();
        populationChart.setLegendSide(Side.RIGHT);
        populationChart.prefHeightProperty().bind(this.prefHeightProperty());
        populationChart.prefWidthProperty().bind(this.prefWidthProperty());

        ProgressIndicator indicator = new ProgressIndicator();
        indicator.setPrefSize(75, 75);
        indicator.setProgress(-1.0);

        vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(indicator);
        vbox.setVisible(false);

        StackPane pane = new StackPane();
        pane.getChildren().addAll(populationChart, vbox, label);
        this.getChildren().add(pane);
    }


    public void listPopulation(HousingUnit housingUnit) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        Set<Subject> subjects = new HashSet<>();
        HashMap<String, Integer> counts = new HashMap<>();

        Task<Void> refresh_task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                collectSubjects(subjects, housingUnit);
                for (Subject s : subjects) {
                    if (counts.containsKey(s.getSpeciesType().getName())) {
                        counts.put(s.getSpeciesType().getName(), counts.get(s.getSpeciesType().getName()) + 1);
                    } else {
                        counts.put(s.getSpeciesType().getName(), 1);
                    }
                }
                return null;
            }

            private void collectSubjects(Set<Subject> subjects, HousingUnit h) {
                collectSubjects(subjects, h, true, true);
            }


            private void collectSubjects(Set<Subject> subjects, HousingUnit h, Boolean currentOnly, Boolean recursive) {
                List<Housing> l;
                String query = "SELECT h FROM Housing h JOIN FETCH h.subject WHERE";
                if (currentOnly) {
                    query = query.concat(" h.end is null");
                }
                if (h != null) {
                    query = query.concat(currentOnly ? " AND " : "");
                    query = query.concat("h.housing.id = " + h.getId());
                }
                l = EntityHelper.getEntityList(query, Housing.class);

                for (Housing hs : l) {
                    subjects.add(hs.getSubject());
                }
                if (h != null && recursive) {
                    for (HousingUnit hu : h.getChildHousingUnits())
                        collectSubjects(subjects, hu, currentOnly, true);
                }
            }

        };
        refresh_task.setOnSucceeded(event -> {
            for (String st : counts.keySet()) {
                pieChartData.add(new PieChart.Data(st + " (" + counts.get(st) + ")", counts.get(st)));
            }
            String title;
            if (housingUnit == null) {
                title = "Total Population: " + subjects.size();
            } else {
                title = housingUnit.getName() + ": " + subjects.size();
            }
            populationChart.setTitle(title);
            populationChart.setData(pieChartData);
            label.setVisible(pieChartData.isEmpty());
        });

        vbox.visibleProperty().bind(refresh_task.runningProperty());
        new Thread(refresh_task).start();
    }

}



