package animalkeeping.ui;

import animalkeeping.model.*;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.List;


/**
 * Created by huben on 10.01.17.
 */
public class IndividualTable extends javafx.scene.control.TableView {

    private Integer id;
    private TableColumn<Subject, Number> idCol;
    private TableColumn<Subject, String> nameCol;
    private TableColumn<Subject, String> aliasCol;
    private TableColumn<Subject, String> speciesCol;
    private TableColumn<Subject, String> subjectCol;
    private TableColumn<Subject, String> supplierCol;

    public IndividualTable(int id){
        super();
        this.id = id;
        idCol = new TableColumn<Subject, Number>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        nameCol = new TableColumn<Subject, String>("name");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        aliasCol = new TableColumn<Subject, String>("alias");
        aliasCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getAlias()));
        speciesCol = new TableColumn<Subject, String>("species");
        speciesCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSpeciesType().getName()));
        subjectCol = new TableColumn<Subject, String>("subject");
        subjectCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSubjectType().getName()));
        supplierCol = new TableColumn<Subject, String>("supplier");
        supplierCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSupplier().getName()));
        this.getColumns().addAll(idCol, nameCol, aliasCol, speciesCol, subjectCol, supplierCol);
        init();
    }



    public void init(){
        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();

            List<Subject> result = session.createQuery("from Subject where id = " + this.id.toString()).list();

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
