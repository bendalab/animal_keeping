package animalkeeping.ui;

import animalkeeping.model.Subject;
import animalkeeping.model.Treatment;
import animalkeeping.ui.controller.TimelineController;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class FishView extends VBox implements Initializable {
    @FXML private ScrollPane tableScrollPane;
    @FXML private TextField idField;
    @FXML private TextField aliasField;
    @FXML private TextField speciesField;
    @FXML private TextField supplierField;
    @FXML private Label aliveField;
    @FXML private TableView<Treatment> treatmentTable;
    @FXML private VBox timelineVBox;

    private SubjectsTable fishTable;
    private TimelineController timeline;
    private TableColumn<Treatment, Number> idCol;
    private TableColumn<Treatment, String> typeCol;
    private TableColumn<Treatment, Date> startDateCol;
    private TableColumn<Treatment, Date> endDateCol;
    private TableColumn<Treatment, String> aliasCol;
    private VBox controls;


    public FishView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/FishView.fxml"));
        loader.setController(this);
        try {
            this.getChildren().add(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fishTable = new SubjectsTable();
        timeline = new TimelineController();

        //personsTable.resize();
        this.tableScrollPane.setContent(fishTable);
        this.timelineVBox.getChildren().add(timeline);
        idField.setEditable(false);
        idField.setText("");
        aliasField.setText("");
        aliasField.setEditable(false);
        speciesField.setEditable(false);
        speciesField.setText("");
        supplierField.setEditable(false);
        supplierField.setText("");
        aliveField.setText("");

        fishTable.getSelectionModel().getSelectedItems().addListener(new FishTableListChangeListener());
        idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        aliasCol = new TableColumn<>("alias");
        aliasCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSubject().getName()));
        typeCol = new TableColumn<>("treatment");
        typeCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getType().getName()));
        startDateCol = new TableColumn<>("start");
        startDateCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getStart()));
        endDateCol = new TableColumn<>("end");
        endDateCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getEnd()));
        treatmentTable.getColumns().clear();
        treatmentTable.getColumns().addAll(idCol, aliasCol, typeCol, startDateCol, endDateCol);

        controls = new VBox();
        ControlLabel newSubjectLabel = new ControlLabel("new subject", true);
        controls.getChildren().add(newSubjectLabel);
        ControlLabel editSubjectLabel = new ControlLabel("edit subject", true);
        controls.getChildren().add(editSubjectLabel);
        ControlLabel deleteSubjectLabel = new ControlLabel("delete subject", true);
        controls.getChildren().add(deleteSubjectLabel);

        controls.getChildren().add(new Separator(Orientation.HORIZONTAL));
        ControlLabel addTreatment = new ControlLabel("new treatment", true);
        addTreatment.setTooltip(new Tooltip("add a treatment entry for the selected subject"));
        controls.getChildren().add(addTreatment);
        ControlLabel editTreatment = new ControlLabel("edit treatment", true);
        controls.getChildren().add(editTreatment);

        controls.getChildren().add(new Separator(Orientation.HORIZONTAL));
        ControlLabel moveSubject = new ControlLabel("move subject", true);
        moveSubject.setTooltip(new Tooltip("relocate subject to a different housing unit"));
        controls.getChildren().add(moveSubject);

        controls.getChildren().add(new Separator(Orientation.HORIZONTAL));
        ControlLabel newComment = new ControlLabel("add observation", true);
        controls.getChildren().add(newComment);
        ControlLabel editComment = new ControlLabel("edit observation", true);
        controls.getChildren().add(editComment);
        ControlLabel deleteComment = new ControlLabel("delete observation", true);
        controls.getChildren().add(deleteComment);

        controls.getChildren().add(new Separator(Orientation.HORIZONTAL));
        ControlLabel reportDead = new ControlLabel("report dead", true);
        controls.getChildren().add(reportDead);
    }


    private void subjectSelected(Subject s) {
        if (s != null) {
            idField.setText(s.getId().toString());
            aliasField.setText(s.getName());
            speciesField.setText(s.getSpeciesType().getName());
            supplierField.setText(s.getSupplier().getName());
            aliveField.setText("Possibly");
            treatmentTable.getItems().clear();
            treatmentTable.getItems().addAll(s.getTreatments());
            timeline.setTreatments(s.getTreatments());
        } else {
            idField.setText("");
            aliasField.setText("");
            supplierField.setText("");
            speciesField.setText("");
            aliveField.setText("");
            treatmentTable.getItems().clear();
            timeline.setTreatments(null);
        }
    }

    public void nameFilter(String name) {
        this.fishTable.setNameFilter(name);
    }


    public void idFilter(Long id) {
        this.fishTable.setIdFilter(id);
    }

    private class FishTableListChangeListener implements ListChangeListener<Subject> {
        @Override
        public void onChanged(Change<? extends Subject> c) {
            if (c.getList().size() > 0) {
                subjectSelected(c.getList().get(0));
            }
        }
    }

    public VBox getControls() {
        return controls;
    }
}