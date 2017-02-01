package animalkeeping.ui;

import animalkeeping.model.*;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.util.Callback;
import javafx.scene.input.MouseEvent;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.Parent;

/**
 * Created by jan on 01.01.17.
 */
public class SubjectsTable extends TableView<Subject> {
    private TableColumn<Subject, Number> idCol;
    private TableColumn<Subject, String> nameCol;
    private TableColumn<Subject, String> aliasCol;
    private TableColumn<Subject, String> speciesCol;
    private TableColumn<Subject, String> subjectCol;
    private TableColumn<Subject, String> supplierCol;
    private ObservableList<Subject> masterList = FXCollections.observableArrayList();
    private FilteredList<Subject> filteredList;

    public SubjectsTable() {
        super();
        idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        nameCol = new TableColumn<>("name");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        //nameCol.setCellFactory(subjectCellFactory);
        nameCol.setCellFactory(new CellFactoryProvider(getScene()).c);
        aliasCol = new TableColumn<>("alias");
        aliasCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getAlias()));
        speciesCol = new TableColumn<>("species");
        speciesCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSpeciesType().getName()));
        subjectCol = new TableColumn<>("subject");
        subjectCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSubjectType().getName()));
        supplierCol = new TableColumn<>("supplier");
        supplierCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSupplier().getName()));
        this.getColumns().addAll(idCol, nameCol, aliasCol, speciesCol, subjectCol, supplierCol);
        init();
    }

    public SubjectsTable(ObservableList<Subject> items) {
        this();
        this.setItems(items);
    }

    private void init() {

        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            List<Subject> result = session.createQuery("from Subject", Subject.class).list();
            masterList.addAll(result);
            filteredList = new FilteredList<>(masterList, p -> true);
            SortedList<Subject> sortedList = new SortedList<>(filteredList);
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


    public void setNameFilter(String name) {
        filteredList.setPredicate(subject -> {
            if (name == null || name.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = name.toLowerCase();
            if (subject.getName().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else if (subject.getAlias() != null && subject.getAlias().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }
            return false;
        });
    }


    public void setIdFilter(Long id) {
        filteredList.setPredicate(subject -> id == null || id.equals(subject.getId()));
    }
}

