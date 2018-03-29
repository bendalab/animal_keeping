package animalkeeping.ui.widgets;
import animalkeeping.model.Housing;
import animalkeeping.model.SpeciesType;
import animalkeeping.ui.Main;
import animalkeeping.util.DateTimeHelper;
import animalkeeping.util.EntityHelper;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

/******************************************************************************
 animalBase
 animalkeeping.ui.widgets

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

 * Created by jan on 24.03.18.

 *****************************************************************************/

public class PopulationStackedChart extends VBox {
    private CategoryAxis xAxis = new CategoryAxis();
    private StackedAreaChart<String, Number> chart;
    private DatePicker startDate, endDate;
    private ComboBox<String> intervalCombo;
    private Button exportBtn;
    private ProgressIndicator busy;
    private VBox vbox;

    public PopulationStackedChart() {
        this.xAxis.setCategories(FXCollections.observableArrayList
                (Arrays.asList("1750", "1800", "1850", "1900", "1950", "1999", "2050" )));
        this.xAxis.setLabel("Date");
        this.xAxis.tickLabelFontProperty().set(Font.font(9));
        NumberAxis yAxis = new NumberAxis();
        yAxis.setForceZeroInRange(true);
        yAxis.setLowerBound(0.);
        yAxis.setUpperBound(100.);
        yAxis.setTickUnit(25.);
        yAxis.setLabel("Population");
        this.chart = new StackedAreaChart<>(xAxis, yAxis);

        GridPane grid = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints(100,100, Double.MAX_VALUE);
        column1.setHgrow(Priority.ALWAYS);
        ColumnConstraints column2 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
        column2.setHgrow(Priority.ALWAYS);
        ColumnConstraints column3 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
        column3.setHgrow(Priority.ALWAYS);
        ColumnConstraints column4 = new ColumnConstraints(0, 150, Double.MAX_VALUE);
        column3.setHgrow(Priority.ALWAYS);
        ColumnConstraints column5 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
        column3.setHgrow(Priority.NEVER);
        grid.getColumnConstraints().addAll(column1, column2, column3, column4, column5);

        this.busy = new ProgressIndicator();
        this.busy.setProgress(0.0);
        this.busy.setPrefSize(75, 75);
        this.startDate = new DatePicker(java.time.LocalDate.now().minusYears(1));
        this.endDate = new DatePicker(DateTimeHelper.toLocalDate(new Date()));
        this.intervalCombo = new ComboBox<>();
        this.exportBtn = new Button("export");
        this.exportBtn.setDisable(true);
        this.exportBtn.setOnAction(event -> exportPopulationCounts());
        Button updateBtn = new Button("update");
        updateBtn.setOnAction(event -> getPupulationCounts());
        this.intervalCombo.getItems().addAll("monthly");
        this.intervalCombo.getSelectionModel().select(0);
        this.intervalCombo.prefWidthProperty().bind(column3.maxWidthProperty());
        this.startDate.prefWidthProperty().bind(column1.maxWidthProperty());
        this.endDate.prefWidthProperty().bind(column2.maxWidthProperty());
        this.exportBtn.prefWidthProperty().bind(column5.maxWidthProperty());
        
        grid.setHgap(5);
        grid.add(new Label("Start date:"), 0, 0);
        grid.add(this.startDate, 0, 1);
        grid.add(new Label("End date:"), 1, 0);
        grid.add(this.endDate, 1,1);
        grid.add(new Label("Interval:"), 2, 0);
        grid.add(this.intervalCombo, 2, 1);
        grid.add(updateBtn, 4, 0);
        grid.add(this.exportBtn, 4, 1);

        vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(busy);
        vbox.setDisable(true);
        vbox.setVisible(false);

        StackPane spane = new StackPane();
        spane.getChildren().addAll(chart, vbox);

        BorderPane pane = new BorderPane();
        pane.setTop(grid);
        pane.setCenter(spane);
        this.getChildren().add(pane);
        this.setFillWidth(true);
        this.setPadding(new Insets(2, 10, 2, 10));
    }


    private Vector<Date> getDueDates() {
        LocalDate start = startDate.getValue();
        LocalDate end = endDate.getValue();
        Vector<Date> dueDates = new Vector<>();
        //  FIXME the due dates are not correct!
        if (intervalCombo.getValue().equalsIgnoreCase("monthly")) {
            start = start.plusMonths(1);
            start = start.minusDays(start.getDayOfMonth());
        } else if (intervalCombo.getValue().equalsIgnoreCase("weekly")) {
            start.plusDays(DayOfWeek.SUNDAY.getValue() - start.getDayOfWeek().getValue());
        }
        dueDates.add(DateTimeHelper.localDateToUtilDate(start));
        while (start.isBefore(end)) {
            start = intervalCombo.getValue().equalsIgnoreCase("monthly") ? start.plusMonths(1): start.plusWeeks(1);

            dueDates.add(DateTimeHelper.localDateToUtilDate(start));
        }
        return dueDates;
    }


