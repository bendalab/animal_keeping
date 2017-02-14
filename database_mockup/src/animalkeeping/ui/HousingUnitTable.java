package animalkeeping.ui;

import animalkeeping.model.HousingUnit;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.VBox;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by grewe on 2/14/17.
 */
public class HousingUnitTable extends VBox {
    private TreeTableView<HousingUnit> table;

    public HousingUnitTable () {
        this.setFillWidth(true);
        initialize();
    }

    public void initialize() {
        table = new TreeTableView<>();

        TreeTableColumn<HousingUnit, String> unitsColumn = new TreeTableColumn<>("housing unit");
        unitsColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HousingUnit, String> hu) ->
                new ReadOnlyStringWrapper(hu.getValue().getValue() != null ? hu.getValue().getValue().getName() : ""));
        unitsColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.25));

        TreeTableColumn<HousingUnit, String> typeColumn = new TreeTableColumn<>("type");
        typeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HousingUnit, String> hu) ->
                new ReadOnlyStringWrapper(hu.getValue().getValue() != null ? hu.getValue().getValue().getHousingType().getName() : ""));
        typeColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.2));

        TreeTableColumn<HousingUnit, String> dimensionColumn = new TreeTableColumn<>("dimension");
        dimensionColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HousingUnit, String> hu) ->
                new ReadOnlyStringWrapper(hu.getValue().getValue() != null ? hu.getValue().getValue().getDimensions() : ""));
        dimensionColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.15));

        TreeTableColumn<HousingUnit, String> descriptionColumn = new TreeTableColumn<>("description");
        descriptionColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HousingUnit, String> hu) ->
                new ReadOnlyStringWrapper(hu.getValue().getValue() != null ? hu.getValue().getValue().getDescription() : ""));
        descriptionColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.25));

        TreeTableColumn<HousingUnit, Number> populationColumn = new TreeTableColumn<>("population");
        populationColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HousingUnit, Number> hu) ->
                new ReadOnlyIntegerWrapper(hu.getValue().getValue() != null ? hu.getValue().getValue().getAllHousings(true).size() : 0));
        populationColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
        table.getColumns().addAll(unitsColumn, typeColumn, dimensionColumn, descriptionColumn, populationColumn);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.getChildren().add(table);
        fillHousingTree();

        //table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> setSelectedUnit(newSelection != null ? newSelection.getValue() : null));
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

    public HousingUnit getSelectedUnit() {
        if (table.getSelectionModel().isEmpty()) {
            return null;
        }
        return table.getSelectionModel().getSelectedItem().getValue();
    }

}
