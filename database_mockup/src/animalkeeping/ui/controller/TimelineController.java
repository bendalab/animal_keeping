package animalkeeping.ui.controller;


import animalkeeping.model.Treatment;
import animalkeeping.model.TreatmentType;
import animalkeeping.ui.DateAxis;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class TimelineController extends VBox implements Initializable {

    @FXML private VBox chartBox;
    private ScatterChart<Date, Number> timelineChart;
    private DateAxis xAxis;
    private NumberAxis yAxis;

    public TimelineController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/TimelinePlot.fxml"));
        loader.setController(this);
        try {
            this.getChildren().add(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public TimelineController(Set<Treatment> treatments) {
        this();
        setTreatments(treatments);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("initialize");
        xAxis = new DateAxis(new Date(0), new Date());
        yAxis = new NumberAxis(0, 10, 5);
        timelineChart = new ScatterChart<Date, Number>(xAxis, yAxis);
        timelineChart.autosize();
        timelineChart.setPrefHeight(150);
        //timelineChart.setPrefWidth(1000);
        timelineChart.setLegendSide(Side.RIGHT);
        chartBox.getChildren().clear();
        chartBox.getChildren().add(timelineChart);
    }


    public void setTreatments(Set<Treatment> treatments) {
        if (treatments == null) {
            timelineChart.setData(null);
            return;
        }
        timelineChart.getXAxis().setAutoRanging(true);
        timelineChart.getYAxis().setAutoRanging(true);
        timelineChart.getYAxis().setTickLabelsVisible(false);

        ObservableList<XYChart.Series<Date, Number>> seriesData = FXCollections.observableArrayList();
        HashMap<String, ObservableList<XYChart.Data<Date, Number>>> series = new HashMap<>();
        HashMap<String, Integer> seriesNumber = new HashMap<>();
        for (Treatment t : treatments) {
            if (!series.containsKey(t.getType().getName())) {
                ObservableList<XYChart.Data<Date, Number>> temp = FXCollections.observableArrayList();
                seriesNumber.put(t.getType().getName(), seriesNumber.size() + 1);
                temp.add(new XYChart.Data<Date, Number>(t.getStart(), seriesNumber.get(t.getType().getName())));
                series.put(t.getType().getName(), temp);
            } else {
                ObservableList<XYChart.Data<Date, Number>> temp = series.get(t.getType().getName());
                temp.add(new XYChart.Data<Date, Number>(t.getStart(), seriesNumber.get(t.getType().getName())));
            }
        }
        for (String s : series.keySet()) {
            seriesData.add(new XYChart.Series<>(s, series.get(s)));
        }
        timelineChart.setData(seriesData);
    }


}
