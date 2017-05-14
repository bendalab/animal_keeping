package animalkeeping.ui.widgets;


import animalkeeping.model.Treatment;
import animalkeeping.ui.widgets.DateAxis;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Set;

public class TimelineController extends VBox implements Initializable {

    @FXML private VBox chartBox;
    private ScatterChart<Date, Number> timelineChart;
    private DateAxis xAxis;
    private NumberAxis yAxis;

    public TimelineController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/animalkeeping/ui/fxml/TimelinePlot.fxml"));
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
        xAxis = new DateAxis(new Date(0), new Date());
        yAxis = new NumberAxis(0, 10, 5);
        timelineChart = new ScatterChart<Date, Number>(xAxis, yAxis);
        timelineChart.autosize();
        timelineChart.setPrefHeight(250);
        timelineChart.setMaxHeight(1000);
        timelineChart.setLegendSide(Side.TOP);
        chartBox.getChildren().clear();
        chartBox.getChildren().add(timelineChart);
    }


    public void setTreatments(Set<Treatment> treatments) {
        if (treatments == null) {
            timelineChart.setData(null);
            return;
        }

        ObservableList<XYChart.Series<Date, Number>> seriesData = FXCollections.observableArrayList();
        HashMap<String, ObservableList<XYChart.Data<Date, Number>>> series = new HashMap<>();
        HashMap<String, Integer> seriesNumber = new HashMap<>();
        for (Treatment t : treatments) {
            if (!series.containsKey(t.getTreatmentType().getName())) {
                ObservableList<XYChart.Data<Date, Number>> temp = FXCollections.observableArrayList();
                seriesNumber.put(t.getTreatmentType().getName(), seriesNumber.size() + 1);
                temp.add(new XYChart.Data<Date, Number>(t.getStart(), seriesNumber.get(t.getTreatmentType().getName())));
                series.put(t.getTreatmentType().getName(), temp);
            } else {
                ObservableList<XYChart.Data<Date, Number>> temp = series.get(t.getTreatmentType().getName());
                temp.add(new XYChart.Data<Date, Number>(t.getStart(), seriesNumber.get(t.getTreatmentType().getName())));
            }
        }
        for (String s : series.keySet()) {
            seriesData.add(new XYChart.Series<>(s, series.get(s)));
        }
        timelineChart.setData(seriesData);

        if (treatments.size() == 1) {
            timelineChart.getXAxis().setAutoRanging(false);
            Date date = seriesData.get(0).getData().get(0).getXValue();
            long time_org = date.getTime();
            Date min_date = new Date(time_org - (60*60*25*1000));
            Date max_date = new Date(time_org + (60*60*25*1000));
            ((DateAxis)timelineChart.getXAxis()).setLowerBound(min_date);
            ((DateAxis)timelineChart.getXAxis()).setUpperBound(max_date);
        } else {
            timelineChart.getXAxis().setAutoRanging(true);
        }
        timelineChart.getYAxis().setTickLabelsVisible(false);
        timelineChart.getXAxis().setLabel("Date");
    }


}
