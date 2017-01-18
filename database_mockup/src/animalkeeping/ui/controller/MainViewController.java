package animalkeeping.ui.controller;

import animalkeeping.ui.*;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.awt.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

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
    private Button addUsrBtn;

    @FXML
    private TextField idField;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox masterBox;

    @FXML
    private VBox contextButtonBox;

    @FXML
    private void showPersons() throws Exception{
        this.scrollPane.setContent(null);
        PersonsView pv = new PersonsView();
        this.scrollPane.setContent(pv);
    }

    @FXML
    private void showSubjects() {
        this.scrollPane.setContent(null);
        FishView fish = new FishView();
        this.scrollPane.setContent(fish);
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
        InventoryController inventory = new InventoryController();
        // InventoryTable inventoryTable= new InventoryTable();
        this.scrollPane.setContent(inventory);
    }

    @FXML
    private void goToId(){
        this.scrollPane.setContent(null);
        System.out.println(idField.getText());
        IndividualTable individualTable = new IndividualTable(Integer.parseInt(idField.getText()));
        this.scrollPane.setContent(individualTable);
    }

    @FXML
    private void showUserAddInterface() throws Exception{
        Main.getPrimaryStage().setScene(new Scene(FXMLLoader.load(Main.class.getResource("fxml/UserAddView.fxml"))));
        /*this.scrollPane.setContent(null);
        PersonsTable personTable = new PersonsTable();
        this.scrollPane.setContent(personTable);*/
    }

    @FXML
    private void connectToDatabase() {
        Main.connectToDatabase();
    }

    @FXML
    private  void closeApplication() {
        Main.getPrimaryStage().close();
    }

    @FXML
    private void disconnectFromDatabase() {
        Main.sessionFactory.close();
    }
}