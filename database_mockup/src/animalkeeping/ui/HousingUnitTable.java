package animalkeeping.ui;

import animalkeeping.logging.Communicator;
import animalkeeping.model.HousingUnit;
import animalkeeping.util.Dialogs;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.List;

import static animalkeeping.util.Dialogs.showInfo;

/**
 * Created by grewe on 2/14/17.
 */
public class HousingUnitTable extends TreeTableView<HousingUnit> {
    private MenuItem editItem, deleteItem, appendItem;


    public HousingUnitTable () {
        super();
        initialize();
    }

    public void initialize() {
        TreeTableColumn<HousingUnit, String> unitsColumn = new TreeTableColumn<>("housing unit");
        unitsColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HousingUnit, String> hu) ->
                new ReadOnlyStringWrapper(hu.getValue().getValue() != null ? hu.getValue().getValue().getName() : ""));
        unitsColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.25));

        TreeTableColumn<HousingUnit, String> typeColumn = new TreeTableColumn<>("type");
        typeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HousingUnit, String> hu) ->
                new ReadOnlyStringWrapper(hu.getValue().getValue() != null ? hu.getValue().getValue().getHousingType().getName() : ""));
        typeColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.2));

        TreeTableColumn<HousingUnit, String> dimensionColumn = new TreeTableColumn<>("dimension");
        dimensionColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HousingUnit, String> hu) ->
                new ReadOnlyStringWrapper(hu.getValue().getValue() != null ? hu.getValue().getValue().getDimensions() : ""));
        dimensionColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        TreeTableColumn<HousingUnit, String> descriptionColumn = new TreeTableColumn<>("description");
        descriptionColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HousingUnit, String> hu) ->
                new ReadOnlyStringWrapper(hu.getValue().getValue() != null ? hu.getValue().getValue().getDescription() : ""));
        descriptionColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.25));

        TreeTableColumn<HousingUnit, Number> populationColumn = new TreeTableColumn<>("population");
        populationColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<HousingUnit, Number> hu) ->
                new ReadOnlyIntegerWrapper(hu.getValue().getValue() != null ? hu.getValue().getValue().getAllHousings(true).size() : 0));
        populationColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.15));
        this.getColumns().addAll(unitsColumn, typeColumn, dimensionColumn, descriptionColumn, populationColumn);
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 2) {
                HousingUnit hu = getSelectionModel().getSelectedItem().getValue();
                hu = Dialogs.editHousingUnitDialog(hu);
                if (hu != null) {
                    refresh();
                    getSelectionModel().select(new TreeItem<>(hu));
                }
            }
            event.consume();
        });
        this.getSelectionModel().getSelectedItems().addListener((ListChangeListener<TreeItem<HousingUnit>>) c -> {
            int sel_count = c.getList().size();
            editItem.setDisable(sel_count == 0);
            deleteItem.setDisable(sel_count == 0);
            appendItem.setDisable(sel_count == 0);
        });

        ContextMenu cmenu = new ContextMenu();
        MenuItem newItem = new MenuItem("new housing unit");
        newItem.setOnAction(event -> editHousingUnit(null));

        editItem = new MenuItem("edit housing unit");
        editItem.setDisable(true);
        editItem.setOnAction(event -> editHousingUnit(getSelectionModel().getSelectedItem().getValue()));

        deleteItem = new MenuItem("delete housing unit");
        deleteItem.setDisable(true);
        deleteItem.setOnAction(event -> deleteHousingUnit(getSelectionModel().getSelectedItem().getValue()));

        appendItem = new MenuItem("append housing unit");
        appendItem.setDisable(true);
        appendItem.setOnAction(event -> appendHousingUnit(getSelectionModel().getSelectedItem().getValue()));

        cmenu.getItems().addAll(newItem, appendItem, editItem, deleteItem);
        setContextMenu(cmenu);
        fillHousingTree();
    }

    @Override
    public void refresh() {
        fillHousingTree();
        super.refresh();
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
        this.setRoot(root);
        this.setShowRoot(false);
    }


    private void fillRecursive(HousingUnit unit, TreeItem<HousingUnit> item) {
        for (HousingUnit hu : unit.getChildHousingUnits()) {
            TreeItem<HousingUnit> it = new TreeItem<>(hu);
            item.getChildren().add(it);
            fillRecursive(hu, it);
        }
    }

    public HousingUnit getSelectedUnit() {
        if (this.getSelectionModel().isEmpty()) {
            return null;
        }
        return this.getSelectionModel().getSelectedItem().getValue();
    }

    public void editHousingUnit(HousingUnit unit) {
        HousingUnit hu = Dialogs.editHousingUnitDialog(unit);
        if (hu != null) {
            refresh();
            getSelectionModel().select(new TreeItem<>(hu));
        }
    }

    public void deleteHousingUnit(HousingUnit unit) {
        if (unit == null)
            return;
        if (!unit.getHousings().isEmpty() || !unit.getChildHousingUnits().isEmpty()) {
            showInfo("Cannot delete housing unit " + unit.getName() + " since there are referring housing entries or child housing units!");
        } else {
            Communicator.pushDelete(unit);
        }
        getSelectionModel().select(null);
        refresh();
    }

    public void appendHousingUnit(HousingUnit parent) {
        HousingUnit hu = Dialogs.editHousingUnitDialog(null, parent);
        if (hu != null) {
            refresh();
            getSelectionModel().select(new TreeItem<>(hu));
        }
    }

}
