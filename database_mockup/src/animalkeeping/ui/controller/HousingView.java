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
import java.util.ResourceBundle;

import static animalkeeping.util.Dialogs.*;


public class HousingView extends VBox implements Initializable, View {
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

        tabVBox.prefHeightProperty().bind(unitsSplit.prefHeightProperty().subtract(unitsSplit.getDividers().get(0).positionProperty().multiply(unitsSplit.getPrefHeight())));
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> setSelectedUnit(newSelection != null ? newSelection.getValue() : null));
        table.prefWidthProperty().bind(this.prefWidthProperty().multiply(0.95));
        plotTabPane.prefWidthProperty().bind(this.prefWidthProperty().multiply(0.95));
        plotTabPane.prefHeightProperty().bind(tabVBox.prefHeightProperty());
        populationChart = new PopulationChart();
        populationChart.prefHeightProperty().bind(plotTabPane.prefHeightProperty().multiply(0.8));
        populationChart.prefWidthProperty().bind(this.prefWidthProperty().multiply(0.95));
        populationTab.setContent(populationChart);
        unitsSplit.prefHeightProperty().bind(this.prefHeightProperty().multiply(0.6));
        unitsSplit.prefWidthProperty().bind(this.prefWidthProperty());

        typesScrollPane.setContent(null);
        typesScrollPane.prefHeightProperty().bind(this.prefHeightProperty().multiply(0.3));
        housingTypes = new HousingTypeTable();
        housingTypes.prefWidthProperty().bind(this.prefWidthProperty());
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

        deleteTypeLabel = new ControlLabel("delete housing type", true);
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


    @Override
    public void refresh() {
        fillHousingTree();
        housingTypes.refresh();
    }


    private void editHousingUnit() {
        HousingUnit unit = table.getSelectionModel().getSelectedItem().getValue();
        editHousingUnitDialog(unit);
        fillHousingTree();
    }



    private void newHousingUnit() {
        HousingUnit unit = null;
        if(!table.getSelectionModel().isEmpty()) {
            unit = table.getSelectionModel().getSelectedItem().getValue();
        }
        editHousingUnitDialog(null, unit);
        fillHousingTree();
    }


    private void editHousingType() {
        HousingType ht = housingTypes.getSelectionModel().getSelectedItem();
        editHousingTypeDialog(ht);
        housingTypes.refresh();
    }


    private void newHousingType() {
        editHousingTypeDialog(null);
        housingTypes.refresh();
    }


    private void deleteHousingUnit() {
        HousingUnit h = table.getSelectionModel().getSelectedItem().getValue();
        if (!h.getHousings().isEmpty() || !h.getChildHousingUnits().isEmpty()) {
            showInfo("Cannot delete housing unit " + h.getName() + " since there are referring housing entries or child housing units!");
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
        TreeItem<HousingUnit> item = table.getSelectionModel().getSelectedItem();
        HousingUnit unit = item.getValue();
        importSubjectsDialog(unit);
        fillHousingTree();
        table.getSelectionModel().select(item);
    }


    private void batchTreatment() {
        HousingUnit unit = table.getSelectionModel().getSelectedItem().getValue();
        batchTreatmentDialog(unit);
    }


    @Override
    public VBox getControls() {
        return controls;
    }
}
