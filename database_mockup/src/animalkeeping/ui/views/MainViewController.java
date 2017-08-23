/******************************************************************************
 Copyright (c) 2017 Neuroethology Lab, University of Tuebingen,
 Jan Grewe <jan.grewe@g-node.org>,
 Dennis Huben <dennis.huben@rwth-aachen.de>

 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this list
 of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice, this
 list of conditions and the following disclaimer in the documentation and/or other
 materials provided with the distribution.

 3. Neither the name of the copyright holder nor the names of its contributors may
 be used to endorse or promote products derived from this software without specific
 prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 DAMAGE.

 * Created by jan on 27.12.16.

 *****************************************************************************/
package animalkeeping.ui.views;

import animalkeeping.model.HousingUnit;
import animalkeeping.model.SpeciesType;
import animalkeeping.model.SubjectType;
import animalkeeping.model.SupplierType;
import animalkeeping.ui.Main;
import animalkeeping.ui.forms.LoginController;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import animalkeeping.util.XlsxExport;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

// import com.apple.eawt.*;


public class MainViewController extends VBox implements Initializable{
    @FXML private TitledPane animalHousingPane;
    @FXML private TitledPane personsPane;
    @FXML private TitledPane licensesPane;
    @FXML private TitledPane inventoryPane;
    @FXML private TitledPane subjectsPane;
    @FXML private TitledPane treatmentsPane;
    @FXML private TitledPane homePane;
    @FXML private TextField idField;
    @FXML private ScrollPane scrollPane;
    @FXML private BorderPane borderPane;
    @FXML private ProgressBar progressBar;
    @FXML private Label messageLabel;
    @FXML private ComboBox<String> findBox;
    @FXML private TitledPane findPane;
    @FXML private Menu speciesTypeMenu;
    @FXML private Menu subjectTypeMenu;
    @FXML private Menu supplierMenu;
    @FXML private MenuItem refreshItem;
    @FXML private HBox hBox;
    @FXML private MenuItem quitMenuItem;
    @FXML private MenuItem aboutMenuItem;
    @FXML private MenuBar menuBar;
    @FXML private VBox navigationBar;
    @FXML private MenuItem exportStockListItem;
    @FXML private MenuItem exportAnimalUseItem;
    @FXML private MenuItem exportPopulationItem;
    @FXML private Accordion accordion;

    private HashMap<String, TitledPane> panes;
    private HashMap<String, AbstractView> views;
    private HashMap<String, Pair<String, Tooltip>> paneAliasMap;
    private String currentView;

    public MainViewController() {
        URL url = Main.class.getResource("/animalkeeping/ui/fxml/MainView.fxml");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(url);
        loader.setController(this);
        try {
            this.getChildren().add(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuBar.setUseSystemMenuBar(true);

        if (System.getProperty("os.name").startsWith("Mac OS X")) {
            System.setProperty("bendalab.animalkeeping", "Animal Keeping");
            //Application.getApplication().setQuitHandler((quitEvent, quitResponse) -> closeApplication());
            //Application.getApplication().setQuitStrategy(QuitStrategy.CLOSE_ALL_WINDOWS);
            //Application.getApplication().setAboutHandler(new myAboutHandler());
            quitMenuItem.setVisible(false);
        }
        findBox.getItems().clear();
        findBox.getItems().addAll("Person", "Subject", "Housing unit", "Treatment");
        findBox.getSelectionModel().select("Subject");
        this.scrollPane.setContent(null);
        if (!Main.isConnected()) {
            LoginController login = new LoginController();
            login.addEventHandler(LoginController.DatabaseEvent.CONNECTING, this::connectedToDatabase);
            login.addEventHandler(LoginController.DatabaseEvent.CONNECTED, this::connectedToDatabase);
            login.addEventHandler(LoginController.DatabaseEvent.FAILED, this::connectedToDatabase);
            this.scrollPane.setContent(login);
        }
        else {
            try{
                connectedToDatabase(null);}
            catch(Exception e){
                e.printStackTrace();
            }
        }

        borderPane.prefHeightProperty().bind(this.prefHeightProperty());
        navigationBar.prefHeightProperty().bind(this.prefHeightProperty());

        paneAliasMap = new HashMap<>(7);
        paneAliasMap.put("Home", new Pair<>("home", QuickActions.getToolTip()));
        paneAliasMap.put("Inventory", new Pair<>("inventory", InventoryController.getToolTip()));
        paneAliasMap.put("Animals", new Pair<>("subject", SubjectView.getToolTip()));
        paneAliasMap.put("Treatments", new Pair<>("treatment", TreatmentsView.getToolTip()));
        paneAliasMap.put("Persons", new Pair<>("person", PersonsView.getToolTip()));
        paneAliasMap.put("Animal housing", new Pair<>("housing", HousingView.getToolTip()));
        paneAliasMap.put("Licenses", new Pair<>("license", LicenseView.getToolTip()));

        panes = new HashMap<>(7);
        panes.put("home", homePane);
        panes.put("inventory", inventoryPane);
        panes.put("subject", subjectsPane);
        panes.put("treatment", treatmentsPane);
        panes.put("person", personsPane);
        panes.put("housing", animalHousingPane);
        panes.put("license", licensesPane);

        for (String t : paneAliasMap.keySet()) {
            String alias = paneAliasMap.get(t).getKey();
            setupTiteldPane(panes.get(alias), t, alias, paneAliasMap.get(t).getValue());
        }
        refreshItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN));
        exportAnimalUseItem.setOnAction(event -> exportAnimalUse());
        exportStockListItem.setOnAction(event -> exportStockList());
        exportPopulationItem.setOnAction(event -> exportPopulation());
        views = new HashMap<>();
    }

