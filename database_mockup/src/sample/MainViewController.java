package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

public class MainViewController {
    private AnchorPane personView = null;

    public MainViewController() {
        FXMLLoader loader = new FXMLLoader();
        try {
            loader.setLocation(sample.Main.class.getResource("PersonsView.fxml"));
            this.personView = (AnchorPane) loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button personsBtn;

    @FXML
    private Button treatmentsBtn;

    @FXML
    private Button subjectsBtn;

    @FXML
    private Button housingBtn;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private void showPersons() {
        System.out.println("show Persons!");
        this.scrollPane.setContent(null);
        this.scrollPane.setContent(personView);
    }

    @FXML
    private void showSubjects() {
        System.out.println("show subjects!");
        this.scrollPane.setContent(null);
        sample.SubjectsTable subjectsTable = new sample.SubjectsTable();

        this.scrollPane.setContent(subjectsTable);
    }


}