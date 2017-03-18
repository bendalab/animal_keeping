package animalkeeping.ui;

import animalkeeping.model.Migrations;
import animalkeeping.util.EntityHelper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by jan on 18.03.17.
 */
public class MigrationTable extends TableView<Migrations> {
    private TableColumn<Migrations, Number> idCol;
    private TableColumn<Migrations, String> nameCol;
    private TableColumn<Migrations, Date> migrationDateCol;
    private ObservableList<Migrations> masterList = FXCollections.observableArrayList();


    public MigrationTable() {
        super();
        init();
        refresh();
    }


    public MigrationTable(ObservableList<Migrations> items) {
        this();
        this.masterList = items;
    }


    private void init() {
        idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        idCol.prefWidthProperty().bind(this.widthProperty().multiply(0.10));

        nameCol = new TableColumn<>("patch name");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        nameCol.prefWidthProperty().bind(this.widthProperty().multiply(0.695));

        migrationDateCol = new TableColumn<>("from");
        migrationDateCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getMigrationDate()));
        migrationDateCol.prefWidthProperty().bind(this.widthProperty().multiply(0.2));

        this.getColumns().addAll(idCol, nameCol, migrationDateCol);
    }


    @Override
    public void refresh() {
        List<Migrations> result = EntityHelper.getEntityList("from Migrations", Migrations.class);
        setMigrations(result);
        super.refresh();
    }


    public void setMigrations(Collection<Migrations> migrations) {
        masterList.clear();
        if (migrations != null) {
            masterList.addAll(migrations);
        }
    }
}