    private void setupTiteldPane(TitledPane pane, String title, String alias, Tooltip ttip) {
        Label imgLabel = new Label(title, null);
        imgLabel.setTooltip(ttip);
        imgLabel.setContentDisplay(ContentDisplay.RIGHT);
        pane.setGraphic(imgLabel);
        pane.setGraphicTextGap(5.0);
        pane.setOnMouseClicked(event -> {
            showView(alias, pane.isExpanded());
        });
    }

    private Boolean viewIsCached(String name) {
        return views.containsKey(name);
    }

    private void cacheView(String name, AbstractView view) {
        if (!views.containsKey(name)) {
            views.put(name, view);
        }
    }

    protected void showView(String type, Boolean expand) {
        AbstractView view;
        if (viewIsCached(type)) {
            view = views.get(type);
        } else {
            view = createView(type);
        }
        if (view != null) {
            this.scrollPane.setContent(null);
            this.scrollPane.setContent(view);
            if (!panes.get(type).isExpanded()) {
                panes.get(type).setExpanded(true);
            }
            currentView = type;
            refreshView();
            view.requestFocus();
            if (type.equalsIgnoreCase("home") && accordion.getExpandedPane() != null) {
                accordion.getExpandedPane().setExpanded(false);
            }
            if (!type.equalsIgnoreCase("home")) {
                panes.get(type).setExpanded(expand);
                if (!expand) {
                    showView("home", true);
                }
            }
        }
    }

    private AbstractView createView(String type) {
        AbstractView view = null;
        switch (type) {
            case "inventory":
                view = new InventoryController();
                break;
            case "person":
                view = new PersonsView();
                break;
            case "subject":
                view = new SubjectView();
                break;
            case "treatment":
                view = new TreatmentsView();
                break;
            case "license":
                view = new LicenseView();
                break;
            case "housing":
                view = new HousingView();
                break;
            case "home":
                view = new QuickActions();
                break;
            default:
                setIdle("invalid view requested!", true);
        }
        if (view != null) {
            cacheView(type, view);
            view.prefHeightProperty().bind(this.borderPane.heightProperty());
            view.prefWidthProperty().bind(this.borderPane.widthProperty());
            view.addEventHandler(EventType.ROOT, this::handleViewEvents);
            panes.get(type).setContent(view.getControls());
        }
        return view;
    }

    private void handleViewEvents(Event event) {
        if (event.getEventType() == ViewEvent.REFRESHING) {
            setBusy("Refreshing view ...");
        } else if (event.getEventType() == ViewEvent.REFRESHED || event.getEventType() == ViewEvent.DONE) {
            setIdle(" ", false);
        } else if (event.getEventType() == ViewEvent.REFRESH_FAIL) {
            setIdle("refreshing failed!", true);
        } else if (event.getEventType() == ViewEvent.EXPORTING) {
            setBusy("Exporting data ...");
        }
    }

