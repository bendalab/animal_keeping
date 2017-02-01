package animalkeeping.ui;

import animalkeeping.model.Subject;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.List;

public class FishTable extends TableView<Subject> {
    private TableColumn<Subject, Number> idCol;
    private TableColumn<Subject, String> aliasCol;
    private TableColumn<Subject, String> speciesCol;
    private TableColumn<Subject, String> supplierCol;

    public FishTable() {
        super();
        idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        aliasCol = new TableColumn<>("name");
        aliasCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        speciesCol = new TableColumn<>("species");
        speciesCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSpeciesType().getName()));
        supplierCol = new TableColumn<>("supplier");
        supplierCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSupplier().getName()));
        this.getColumns().addAll(idCol, aliasCol, speciesCol, supplierCol);
        init();
    }

   /* public PersonsTable(ObservableList<Person> items) {
        this();
        this.setItems(items);
    }*/

    private void init() {

        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            List<Subject> result = session.createQuery("from Subject", Subject.class).list();
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
