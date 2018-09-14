package animalkeeping.ui.widgets;

import animalkeeping.ui.Main;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DelayButtons extends VBox implements Initializable {
    @FXML private Button nowBtn;
    @FXML private Button fifteenBtn;
    @FXML private Button thirtyBtn;
    @FXML private Button oneHourBtn;
    @FXML private Button sixHourBtn;
    @FXML private Button immediateBtn;
    public SimpleIntegerProperty minutesProperty;

    public DelayButtons() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/animalkeeping/ui/fxml/DelayButtons.fxml"));
        loader.setController(this);
        try {
            this.getChildren().add(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        minutesProperty = new SimpleIntegerProperty(-99);

        immediateBtn.setTooltip(new Tooltip("Treatment ends immediately after start."));
        immediateBtn.setOnAction(event -> {
            minutesProperty.setValue(-1);});

        nowBtn.setTooltip(new Tooltip("Treatment ends now."));
        nowBtn.setOnAction(event -> {
            minutesProperty.setValue(0);});

        fifteenBtn.setTooltip(new Tooltip("15 minutes after start."));
        fifteenBtn.setOnAction(event -> {
            minutesProperty.setValue(15);});

        thirtyBtn.setTooltip(new Tooltip("30 minutes after start."));
        thirtyBtn.setOnAction(event -> {
            minutesProperty.setValue(30);});

        oneHourBtn.setTooltip(new Tooltip("1 hour after start."));
        oneHourBtn.setOnAction(event -> {
            minutesProperty.setValue(60);});

        sixHourBtn.setTooltip(new Tooltip("6 hours after start."));
        sixHourBtn.setOnAction(event -> {
            minutesProperty.setValue(360);});
    }
}
