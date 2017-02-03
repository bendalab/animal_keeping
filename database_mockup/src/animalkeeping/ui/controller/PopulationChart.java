package animalkeeping.ui.controller;

import animalkeeping.model.Housing;
import animalkeeping.model.HousingUnit;
import animalkeeping.model.Subject;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Created by grewe on 2/3/17.
 */
public class PopulationChart extends VBox implements Initializable {
    @FXML private VBox chartBox;
    private PieChart populationChart;
    private Label label;

    public PopulationChart() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/populationChart.fxml"));
        loader.setController(this);
        try {
            this.getChildren().add(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label = new Label("There is no population to show...");
        label.setVisible(true);
        populationChart = new PieChart();
        populationChart.setLegendSide(Side.RIGHT);
        populationChart.prefHeightProperty().bind(this.heightProperty());
        populationChart.prefWidthProperty().bind(this.widthProperty());
        chartBox.getChildren().clear();
        //chartBox.getChildren().add(populationChart);
        chartBox.getChildren().add(label);
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
                Integer c = counts.get(s.getSpeciesType().getName());
                c += 1;
                counts.put(s.getSpeciesType().getName(), c);
            } else {
                counts.put(s.getSpeciesType().getName(), 1);
            }
        }

        for (String st : counts.keySet()) {
            pieChartData.add(new PieChart.Data(st + " (" + counts.get(st) + ")", counts.get(st)));
        }
        populationChart.setTitle(housingUnit.getName() + ": " + subjects.size());
        populationChart.setData(pieChartData);
        chartBox.getChildren().clear();
        if (pieChartData.isEmpty()) {
            chartBox.getChildren().add(label);
        } else {
            chartBox.getChildren().add(populationChart);
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

}