    private Long looksLikeId(String text) {
        Long aLong = null;
        try {
            aLong = Long.parseLong(text);
        } catch (NumberFormatException ignored) {}
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
                subjectsPane.setExpanded(true);
                SubjectView fv = (SubjectView) views.get("subjects");
                if (id != null) {
                    fv.idFilter(id);
                } else {
                    fv.nameFilter(idField.getText());
                }
                //this.scrollPane.setContent(fv);
                break;
            case "Person":
                personsPane.setExpanded(true);
                PersonsView pv = (PersonsView) views.get("persons");
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
    private  void closeApplication() {
        Main.getPrimaryStage().close();
    }

    /*
    @FXML
    private void disconnectFromDatabase() {
        Main.sessionFactory.close();
    }
    */

    @FXML
    private void newSubjectType() {
        Dialogs.editSubjectTypeDialog(null);
        fillSubjectTypeMenu();
    }

    @FXML
    private void newSpeciesType() {
        Dialogs.editSpeciesTypeDialog(null);
        fillSpeciesTypeMenu();
    }

    @FXML
    private void newSupplierType() {
        Dialogs.editSupplierTypeDialog(null);
        fillSupplierTypeMenu();
    }

    @FXML
    private void refreshView() {
        if (this.scrollPane.getContent() instanceof View) {
            Task<Void> refreshTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(50);
                    ((View) scrollPane.getContent()).refresh();
                    return null;
                }
            };
            refreshTask.setOnScheduled(event -> setBusy("refreshing ..."));
            refreshTask.setOnSucceeded(event -> setIdle("", false));

            new Thread(refreshTask).start();
        }
    }

    private void fillSubjectTypeMenu() {
        subjectTypeMenu.getItems().clear();
        List<SubjectType> subjectTypeList = EntityHelper.getEntityList("From SubjectType", SubjectType.class);
        MenuItem newSubjectItem = new MenuItem("new");
        newSubjectItem.setOnAction(event -> newSubjectType());
        subjectTypeMenu.getItems().add(newSubjectItem);
        subjectTypeMenu.getItems().add(new SeparatorMenuItem());

        for (SubjectType t : subjectTypeList) {
            Menu submenu = new Menu(t.getName());
            subjectTypeMenu.getItems().add(submenu);
            MenuItem editItem = new MenuItem("edit");
            editItem.setUserData(t);
            editItem.setOnAction(event -> editSubjectType((SubjectType) editItem.getUserData()));
            MenuItem deleteItem = new MenuItem("delete");
            deleteItem.setUserData(t);
            deleteItem.setOnAction(event -> deleteSubjectType((SubjectType) editItem.getUserData()));
            submenu.getItems().add(editItem);
            submenu.getItems().add(deleteItem);
        }
    }


    private void fillSpeciesTypeMenu() {
        speciesTypeMenu.getItems().clear();
        List<SpeciesType> speciesTypeList = EntityHelper.getEntityList("From SpeciesType", SpeciesType.class);

        MenuItem newSpeciesItem = new MenuItem("new");
        newSpeciesItem.setOnAction(event -> newSpeciesType());
        speciesTypeMenu.getItems().add(newSpeciesItem);
        speciesTypeMenu.getItems().add(new SeparatorMenuItem());

        for (SpeciesType t : speciesTypeList) {
            Menu submenu = new Menu(t.getName());
            speciesTypeMenu.getItems().add(submenu);
            MenuItem editItem = new MenuItem("edit");
            editItem.setUserData(t);
            editItem.setOnAction(event -> editSpeciesType((SpeciesType) editItem.getUserData()));
            MenuItem deleteItem = new MenuItem("delete");
            deleteItem.setUserData(t);
            deleteItem.setOnAction(event -> deleteSpeciesType((SpeciesType) editItem.getUserData()));
            submenu.getItems().add(editItem);
            submenu.getItems().add(deleteItem);
        }
    }


    private void fillSupplierTypeMenu() {
        supplierMenu.getItems().clear();
        List<SupplierType> supplier = EntityHelper.getEntityList("From SupplierType", SupplierType.class);
        MenuItem newSupplierItem = new MenuItem("new");
        newSupplierItem.setOnAction(event -> newSupplierType());
        supplierMenu.getItems().add(newSupplierItem);
        supplierMenu.getItems().add(new SeparatorMenuItem());

        for (SupplierType t : supplier) {
            Menu submenu = new Menu(t.getName());
            supplierMenu.getItems().add(submenu);
            MenuItem editItem = new MenuItem("edit");
            editItem.setUserData(t);
            editItem.setOnAction(event -> editSupplierType((SupplierType) editItem.getUserData()));
            MenuItem deleteItem = new MenuItem("delete");
            deleteItem.setUserData(t);
            deleteItem.setOnAction(event -> deleteSupplierType((SupplierType) editItem.getUserData()));
            submenu.getItems().add(editItem);
            submenu.getItems().add(deleteItem);
        }
    }


    private void fillMenus() {
        fillSubjectTypeMenu();
        fillSpeciesTypeMenu();
        fillSupplierTypeMenu();
    }


    private void connectedToDatabase(LoginController.DatabaseEvent event) {
        if (event == null)
            return;
        if(event.getEventType() == LoginController.DatabaseEvent.CONNECTED) {
            for (TitledPane p : panes.values())
                p.setDisable(false);
            subjectTypeMenu.setDisable(false);
            speciesTypeMenu.setDisable(false);
            supplierMenu.setDisable(false);
            fillMenus();
            refreshItem.setDisable(false);
            exportStockListItem.setDisable(false);
            exportAnimalUseItem.setDisable(false);
            exportPopulationItem.setDisable(false);
            showView("home", false);
            //inventoryPane.setExpanded(true);
            setIdle("Successfully connected to database!", false);
        } else if (event.getEventType() == LoginController.DatabaseEvent.CONNECTING) {
            setBusy("Connecting to database...");
        } else if (event.getEventType() == LoginController.DatabaseEvent.FAILED) {
            setIdle("Connection failed! " + event.getMessage(), true);
        }
    }

    private void editSubjectType(SubjectType t) {
        Dialogs.editSubjectTypeDialog(t);
    }

    private void deleteSubjectType(SubjectType t) {
        if (!EntityHelper.deleteEntity(t)) {
            Dialogs.showInfo("Subject type " + t.getName() + " could not be deleted. Probably referenced by other entries.");
            return;
        }
        fillSubjectTypeMenu();
    }

    private void editSpeciesType(SpeciesType t) {
        Dialogs.editSpeciesTypeDialog(t);
    }

    private void deleteSpeciesType(SpeciesType t) {
        if (!EntityHelper.deleteEntity(t)) {
            Dialogs.showInfo("Species " + t.getName() + " could not be deleted. Probably referenced by other entries.");
            return;
        }
        fillSpeciesTypeMenu();
    }


    private void editSupplierType(SupplierType t) {
        Dialogs.editSupplierTypeDialog(t);
    }

    private void deleteSupplierType(SupplierType t) {
        if (!EntityHelper.deleteEntity(t)) {
            Dialogs.showInfo("Supplier " + t.getName() + " could not be deleted. Probably referenced by other entries.");
            return;
        }
        fillSupplierTypeMenu();
    }

    @FXML
    private void showAbout() {
        Dialogs.showAboutDialog();
    }

    /*
    class myAboutHandler implements AboutHandler {
        @Override
        public void handleAbout(AppEvent.AboutEvent aboutEvent) {
            showAbout();
        }
    }
    */

    private void exportPopulation() {
        final HousingUnit unit = Dialogs.selectHousingUnit();
        Task<Void> exportTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(50);
                XlsxExport.exportPopulation(unit);
                return null;
            }
        };
        exportTask.setOnScheduled(event -> setBusy("exporting data ..."));
        exportTask.setOnSucceeded(event -> setIdle("", false));
        new Thread(exportTask).start();
    }


    private void exportAnimalUse() {
        final Pair<Date, Date> interval = Dialogs.getDateInterval();
        Task<Void> exportTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(50);
                XlsxExport.exportAnimalUse(interval);
                return null;
            }
        };
        exportTask.setOnScheduled(event -> setBusy("exporting data ..."));
        exportTask.setOnSucceeded(event -> setIdle("", false));
        new Thread(exportTask).start();
    }


    private void exportStockList() {
        final Pair<Date, Date> interval = Dialogs.getDateInterval();
        Task<Void> exportTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(50);
                XlsxExport.exportStockList(interval);
                return null;
            }
        };
        exportTask.setOnScheduled(event -> setBusy("exporting data ..."));
        exportTask.setOnSucceeded(event -> setIdle("", false));
        new Thread(exportTask).start();
    }


    private void setBusy(String message) {
        Platform.runLater(() -> {
            progressBar.setProgress(-1.0);
            messageLabel.setTextFill(Color.BLACK);
            messageLabel.setText(message != null ? message : "");
        });

    }

    private void setIdle(String message, boolean error) {
        Platform.runLater(() -> {
            progressBar.setProgress(0.);
            if (error) {
                messageLabel.setTextFill(Color.RED);
            } else {
                messageLabel.setTextFill(Color.BLACK);
            }
            messageLabel.setText(message != null ? message : "");
        });

    }
}