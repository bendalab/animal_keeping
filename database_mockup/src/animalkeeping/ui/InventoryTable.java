package animalkeeping.ui;

import animalkeeping.model.SpeciesType;
import animalkeeping.model.Treatment;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.Date;
import java.util.List;

/**
 * Created by jan on 01.01.17.
 */
public class InventoryTable extends TableView {
    private TableColumn<SpeciesType, Number> idCol;
    private TableColumn<SpeciesType, String> nameCol;
    private TableColumn<SpeciesType, String> trivialCol;
    private TableColumn<SpeciesType, Number> countCol;

    public InventoryTable() {
        idCol = new TableColumn<SpeciesType, Number>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        nameCol = new TableColumn<SpeciesType, String>("species");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        trivialCol = new TableColumn<SpeciesType, String>("trivial name");
        trivialCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTrivial()));
        countCol = new TableColumn<SpeciesType, Number>("count");
        countCol.setCellValueFactory(data -> new ReadOnlyIntegerWrapper(data.getValue().getCount()));

        this.getColumns().addAll(idCol, nameCol, trivialCol, countCol);
        init();
    }


    public InventoryTable(ObservableList items) {
        this();
        this.setItems(items);
    }


    private void init() {
        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            List result = session.createQuery("from SpeciesType").list();
            this.getItems().addAll(result);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }
    }
}