    private void getPupulationCounts() {
        chart.getData().clear();
        this.xAxis.getCategories().clear();
        Vector<Date> dueDates = getDueDates();
        List<SpeciesType> species = EntityHelper.getEntityList("FROM SpeciesType", SpeciesType.class);
        Vector<XYChart.Series> series = new Vector<>();

        Task<Void> refresh_task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int maxWork = dueDates.size() * species.size();
                updateProgress(0, maxWork);
                Session session = Main.sessionFactory.openSession();
                SimpleDateFormat fmt = new SimpleDateFormat("MM.yyyy");

                String q = "SELECT COUNT(distinct(s.id)) FROM census_subject s, census_housing h, census_speciestype st " +
                        "where h.subject_id = s.id AND s.species_id = st.id AND st.id = :species " +
                        "AND h.start_datetime < :end AND (h.end_datetime is null OR h.end_datetime > :end)";
                Query query = session.createNativeQuery(q);
                int progress = 0;
                for (int i = 0; i < dueDates.size(); i++) {
                    Date d = dueDates.get(i);
                    for (int j = 0; j < species.size(); j++) {
                        SpeciesType sp = species.get(j);
                        XYChart.Series s;
                        if (i == 0) {
                            s = new XYChart.Series();
                            s.setName(sp.getName());
                            series.add(s);
                        } else {
                            s = series.get(j);
                        }

                        query.setParameter("species", sp.getId());
                        query.setParameter("end", d);
                        Number count = (Number) query.getSingleResult();
                        s.getData().add(new XYChart.Data<>(fmt.format(d), count));
                        progress++;
                        updateProgress(progress, maxWork);
                    }
                }
                session.close();
                updateProgress(maxWork, maxWork);
                return null;
            }
        };

        refresh_task.setOnSucceeded(event -> {
            SimpleDateFormat fmt = new SimpleDateFormat("MM.yyyy");
            ArrayList<String> dates = new ArrayList<>();
            for (Date d : dueDates) {
                dates.add(fmt.format(d));
            }
            this.xAxis.getCategories().addAll(dates);
            for (XYChart.Series s : series)
                this.chart.getData().add(s);
        });
        vbox.visibleProperty().bind(refresh_task.runningProperty());
        busy.progressProperty().bind(refresh_task.progressProperty());
        exportBtn.disableProperty().bind(refresh_task.runningProperty());
        new Thread(refresh_task).start();
    }

    private void exportPopulationCounts() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet overviewsheet = workbook.createSheet("Population history");
        XSSFRow row = overviewsheet.createRow(0);
        XSSFCell cell = null;

        for (int i = 0; i < chart.getData().size(); i++) {
            XYChart.Series s = chart.getData().get(i);
            if (row.getRowNum() > 0) {
                row = overviewsheet.getRow(0);
            }
            row.createCell(i + 1).setCellValue(s.getName());
            int totalCount = 0;
            for ( int j = 0; j < s.getData().size(); j++) {
                if (i == 0) {
                    row = overviewsheet.createRow(j + 1);
                } else {
                    row = overviewsheet.getRow(j + 1);
                }
                XYChart.Data<String, Number> d = (XYChart.Data<String, Number>) s.getData().get(j);
                if (i == 0) {
                    row.createCell(0).setCellValue(d.getXValue());
                }
                cell = row.createCell(i + 1);
                cell.setCellValue(d.getYValue().intValue());
                totalCount += d.getYValue().intValue();
            }
            if (i == 0) {
                row = overviewsheet.createRow(s.getData().size() + 1);
                row.createCell(0).setCellValue("Average:");
            } else {
                row = overviewsheet.getRow(s.getData().size() + 1);
            }
            XSSFCell avgcell = row.createCell(i + 1);
            avgcell.setCellValue(totalCount/s.getData().size());
            overviewsheet.autoSizeColumn(i);
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select output file");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel doc(*.xlsx)", "*.xlsx"));
        chooser.setInitialFileName("populationHistory.xlsx");
        File f = chooser.showSaveDialog(Main.getPrimaryStage());
        if (f != null) {
            try {
                FileOutputStream out = new FileOutputStream(f);
                workbook.write(out);
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //FIXME should be able to show the history of a single housing unit.
    //FIXME add option to create a simple population plot, irrespective of species
}