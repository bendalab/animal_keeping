package animalkeeping.ui.controller;

import animalkeeping.ui.InventoryTable;
import animalkeeping.ui.PersonsTable;
import animalkeeping.ui.SubjectsTable;
import animalkeeping.ui.TreatmentsTable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;

public class MainViewController {

    @FXML
    private Button personsBtn;

    @FXML
    private Button treatmentsBtn;

    @FXML
    private Button subjectsBtn;

    @FXML
    private Button homeBtn;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private void showPersons() {
        this.scrollPane.setContent(null);
        PersonsTable personTable = new PersonsTable();
        this.scrollPane.setContent(personTable);
    }

    @FXML
    private void showSubjects() {
        this.scrollPane.setContent(null);
        SubjectsTable subjectsTable = new SubjectsTable();
        this.scrollPane.setContent(subjectsTable);
    }

    @FXML
    private void showTreatments() {
        this.scrollPane.setContent(null);
        TreatmentsTable treatmentsTable = new TreatmentsTable();
        this.scrollPane.setContent(treatmentsTable);
    }

    @FXML
    private void showInventory() {
        this.scrollPane.setContent(null);
        InventoryTable inventoryTable= new InventoryTable();
        this.scrollPane.setContent(inventoryTable);
    }
}