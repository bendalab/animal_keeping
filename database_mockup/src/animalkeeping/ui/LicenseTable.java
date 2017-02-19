package animalkeeping.ui;

import animalkeeping.model.License;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by jan on 18.02.17.
 */
public class LicenseTable extends TableView<License> {
    private TableColumn<License, Number> idCol;
    private TableColumn<License, String> nameCol;
    private TableColumn<License, String> fileNumberCol;
    private TableColumn<License, Date> startDateCol;
    private TableColumn<License, Date> endDateCol;
    private ObservableList<License> masterList = FXCollections.observableArrayList();
    private FilteredList<License> filteredList;

    public LicenseTable() {
        super();
        init();
    }

    public LicenseTable(ObservableList<License> items) {
        this();
        this.setItems(items);
    }

    private void init() {
        idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        idCol.prefWidthProperty().bind(this.widthProperty().multiply(0.08));

        nameCol = new TableColumn<>("name");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        nameCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        startDateCol= new TableColumn<>("from");
        startDateCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Date>(data.getValue().getStartDate()));
        startDateCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        endDateCol= new TableColumn<>("from");
        endDateCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Date>(data.getValue().getEndDate()));
        endDateCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        fileNumberCol = new TableColumn<>("person");
        fileNumberCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getNumber()));
        fileNumberCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        this.getColumns().addAll(idCol, nameCol, fileNumberCol, startDateCol, endDateCol);

        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            List<License> result = session.createQuery("from License", License.class).list();
            masterList.addAll(result);
            filteredList = new FilteredList<>(masterList, p -> true);
            SortedList<License> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(this.comparatorProperty());
            setItems(sortedList);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }
    }


    public void setLicenses(Set<License> licenses) {
        this.getItems().clear();
        this.getItems().addAll(licenses);
    }
}
