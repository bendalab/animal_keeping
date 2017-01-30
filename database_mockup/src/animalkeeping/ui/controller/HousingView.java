package animalkeeping.ui.controller;

import animalkeeping.model.Housing;
import animalkeeping.model.HousingType;
import animalkeeping.model.HousingUnit;
import animalkeeping.ui.Main;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
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
    @FXML private TreeTableColumn<HousingUnit, String> descriptionColumn;
    @FXML private TreeTableColumn<HousingUnit, Number> populationColumn;
    @FXML private ListView<HousingType> typesList;
    @FXML private Label unitTypeLabel;
    @FXML private TextArea typeDescription;
    @FXML private TextField typeIdField;
    @FXML private TabPane tabPane;

    private VBox controls;
    private Button editBtn, deleteBtn, newBtn;

    public HousingView () {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/HousingView.fxml"));
        loader.setController(this);
        try {
            this.getChildren().add(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.setVgrow(table, Priority.ALWAYS);
        this.setFillWidth(true);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        unitsColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HousingUnit, String> hu) ->
                new ReadOnlyStringWrapper(hu.getValue().getValue() != null ? hu.getValue().getValue().getName() : ""));
        unitsColumn.setPrefWidth(200);
        descriptionColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HousingUnit, String> hu) ->
                new ReadOnlyStringWrapper(hu.getValue().getValue() != null ? hu.getValue().getValue().getDescription() : ""));
        descriptionColumn.setPrefWidth(200);
        populationColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HousingUnit, Number> hu) ->
                new ReadOnlyIntegerWrapper(hu.getValue().getValue() != null ? hu.getValue().getValue().getAllHousings(true).size() : 0));
        populationColumn.setPrefWidth(50);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                setSelectedUnit(obs.getValue().getValue());
            } else {
                setSelectedUnit(null);
            }
        });

        controls = new VBox();
        controls.setAlignment(Pos.CENTER);
        //controls.setPadding(new Insets(0.0, 0.0, 10.0, 0.0));
        controls.setSpacing(10);
        Label heading = new Label("Housing controls:");
        heading.setUnderline(true);
        controls.getChildren().add(heading);

        newBtn = new Button();
        newBtn.setPrefWidth(100);
        newBtn.setOnAction(event -> newBtnPressed());
        newBtn.setText("new");
        controls.getChildren().add(newBtn);

        editBtn = new Button();
        editBtn.setPrefWidth(100);
        editBtn.setOnAction(event -> editBtnPressed());
        editBtn.setText("edit");
        controls.getChildren().add(editBtn);

        deleteBtn = new Button();
        deleteBtn.setText("delete");
        deleteBtn.setPrefWidth(100);
        deleteBtn.setOnAction(event -> deleteBtnPressed());
        controls.getChildren().add(deleteBtn);

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


    private void fillTypes() {
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
    }


    private void fillRecursive(HousingUnit unit, TreeItem<HousingUnit> item) {
        for (HousingUnit hu : unit.getChildHousingUnits()) {
            TreeItem<HousingUnit> it = new TreeItem<>(hu);
            item.getChildren().add(it);
            fillRecursive(hu, it);
        }
    }

    private void setSelectedUnit(HousingUnit unit) {
        if (unit != null) {
            nameLabel.setText(unit.getName());
            idField.setText(unit.getId().toString());
            dimensionsField.setText(unit.getDimensions());
            populationField.setText(unit.getPopulation().toString());
            typeField.setText((unit.getHousingType().getName()));
        } else {
            nameLabel.setText("");
            idField.setText("");
            dimensionsField.setText("");
            populationField.setText("");
            typeField.setText("");
        }
    }

    private void setSelectedType(HousingType ht) {
        if (ht != null) {
            unitTypeLabel.setText(ht.getName());
            typeIdField.setText(ht.getId().toString());
            typeDescription.setText(ht.getDescription());
        } else {
            unitTypeLabel.setText(ht.getName());
            typeIdField.setText(ht.getId().toString());
            typeDescription.setText(ht.getDescription());
        }
    }

    private void refresh() {
        fillHousingTree();
        fillTypes();
    }

    private void editBtnPressed() {

    }

    private void newBtnPressed() {
        String tabName = this.tabPane.getSelectionModel().getSelectedItem().getText();
        if (tabName.contains("types")) {
            newHousingType();
        } else {
            newHousingUnit();
        }
        refresh();
    }

    private void newHousingUnit() {
        HousingType t = typesList.getItems().get(0);
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
        System.out.println("new unit!");
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
        System.out.println("new type!");

    }

    private void deleteBtnPressed() {
        String tabName = this.tabPane.getSelectionModel().getSelectedItem().getText();
        if (tabName.contains("types")) {
            deleteType();
        } else {
            deleteHousing();
        }
        refresh();
    }

    private void deleteHousing() {
        HousingUnit h = table.getSelectionModel().getSelectedItem().getValue();
        if (!h.getHousings().isEmpty()) {
            showInfo("Cannot delete housing unit " + h.getName() + " since it is referenced by " +
                    Integer.toString(h.getHousings().size()) + " housing entries!");
        } else {
            Main.sessionFactory.openSession().delete(h);
        }
    }

    private void deleteType() {
        HousingType ht = typesList.getSelectionModel().getSelectedItem();
        if (!ht.getHousingUnits().isEmpty()) {
            showInfo("Cannot delete housing type " + ht.getName() + " since it is referenced by " +
                    Integer.toString(ht.getHousingUnits().size()) + " housing units!");
        } else {
            Main.sessionFactory.openSession().delete(ht);
        }
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
