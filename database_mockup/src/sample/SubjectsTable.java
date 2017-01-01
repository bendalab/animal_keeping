package sample;

import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.List;

/**
 * Created by jan on 01.01.17.
 */
public class SubjectsTable extends TableView {
    private TableColumn<model.Subject, Number> idCol;
    private TableColumn<model.Subject, String> nameCol;
    private TableColumn<model.Subject, String> aliasCol;
    private TableColumn<model.Subject, String> speciesCol;
    private TableColumn<model.Subject, String> subjectCol;
    private TableColumn<model.Subject, String> supplierCol;


    public SubjectsTable() {
        super();
        idCol = new TableColumn<model.Subject, Number>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        nameCol = new TableColumn<model.Subject, String>("name");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        aliasCol = new TableColumn<model.Subject, String>("alias");
        aliasCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getAlias()));
        speciesCol = new TableColumn<model.Subject, String>("species");
        //speciesCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSpeciesType().getName()));
        subjectCol = new TableColumn<model.Subject, String>("subject");
        //subjectCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSubjectType().getName()));
        supplierCol = new TableColumn<model.Subject, String>("supplier");
        //supplierCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSupplier().getName()));
        this.getColumns().addAll(idCol, nameCol, aliasCol, speciesCol, subjectCol, supplierCol);
        init();
    }

    public SubjectsTable(ObservableList<model.Subject> items) {
        this();
        this.setItems(items);
    }

    private void init() {
        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            List result = session.createQuery("from Subject").list();
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

