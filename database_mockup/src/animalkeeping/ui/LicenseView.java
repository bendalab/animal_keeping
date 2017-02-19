package animalkeeping.ui;

import animalkeeping.model.License;
import animalkeeping.model.Quota;
import animalkeeping.ui.controller.TimelineController;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

/**
 * Created by jan on 18.02.17.
 */
public class LicenseView extends VBox implements Initializable {
    @FXML private ScrollPane tableScrollPane;
    @FXML private Label idLabel;
    @FXML private Label nameLabel;
    @FXML private Label permitNoLabel;
    @FXML private Label agencyLabel;
    @FXML private Label respPersonLabel;
    @FXML private Label deputyPersonLabel;
    @FXML private Label startDateLabel;
    @FXML private Label endDateLabel;
    @FXML private VBox timelineVBox;
    @FXML private VBox quotaBox;
    @FXML private VBox typesBox;
    @FXML private TabPane tabPane;

    private LicenseTable licenseTable;
    private TreatmentTypeTable typeTable;
    private QuotaView qv;
    private TimelineController timeline;
    private ControlLabel addLicenseLabel;
    private ControlLabel editLicenseLabel;
    private ControlLabel deleteLicenseLabel;
    private VBox controls;


    public LicenseView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/LicenseView.fxml"));
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
        licenseTable = new LicenseTable();
        licenseTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<License>) c -> {
            if (c.getList().size() > 0) {
                licenseSelected(c.getList().get(0));
            }
        });
        typeTable = new TreatmentTypeTable();
        typeTable.prefWidthProperty().bind(prefWidthProperty());
        typesBox.getChildren().add(typeTable);

        timeline = new TimelineController();
        this.tableScrollPane.setContent(licenseTable);
        this.timelineVBox.getChildren().add(timeline);

        qv = new QuotaView();
        quotaBox.prefWidthProperty().bind(this.prefWidthProperty());
        qv.prefWidthProperty().bind(quotaBox.prefWidthProperty());
        quotaBox.getChildren().add(qv);

        controls = new VBox();
        addLicenseLabel = new ControlLabel("new license", false);
        addLicenseLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                System.out.println("new License");
            }
        });
        controls.getChildren().add(addLicenseLabel);

        editLicenseLabel = new ControlLabel("edit license", true);
        editLicenseLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                System.out.println("edit License");
            }
        });
        controls.getChildren().add(editLicenseLabel);

        deleteLicenseLabel = new ControlLabel("delete license", true);
        deleteLicenseLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
              System.out.print("delete License");
            }
        });
        controls.getChildren().add(deleteLicenseLabel);
    }


    private void setInfo(License l) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        idLabel.setText(l.getId().toString());
        nameLabel.setText(l.getName());
        permitNoLabel.setText(l.getNumber());
        startDateLabel.setText(l.getStartDate() != null ? sdf.format(l.getStartDate()) : "");
        endDateLabel.setText(l.getEndDate() != null ? sdf.format(l.getEndDate()) : "");
        respPersonLabel.setText("n.a.");
        deputyPersonLabel.setText("n.a.");
        agencyLabel.setText("n.a.");
    }

    private void licenseSelected(License l) {
        setInfo(l);
        qv.setQuota(l.getQuotas());
        typeTable.setTreatmentTypes(l.getTreatmentTypes());
    }

    public VBox getControls() {
        return controls;
    }
}
