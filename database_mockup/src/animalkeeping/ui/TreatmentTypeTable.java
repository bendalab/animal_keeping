package animalkeeping.ui;

import animalkeeping.model.Subject;
import animalkeeping.model.TreatmentType;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.sun.tools.corba.se.idl.constExpr.BooleanNot;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.List;
import java.util.Set;

/**
 * Created by jan on 19.02.17.
 */
public class TreatmentTypeTable extends TableView<TreatmentType> {
    private TableColumn<TreatmentType, Number> idCol;
    private TableColumn<TreatmentType, String> nameCol;
    private TableColumn<TreatmentType, Boolean> invasiveCol;
    private TableColumn<TreatmentType, String> licenseCol;
    private TableColumn<TreatmentType, String> descriptionCol;
    private ObservableList<TreatmentType> masterList = FXCollections.observableArrayList();
    private FilteredList<TreatmentType> filteredList;


    public TreatmentTypeTable() {
        super();
        idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        idCol.prefWidthProperty().bind(this.widthProperty().multiply(0.07));

        nameCol = new TableColumn<>("name");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        nameCol.prefWidthProperty().bind(this.widthProperty().multiply(0.20));

        licenseCol = new TableColumn<>("license");
        licenseCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getLicense() != null ? data.getValue().getLicense().getName() : ""));
        licenseCol.prefWidthProperty().bind(this.widthProperty().multiply(0.20));

        invasiveCol = new TableColumn<>("is invasive");
        invasiveCol.setCellValueFactory(data -> new ReadOnlyBooleanWrapper(data.getValue().isInvasive()));
        invasiveCol.prefWidthProperty().bind(this.widthProperty().multiply(0.08));

        descriptionCol = new TableColumn<>("description");
        descriptionCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDescription() != null ?
                data.getValue().getDescription() : ""));
        descriptionCol.prefWidthProperty().bind(this.widthProperty().multiply(0.44));

        this.getColumns().addAll(idCol, nameCol, licenseCol, invasiveCol, descriptionCol);
        init();
    }


    public TreatmentTypeTable(ObservableList<TreatmentType> items) {
        this();
        this.setItems(items);
    }


    private void init() {
        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            List<TreatmentType> result = session.createQuery("from TreatmentType", TreatmentType.class).list();
            masterList.addAll(result);
            filteredList = new FilteredList<>(masterList, p -> true);
            SortedList<TreatmentType> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(this.comparatorProperty());
            this.setItems(sortedList);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }
    }

    public void setTreatmentTypes(Set<TreatmentType> types) {
        this.masterList.clear();
        this.masterList.addAll(types);
    }

}

