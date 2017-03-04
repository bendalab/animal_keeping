package animalkeeping.ui;

import animalkeeping.model.TreatmentType;
import animalkeeping.ui.controller.TimelineController;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by jan on 04.03.17.
 */
public class TreatmentsView extends VBox implements Initializable, View{
    @FXML private ScrollPane tableScrollPane;
    @FXML private Label idLabel;
    @FXML private Label nameLabel;
    @FXML private Label licenseLabel;
    @FXML private Label isFinalLabel;
    @FXML private Label descriptionLabel;
    @FXML private VBox timelineBox;
    @FXML private VBox treatmentsBox;
    @FXML private TabPane tabPane;

    private TreatmentTypeTable typeTable;
    private TreatmentsTable treatmentsTable;
    private TimelineController timeline;
    private ControlLabel editTreatmentTypeLabel;
    private ControlLabel deleteTreatmentTypeLabel;
    private VBox controls;


    public TreatmentsView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/TreatmentsView.fxml"));
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

        typeTable = new TreatmentTypeTable();
        typeTable.prefWidthProperty().bind(prefWidthProperty());
        typeTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<TreatmentType>) c -> {
            if (c.getList().size() > 0) {
                typeSelected(c.getList().get(0));
            }
        });
        tableScrollPane.setContent(typeTable);

        treatmentsTable = new TreatmentsTable();
        treatmentsTable.prefWidthProperty().bind(prefWidthProperty());
        treatmentsBox.getChildren().add(treatmentsTable);

        timeline = new TimelineController();
        this.timelineBox.getChildren().add(timeline);

        controls = new VBox();
        ControlLabel newTreatmentTypeLabel = new ControlLabel("new treatment type", false);
        newTreatmentTypeLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                editTreatmentType(null);
            }
        });
        controls.getChildren().add(newTreatmentTypeLabel);

        editTreatmentTypeLabel = new ControlLabel("edit license", true);
        editTreatmentTypeLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                editTreatmentType(typeTable.getSelectionModel().getSelectedItem());
            }
        });
        controls.getChildren().add(editTreatmentTypeLabel);

        deleteTreatmentTypeLabel = new ControlLabel("delete license", true);
        deleteTreatmentTypeLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                deleteType(typeTable.getSelectionModel().getSelectedItem());
            }
        });
        controls.getChildren().add(deleteTreatmentTypeLabel);

    }


    private void typeSelected(TreatmentType type) {
        idLabel.setText(type == null ? "" : type.getId().toString());
        nameLabel.setText(type == null ? "" : type.getName());
        descriptionLabel.setText(type == null ? "" : type.getDescription());
        licenseLabel.setText((type != null) && (type.getLicense() != null) ? type.getLicense().getName() : "");
        isFinalLabel.setText(type != null ? type.isInvasive() ? "True" : "False" : "");

        editTreatmentTypeLabel.setDisable(type == null);
        deleteTreatmentTypeLabel.setDisable(type == null);

        treatmentsTable.setTreatments(type != null ? type.getTreatments() : null);
        timeline.setTreatments(type != null ? type.getTreatments() : null);

    }

    private void editTreatmentType(TreatmentType type) {

    }

    private void deleteType(TreatmentType type) {

    }

    public VBox getControls() {
        return  controls;
    }

    @Override
    public void refresh() {

    }
}
