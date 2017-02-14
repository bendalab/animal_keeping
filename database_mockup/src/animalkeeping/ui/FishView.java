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
    @FXML private Tab housingHistoryTab;
    @FXML private VBox timelineVBox;
    @FXML private RadioButton deadOrAliveRadioBtn;

    private SubjectsTable fishTable;
    private HousingTable housingTable;
    private TimelineController timeline;
    private TableColumn<Treatment, Number> idCol;
    private TableColumn<Treatment, String> typeCol;
    private TableColumn<Treatment, Date> startDateCol;
    private TableColumn<Treatment, Date> endDateCol;
    private TableColumn<Treatment, String> nameCol;
    private TableColumn<Treatment, String> personCol;
    private ControlLabel reportDead;
    private ControlLabel moveSubject;
    private ControlLabel editSubjectLabel;
    private ControlLabel deleteSubjectLabel;
    private ControlLabel addTreatmentLabel;
    private ControlLabel editTreatmentLabel;
    private ControlLabel deleteTreatmentLabel;
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
        fishTable.getSelectionModel().getSelectedItems().addListener(new FishTableListChangeListener());
        fishTable.setAliveFilter(true);
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

        idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        idCol.prefWidthProperty().bind(treatmentTable.widthProperty().multiply(0.08));

        typeCol = new TableColumn<>("treatment");
        typeCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getType().getName()));
        typeCol.prefWidthProperty().bind(treatmentTable.widthProperty().multiply(0.18));

        startDateCol = new TableColumn<>("start");
        startDateCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getStart()));
        startDateCol.prefWidthProperty().bind(treatmentTable.widthProperty().multiply(0.18));

        endDateCol = new TableColumn<>("end");
        endDateCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getEnd()));
        endDateCol.prefWidthProperty().bind(treatmentTable.widthProperty().multiply(0.18));

        nameCol = new TableColumn<>("name");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSubject().getName()));
        nameCol.prefWidthProperty().bind(treatmentTable.widthProperty().multiply(0.18));

        personCol = new TableColumn<>("person");
        personCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPerson().getLastName() +
         ", " + data.getValue().getPerson().getFirstName()));
        personCol.prefWidthProperty().bind(treatmentTable.widthProperty().multiply(0.18));

        treatmentTable.getColumns().clear();
        treatmentTable.getColumns().addAll(idCol, typeCol, startDateCol, endDateCol, nameCol, personCol);
        treatmentTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Treatment>) c -> treatmentSelected(c.getList().size() > 0 ? c.getList().get(0) : null));

        housingTable = new HousingTable();
        housingHistoryTab.setContent(housingTable);

        controls = new VBox();
        ControlLabel newSubjectLabel = new ControlLabel("new subject", false);
        controls.getChildren().add(newSubjectLabel);
        editSubjectLabel = new ControlLabel("edit subject", true);
        controls.getChildren().add(editSubjectLabel);
        deleteSubjectLabel = new ControlLabel("delete subject", true);
        controls.getChildren().add(deleteSubjectLabel);

        controls.getChildren().add(new Separator(Orientation.HORIZONTAL));
        addTreatmentLabel = new ControlLabel("new treatment", true);
        addTreatmentLabel.setTooltip(new Tooltip("add a treatment entry for the selected subject"));
        controls.getChildren().add(addTreatmentLabel);
        editTreatmentLabel = new ControlLabel("edit treatment", true);
        controls.getChildren().add(editTreatmentLabel);
        deleteTreatmentLabel = new ControlLabel("remove treatment", true);
        controls.getChildren().add(deleteTreatmentLabel);

        controls.getChildren().add(new Separator(Orientation.HORIZONTAL));
        ControlLabel newComment = new ControlLabel("add observation", true);
        controls.getChildren().add(newComment);
        ControlLabel editComment = new ControlLabel("edit observation", true);
        controls.getChildren().add(editComment);
        ControlLabel deleteComment = new ControlLabel("delete observation", true);
        controls.getChildren().add(deleteComment);

        controls.getChildren().add(new Separator(Orientation.HORIZONTAL));
        moveSubject = new ControlLabel("move subject", true);
        moveSubject.setTooltip(new Tooltip("relocate subject to a different housing unit"));
        controls.getChildren().add(moveSubject);

        reportDead = new ControlLabel("report dead", true);
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
            housingTable.setHousings(s.getHousings());
        } else {
            idField.setText("");
            aliasField.setText("");
            supplierField.setText("");
            speciesField.setText("");
            aliveField.setText("");
            treatmentTable.getItems().clear();
            timeline.setTreatments(null);
            housingTable.setHousings(null);
        }
        moveSubject.setDisable(s == null);
        deleteSubjectLabel.setDisable(s == null);
        editSubjectLabel.setDisable(s == null);
        reportDead.setDisable(s == null);
        addTreatmentLabel.setDisable(s==null);
    }

    private void treatmentSelected(Treatment t) {
        editTreatmentLabel.setDisable(t == null);
        deleteTreatmentLabel.setDisable(t == null);
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