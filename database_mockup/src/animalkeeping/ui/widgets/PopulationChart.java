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
import animalkeeping.ui.Main;
import animalkeeping.util.EntityHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class PopulationChart extends VBox implements Initializable {
    @FXML private VBox chartBox;
    private PieChart populationChart;
    private Label label;

    public PopulationChart() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/animalkeeping/ui/fxml/populationChart.fxml"));
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
        label = new Label("There is no population to show...");
        label.setVisible(true);
        populationChart = new PieChart();
        populationChart.setLegendSide(Side.RIGHT);
        populationChart.prefHeightProperty().bind(this.prefHeightProperty());
        populationChart.prefWidthProperty().bind(this.prefWidthProperty());
        this.getChildren().clear();
        this.getChildren().add(label);
    }


    public void listPopulation(HousingUnit housingUnit) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        if (housingUnit == null) {
            populationChart.setData(pieChartData);
            return;
        }
        Set<Subject> subjects = new HashSet<>();
        collectSubjects(subjects, housingUnit);

        HashMap<String, Integer> counts = new HashMap<>();
        for (Subject s : subjects) {
            if (counts.containsKey(s.getSpeciesType().getName())) {
                counts.put(s.getSpeciesType().getName(), counts.get(s.getSpeciesType().getName()) + 1);
            } else {
                counts.put(s.getSpeciesType().getName(), 1);
            }
        }

        for (String st : counts.keySet()) {
            pieChartData.add(new PieChart.Data(st + " (" + counts.get(st) + ")", counts.get(st)));
        }
        populationChart.setTitle(housingUnit.getName() + ": " + subjects.size());
        populationChart.setData(pieChartData);
        this.getChildren().clear();
        if (pieChartData.isEmpty()) {
            this.getChildren().add(label);
        } else {
            this.getChildren().add(populationChart);
        }
    }


    private void collectSubjects(Set<Subject> subjects, HousingUnit h) {
        collectSubjects(subjects, h, true, true);
    }


    void collectSubjects(Set<Subject> subjects, HousingUnit h, Boolean currentOnly, Boolean recursive) {
        List<Housing> l;
        if (currentOnly) {
            l = EntityHelper.getEntityList("from Housing where end_datetime is null and type_id = " + h.getId(), Housing.class);
        } else {
            l = EntityHelper.getEntityList("from Housing where type_id = " + h.getId(), Housing.class);
        }
        for (Housing hs : l) {
            subjects.add(hs.getSubject());
        }
        if (recursive) {
            for (HousingUnit hu : h.getChildHousingUnits())
                collectSubjects(subjects, hu, currentOnly, recursive);
        }
    }

}



