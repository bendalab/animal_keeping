package animalkeeping.ui.controller;

import animalkeeping.ui.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Vector;


public class MainViewController {
    @FXML private TitledPane animalHousingPane;
    @FXML private TitledPane personsPane;
    @FXML private TitledPane licensesPane;
    @FXML private TitledPane inventoryPane;
    @FXML private TitledPane subjectsPane;
    @FXML private TitledPane treatmentsPane;
    @FXML private Button addUsrBtn;
    @FXML private TextField idField;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox masterBox;
    @FXML private ComboBox<String> findBox;
    @FXML private TitledPane findPane;
    private Vector<TitledPane> panes;

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
        panes = new Vector<>();
        panes.add(inventoryPane);
        panes.add(subjectsPane);
        panes.add(treatmentsPane);
        panes.add(personsPane);
        panes.add(animalHousingPane);
        panes.add(licensesPane);
    }


    @FXML
    private void showPersons() throws Exception{
        this.scrollPane.setContent(null);
        try{
            PersonsView pv = new PersonsView();

            this.scrollPane.setFitToHeight(true);
            this.scrollPane.setFitToWidth(true);
            pv.minHeightProperty().bind(this.scrollPane.heightProperty());
            pv.minWidthProperty().bind(this.scrollPane.widthProperty());
            this.scrollPane.setContent(pv);
            this.personsPane.setContent(pv.getControls());
            collapsePanes(personsPane);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    @FXML
    private void showSubjects() {
        this.scrollPane.setContent(null);
        try {
            FishView fish = new FishView();
            fish.prefHeightProperty().bind(this.scrollPane.heightProperty());
            fish.prefWidthProperty().bind(this.scrollPane.widthProperty());
            this.scrollPane.setContent(fish);
            this.subjectsPane.setContent(fish.getControls());
            collapsePanes(subjectsPane);
        } catch(Exception e){
            e.printStackTrace();}
    }

    @FXML
    private void showTreatments() {
        this.scrollPane.setContent(null);
        try {
            TreatmentsTable treatmentsTable = new TreatmentsTable();
            treatmentsTable.prefHeightProperty().bind(this.scrollPane.heightProperty());
            treatmentsTable.prefWidthProperty().bind(this.scrollPane.widthProperty());
            this.scrollPane.setContent(treatmentsTable);
            this.treatmentsPane.setContent(new VBox());
            collapsePanes(treatmentsPane);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showInventory() {
        this.scrollPane.setContent(null);
        try {
            InventoryController inventory = new InventoryController();
            inventory.prefHeightProperty().bind(this.scrollPane.heightProperty());
            inventory.prefWidthProperty().bind(this.scrollPane.widthProperty());
            this.scrollPane.setContent(inventory);
            this.inventoryPane.setContent(inventory.getControls());
            collapsePanes(inventoryPane);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showHousingUnits() {
        this.scrollPane.setContent(null);
        this.licensesPane.setContent(null);
        try {
            HousingView housingView = new HousingView();
            this.scrollPane.setFitToHeight(true);
            this.scrollPane.setFitToWidth(true);
            housingView.prefHeightProperty().bind(this.scrollPane.heightProperty());
            housingView.prefWidthProperty().bind(this.scrollPane.widthProperty());
            this.scrollPane.setContent(housingView);
            this.animalHousingPane.setContent(housingView.getControls());
            collapsePanes(animalHousingPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void showLicenseView() {
        this.scrollPane.setContent(null);
        this.licensesPane.setContent(null);
        try {
            LicenseView licenseView = new LicenseView();
            //this.scrollPane.setFitToHeight(true);
            //this.scrollPane.setFitToWidth(true);
            licenseView.prefHeightProperty().bind(this.scrollPane.heightProperty());
            licenseView.prefWidthProperty().bind(this.scrollPane.widthProperty());
            this.scrollPane.setContent(licenseView);
            this.licensesPane.setContent(licenseView.getControls());
            //collapsePanes(licensesPane);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Long looksLikeId(String text) {
        Long aLong = null;
        try {
            aLong = Long.parseLong(text);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (aLong!= null && aLong < 0)
            aLong = null;
        return aLong;
    }


    @FXML
    private void goToId(){
        this.scrollPane.setContent(null);
        Long id = looksLikeId(idField.getText());
        String selectedTable = findBox.getSelectionModel().getSelectedItem();
        if (selectedTable == null) {
            selectedTable = findBox.getItems().get(0);
        }
        if (!Main.isConnected()) {
            return;
        }

        switch (selectedTable) {
            case "Subject":
                FishView fv = new FishView();
                if (id != null) {
                    fv.idFilter(id);
                } else {
                    fv.nameFilter(idField.getText());
                }
                this.scrollPane.setContent(fv);
                break;
            case "Person":
                PersonsView pv = new PersonsView();
                if (id != null) {
                    pv.idFilter(id);
                } else {
                    pv.nameFilter(idField.getText());
                }
                this.scrollPane.setContent(pv);
                break;
            case "Treatment":
                System.out.println("not yet supported");
                break;
            default:
                System.out.println("invalid selection");
                break;
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
        subjectsPane.setDisable(false);
        personsPane.setDisable(false);
        inventoryPane.setDisable(false);
        treatmentsPane.setDisable(false);
        findPane.setDisable(false);
        addUsrBtn.setDisable(false);
        animalHousingPane.setDisable(false);
        licensesPane.setDisable(false);
        showInventory();
    }

    private void collapsePanes(TitledPane excludedPane) {
        for (TitledPane p : panes) {
            if (p != excludedPane) {
                p.setExpanded(false);
            }
        }
    }
}