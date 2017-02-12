package animalkeeping.ui.controller;

import animalkeeping.model.HousingType;
import animalkeeping.model.HousingUnit;
import animalkeeping.ui.*;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;


public class HousingView extends VBox implements Initializable {
    @FXML private TextField dimensionsField;
    @FXML private TextField typeField;
    @FXML private TextField populationField;
    @FXML private TextField idField;
    @FXML private Label nameLabel;
    @FXML private TreeTableView<HousingUnit> table;
    @FXML private TreeTableColumn<HousingUnit, String> unitsColumn;
    @FXML private TreeTableColumn<HousingUnit, String> typeColumn;
    @FXML private TreeTableColumn<HousingUnit, String> dimensionColumn;
    @FXML private TreeTableColumn<HousingUnit, Number> populationColumn;
    @FXML private TreeTableColumn<HousingUnit, String> descriptionColumn;
    @FXML private Tab populationTab, historyTab;
    @FXML private TabPane plotTabPane;
    @FXML private ScrollPane typesScrollPane;
    @FXML private SplitPane unitsSplit;
    @FXML private VBox tabVBox;

    private PopulationChart populationChart;
    private HousingTypeTable housingTypes;
    private VBox controls;
    private ControlLabel editUnitLabel, deleteUnitLabel;
    private ControlLabel editTypeLabel, deleteTypeLabel;
    private ControlLabel importSubjectsLabel, batchTreatmentLabel;


