package animalkeeping.ui.controller;

import animalkeeping.ui.*;
import com.sun.org.apache.xpath.internal.operations.Bool;
//import com.sun.tools.corba.se.idl.constExpr.BooleanNot;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.awt.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class MainViewController {

    @FXML private Button personsBtn;
    @FXML private Button treatmentsBtn;
    @FXML private Button subjectsBtn;
    @FXML private Button inventoryBtn;
    @FXML private Button addUsrBtn;
    @FXML private TextField idField;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox masterBox;
    @FXML private VBox contextButtonBox;
    @FXML private ComboBox<String> findBox;
    @FXML private TitledPane findPane;

    @FXML
    private void initialize() {
        findBox.getItems().clear();
        findBox.getItems().addAll("Person", "Subject", "Housing unit", "Treatment");
        findBox.getSelectionModel().select("Subject");
        this.scrollPane.setContent(null);
        if (!Main.isConnected()) {
            LoginController login = new LoginController();
            login.addEventHandler(LoginController.DatabaseEvent.CONNECT, event -> connectedToDatabase());
            this.scrollPane.setContent(login);
        }
        else {
            try{
                connectedToDatabase();}
            catch(Exception e){
                e.printStackTrace();
            }
        }

    }


    @FXML
    private void showPersons() throws Exception{
        this.scrollPane.setContent(null);
        try{
            PersonsView pv = new PersonsView();
            this.scrollPane.setContent(pv);
            this.contextButtonBox.getChildren().clear();
            this.contextButtonBox.getChildren().add(pv.getControls());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    @FXML
    private void showSubjects() {
        this.scrollPane.setContent(null);
        this.contextButtonBox.getChildren().clear();
        try{
            FishView fish = new FishView();
            this.scrollPane.setContent(fish);}
        catch(Exception e){
            e.printStackTrace();}
    }

    @FXML
    private void showTreatments() {
        this.scrollPane.setContent(null);
        this.contextButtonBox.getChildren().clear();
        try{
            TreatmentsTable treatmentsTable = new TreatmentsTable();
            this.scrollPane.setContent(treatmentsTable);}
        catch(Exception e){
            e.printStackTrace();}
    }

    @FXML
    private void showInventory() {
        this.scrollPane.setContent(null);
        this.contextButtonBox.getChildren().clear();
        try {
            InventoryController inventory = new InventoryController();
            this.scrollPane.setContent(inventory);}
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private Integer looksLikeId(String text) {
        Integer integer = null;
        try {
            integer = Integer.parseInt(text);
        } catch (NumberFormatException e) {

        }
        if (integer != null && integer < 0)
            integer = null;
        return integer;
    }


    @FXML
    private void goToId(){
        this.scrollPane.setContent(null);
        Integer id = looksLikeId(idField.getText());
        System.out.println(id != null);
        String selectedTable = findBox.getSelectionModel().getSelectedItem();
        if (selectedTable == null) {
            selectedTable = findBox.getItems().get(0);
        }
        if (!Main.isConnected()) {
            return;
        }

        if (selectedTable.equals("Subject")) {
            if (Main.isConnected()) {
                if (id != null) {
                    IndividualTable individualTable = new IndividualTable(id);
                    this.scrollPane.setContent(individualTable);
                } else {
                    IndividualTable individualTable = new IndividualTable(idField.getText());
                    this.scrollPane.setContent(individualTable);
                }
            }
        } else if (selectedTable.equals("Person")) {
            PersonsView pv = new PersonsView();
            if (id != null) {
                pv.setSelectedPerson(id);
            } else {
                pv.setSelectedPerson(idField.getText());
            }
            this.scrollPane.setContent(pv);
        } else if (selectedTable.equals("Treatment")) {
            System.out.println("not yet supported");
        } else {
            System.out.println("invalid selection");
        }
    }

    @FXML
    private void showUserAddInterface() throws Exception{
        Main.getPrimaryStage().setScene(new Scene(FXMLLoader.load(Main.class.getResource("fxml/UserAddView.fxml"))));
        /*this.scrollPane.setContent(null);
        PersonsTable personTable = new PersonsTable();
        this.scrollPane.setContent(personTable);*/
    }

    @FXML
    private  void closeApplication() {
        Main.getPrimaryStage().close();
    }

    @FXML
    private void disconnectFromDatabase() {
        Main.sessionFactory.close();
    }


    private void connectedToDatabase() {
        subjectsBtn.setDisable(false);
        personsBtn.setDisable(false);
        subjectsBtn.setDisable(false);
        inventoryBtn.setDisable(false);
        treatmentsBtn.setDisable(false);
        findPane.setDisable(false);
        addUsrBtn.setDisable(false);
        showInventory();
    }
}