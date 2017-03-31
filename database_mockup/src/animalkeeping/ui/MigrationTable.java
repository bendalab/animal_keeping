package animalkeeping.ui;

import animalkeeping.model.Migration;
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
public class MigrationTable extends TableView<Migration> {
    private TableColumn<Migration, Number> idCol;
    private TableColumn<Migration, String> nameCol;
    private TableColumn<Migration, Date> migrationDateCol;
    private ObservableList<Migration> masterList = FXCollections.observableArrayList();


    public MigrationTable() {
        super();
        init();
        refresh();
    }


    public MigrationTable(ObservableList<Migration> items) {
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
        List<Migration> result = EntityHelper.getEntityList("from Migration", Migration.class);
        setMigrations(result);
        super.refresh();
    }


    public void setMigrations(Collection<Migration> migrations) {
        masterList.clear();
        if (migrations != null) {
            masterList.addAll(migrations);
        }
    }
}