    public HousingView () {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/HousingView.fxml"));
        loader.setController(this);
        try {
            this.getChildren().add(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.setFillWidth(true);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        unitsColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HousingUnit, String> hu) ->
                new ReadOnlyStringWrapper(hu.getValue().getValue() != null ? hu.getValue().getValue().getName() : ""));
        unitsColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
        typeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HousingUnit, String> hu) ->
                new ReadOnlyStringWrapper(hu.getValue().getValue() != null ? hu.getValue().getValue().getHousingType().getName() : ""));
        typeColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        dimensionColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HousingUnit, String> hu) ->
                new ReadOnlyStringWrapper(hu.getValue().getValue() != null ? hu.getValue().getValue().getDimensions() : ""));
        dimensionColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
        descriptionColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HousingUnit, String> hu) ->
                new ReadOnlyStringWrapper(hu.getValue().getValue() != null ? hu.getValue().getValue().getDescription() : ""));
        descriptionColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
        populationColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HousingUnit, Number> hu) ->
                new ReadOnlyIntegerWrapper(hu.getValue().getValue() != null ? hu.getValue().getValue().getAllHousings(true).size() : 0));
        populationColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> setSelectedUnit(newSelection != null ? newSelection.getValue() : null));

        plotTabPane.prefWidthProperty().bind(this.widthProperty());
        plotTabPane.prefHeightProperty().bind(tabVBox.heightProperty());
        populationChart = new PopulationChart();
        populationChart.prefHeightProperty().bind(plotTabPane.heightProperty());
        populationChart.minWidthProperty().bind(plotTabPane.prefWidthProperty().multiply(0.95));
        populationTab.setContent(populationChart);
        unitsSplit.prefHeightProperty().bind(this.heightProperty().multiply(0.66));
        unitsSplit.prefWidthProperty().bind(this.widthProperty());
        typesScrollPane.prefHeightProperty().bind(this.heightProperty().multiply(0.33));
        typesScrollPane.prefWidthProperty().bind(this.widthProperty());

        typesScrollPane.setContent(null);
        housingTypes = new HousingTypeTable();
        housingTypes.prefWidthProperty().bind(typesScrollPane.widthProperty());
        housingTypes.prefHeightProperty().bind(typesScrollPane.heightProperty());
        housingTypes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        housingTypes.getSelectionModel().selectedItemProperty().addListener((ov, old_val, new_val) -> setSelectedType(ov != null ? ov.getValue() : null));
        typesScrollPane.setContent(housingTypes);

        controls = new VBox();
        controls.setAlignment(Pos.TOP_LEFT);
        controls.setSpacing(5);

        ControlLabel newUnitLabel = new ControlLabel("new housing unit");
        newUnitLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                newHousingUnit();
            }
        });
        controls.getChildren().add(newUnitLabel);

        editUnitLabel = new ControlLabel("edit housing unit", true);
        editUnitLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                editHousingUnit();
            }
        });
        controls.getChildren().add(editUnitLabel);

        deleteUnitLabel = new ControlLabel("delete housing unit", true);
        deleteUnitLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                deleteHousingUnit();
            }
        });
        controls.getChildren().add(deleteUnitLabel);

        controls.getChildren().add(new Separator(Orientation.HORIZONTAL));

        ControlLabel newTypeLabel = new ControlLabel("new housing type");
        newTypeLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                newHousingType();
            }
        });
        controls.getChildren().add(newTypeLabel);

        editTypeLabel = new ControlLabel("edit housing type", true);
        editTypeLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                editHousingType();
            }
        });
        controls.getChildren().add(editTypeLabel);

        deleteTypeLabel = new ControlLabel("delete housing unit", true);
        deleteTypeLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                deleteHousingType();
            }
        });
        controls.getChildren().add(deleteTypeLabel);
        controls.getChildren().add(new Separator(Orientation.HORIZONTAL));

        importSubjectsLabel = new ControlLabel("import subjects", true);
        importSubjectsLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                importSubjects();
            }
        });
        controls.getChildren().add(importSubjectsLabel);

        batchTreatmentLabel = new ControlLabel("batch treatment", true);
        batchTreatmentLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                batchTreatment();
            }
        });
        controls.getChildren().add(batchTreatmentLabel);

        refresh();
    }


    private void fillHousingTree() {
        final TreeItem<HousingUnit> root = new TreeItem<>();
        root.setExpanded(true);
        Session session = Main.sessionFactory.openSession();
        List<HousingUnit> housingUnits = null;
        try {
            session.beginTransaction();
            housingUnits = session.createQuery("from HousingUnit where parentUnit is null", HousingUnit.class).list();
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }
        if (housingUnits == null) {
            return;
        }
        for (HousingUnit hu : housingUnits) {
            TreeItem<HousingUnit> child = new TreeItem<>(hu);
            root.getChildren().add(child);
            fillRecursive(hu, child);
        }
        table.setRoot(root);
        table.setShowRoot(false);
    }


    private void fillRecursive(HousingUnit unit, TreeItem<HousingUnit> item) {
        for (HousingUnit hu : unit.getChildHousingUnits()) {
            TreeItem<HousingUnit> it = new TreeItem<>(hu);
            item.getChildren().add(it);
            fillRecursive(hu, it);
        }
    }


    private void setSelectedUnit(HousingUnit unit) {
        populationChart.listPopulation(unit);
        deleteUnitLabel.setDisable(unit == null);
        editUnitLabel.setDisable(unit == null);
        importSubjectsLabel.setDisable(unit == null);
        batchTreatmentLabel.setDisable(unit == null);
    }


    private void setSelectedType(HousingType ht) {
        deleteTypeLabel.setDisable(ht == null);
        editTypeLabel.setDisable(ht == null);
    }


    private void refresh() {
        fillHousingTree();
        housingTypes.refresh();
    }


    private void editHousingUnit() {
        HousingUnit unit = table.getSelectionModel().getSelectedItem().getValue();
        showEditUnitDialog(unit);
        fillHousingTree();
    }


    public static void showEditUnitDialog(HousingUnit unit) {
        HousingUnitDialog hud = new HousingUnitDialog(unit);
        Dialog<HousingUnit> dialog = new Dialog<>();
        dialog.setTitle("Housing unit");
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(hud);
        hud.prefWidthProperty().bind(dialog.widthProperty());
        dialog.setWidth(200);

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return hud.getHousingUnit();
            }
            return null;
        });
        Optional<HousingUnit> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                Session session = Main.sessionFactory.openSession();
                session.beginTransaction();
                session.saveOrUpdate(result.get());
                session.getTransaction().commit();
                session.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void editHousingType() {
        HousingType ht = housingTypes.getSelectionModel().getSelectedItem();
        showEditTypeDialog(ht);
        housingTypes.refresh();
    }


    private void newHousingUnit() {
        showEditUnitDialog(null);
        fillHousingTree();
    }


    private void newHousingType() {
        showEditTypeDialog(null);
        housingTypes.refresh();
    }


    public static void showEditTypeDialog(HousingType type) {
        HousingTypeDialog htd = new HousingTypeDialog(type);
        Dialog<HousingType> dialog = new Dialog<>();
        dialog.setTitle("Housing type");
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(htd);
        dialog.setWidth(200);
        htd.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return htd.getHousingType();
            }
            return null;
        });
        Optional<HousingType> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                Session session = Main.sessionFactory.openSession();
                session.beginTransaction();
                session.saveOrUpdate(result.get());
                session.getTransaction().commit();
                session.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void deleteHousingUnit() {
        HousingUnit h = table.getSelectionModel().getSelectedItem().getValue();
        if (!h.getHousings().isEmpty()) {
            showInfo("Cannot delete housing unit " + h.getName() + " since it is referenced by " +
                    Integer.toString(h.getHousings().size()) + " housing entries!");
        } else {
            Session session = Main.sessionFactory.openSession();
            session.beginTransaction();
            session.delete(h);
            session.getTransaction().commit();
            session.close();
        }
        table.getSelectionModel().select(null);
        fillHousingTree();
    }


    private void deleteHousingType() {
        HousingType ht = housingTypes.getSelectionModel().getSelectedItem();
        if (!ht.getHousingUnits().isEmpty()) {
            showInfo("Cannot delete housing type " + ht.getName() + " since it is referenced by " +
                    Integer.toString(ht.getHousingUnits().size()) + " housing units!");
        } else {
            Session session = Main.sessionFactory.openSession();
            session.beginTransaction();
            session.delete(ht);
            session.getTransaction().commit();
            session.close();
        }
        housingTypes.getSelectionModel().select(null);
        housingTypes.refresh();
    }


    private void importSubjects() {
        HousingUnit unit = table.getSelectionModel().getSelectedItem().getValue();
        AddSubjectsForm htd = new AddSubjectsForm(unit);
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Import subjects");
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(htd);
        dialog.setWidth(300);
        htd.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return htd.persistSubjects();
            }
            return null;
        });

        Optional<Boolean> result = dialog.showAndWait();
        if (!result.isPresent() || !result.get()) {
            showInfo("Something went wrong while creating new subjects!");
        } else {
            showInfo("Successfully created new subjects!");
        }
    }


    private void batchTreatment() {
        HousingUnit unit = table.getSelectionModel().getSelectedItem().getValue();
        BatchTreatmentForm btf = new BatchTreatmentForm(unit);
        Dialog<Boolean> dialog = new Dialog<>();


        dialog.setTitle("Batch Treatment");
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(btf);
        dialog.setWidth(300);
        btf.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return btf.persist();
            }
            return null;
        });

        Optional<Boolean> result = dialog.showAndWait();
        if (!result.isPresent() || !result.get()) {
            showInfo("Something went wrong while creating the treatment!");
        } else {
            showInfo("Successfully created a batch treatment!");
        }
    }


    VBox getControls() {
        return controls;
    }


    private void showInfo(String  info) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(info);
        alert.show();
    }
}
