package animalkeeping.ui.controller;

import animalkeeping.model.HousingType;
import animalkeeping.model.HousingUnit;
import animalkeeping.ui.HousingTypeTable;
import animalkeeping.ui.Main;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by jan on 28.01.17.
 */
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
    private Button editUnitBtn, deleteUnitBtn, newUnitBtn;
    private Button editTypeBtn, deleteTypeBtn, newTypeBtn;


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
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                setSelectedUnit(obs.getValue().getValue());
            } else {
                setSelectedUnit(null);
            }
        });
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
        typesScrollPane.setContent(housingTypes);

        controls = new VBox();
        controls.setAlignment(Pos.CENTER);
        controls.setSpacing(10);
        Label heading = new Label("Housing controls:");
        heading.setUnderline(true);
        controls.getChildren().add(heading);

        newUnitBtn = new Button();
        newUnitBtn.setPrefWidth(100);
        newUnitBtn.setOnAction(event -> newHousingUnit());
        newUnitBtn.setText("new unit");
        controls.getChildren().add(newUnitBtn);

        editUnitBtn = new Button();
        editUnitBtn.setPrefWidth(100);
        editUnitBtn.setOnAction(event -> editHousingUnit());
        editUnitBtn.setText("edit unit");
        controls.getChildren().add(editUnitBtn);

        deleteUnitBtn = new Button();
        deleteUnitBtn.setText("delete unit");
        deleteUnitBtn.setPrefWidth(100);
        deleteUnitBtn.setOnAction(event -> deleteHousingUnit());
        controls.getChildren().add(deleteUnitBtn);

        newTypeBtn = new Button();
        newTypeBtn.setPrefWidth(100);
        newTypeBtn.setOnAction(event -> newHousingType());
        newTypeBtn.setText("new type");
        controls.getChildren().add(newTypeBtn);

        editTypeBtn = new Button();
        editTypeBtn.setPrefWidth(100);
        editTypeBtn.setOnAction(event -> editHousingType());
        editTypeBtn.setText("edit type");
        controls.getChildren().add(editTypeBtn);

        deleteTypeBtn = new Button();
        deleteTypeBtn.setText("delete type");
        deleteTypeBtn.setPrefWidth(100);
        deleteTypeBtn.setOnAction(event -> deleteHousingType());
        controls.getChildren().add(deleteTypeBtn);
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


   /* private void fillTypes() {
        Session session = Main.sessionFactory.openSession();
        List<HousingType> housingTypes = null;
        try {
            session.beginTransaction();
            housingTypes = session.createQuery("from HousingType", HousingType.class).list();
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }
        typesList.getItems().clear();
        typesList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        typesList.setCellFactory(new Callback<ListView<HousingType>, ListCell<HousingType>>() {
            @Override
            public ListCell<HousingType> call(ListView<HousingType> p) {
                return new ListCell<HousingType>() {
                    @Override
                    protected void updateItem(HousingType t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.getName());
                        }
                    }
                };
            }
        });
        typesList.getSelectionModel().selectedItemProperty().addListener((ov, old_val, new_val) -> setSelectedType(ov.getValue()));
        if (housingTypes != null) {
            typesList.getItems().addAll(housingTypes);
        }
    }*/


    private void fillRecursive(HousingUnit unit, TreeItem<HousingUnit> item) {
        for (HousingUnit hu : unit.getChildHousingUnits()) {
            TreeItem<HousingUnit> it = new TreeItem<>(hu);
            item.getChildren().add(it);
            fillRecursive(hu, it);
        }
    }

    private void setSelectedUnit(HousingUnit unit) {
        populationChart.listPopulation(unit);
    }

    private void setSelectedType(HousingType ht) {
        deleteTypeBtn.setDisable(ht != null);
        editTypeBtn.setDisable(ht != null);
    }

    private void refresh() {
        fillHousingTree();
        housingTypes.refresh();
    }

    private void editHousingUnit() {

    }


    private void editHousingType() {

    }


    private void newHousingUnit() {
        HousingType t = housingTypes.getItems().get(0);
        HousingUnit test = new HousingUnit();
        test.setName("Test");
        test.setDescription("A simple test unit that can be safely deleted.");

        test.setHousingType(t);
        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(test);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }
        refresh();
    }


    private void newHousingType() {
        HousingType test = new HousingType();
        test.setName("Test");
        test.setDescription("A simple test type that can be safely deleted.");
        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(test);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }
        refresh();
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
        refresh();
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
        refresh();
    }

    public VBox getControls() {
        return controls;
    }

    private void showInfo(String  info) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(info);
        alert.show();

    }
}
