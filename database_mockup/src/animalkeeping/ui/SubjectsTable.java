package animalkeeping.ui;

import animalkeeping.model.Subject;
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

public class SubjectsTable extends TableView<Subject> {
    private TableColumn<Subject, Number> idCol;
    private TableColumn<Subject, String> nameCol;
    private TableColumn<Subject, String> aliasCol;
    private TableColumn<Subject, String> speciesCol;
    private TableColumn<Subject, String> subjectCol;
    private TableColumn<Subject, String> supplierCol;
    private TableColumn<Subject, String> housingCol;
    private ObservableList<Subject> masterList = FXCollections.observableArrayList();
    private FilteredList<Subject> filteredList;

    public SubjectsTable() {
        super();
        idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        idCol.prefWidthProperty().bind(this.widthProperty().multiply(0.08));

        nameCol = new TableColumn<>("name");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        nameCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        aliasCol = new TableColumn<>("alias");
        aliasCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getAlias()));
        aliasCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        speciesCol = new TableColumn<>("species");
        speciesCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSpeciesType().getName()));
        speciesCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        housingCol = new TableColumn<>("housing");
        housingCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getCurrentHousing() != null ?
                data.getValue().getCurrentHousing().getHousing().getName() : ""));
        housingCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        subjectCol = new TableColumn<>("subject");
        subjectCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSubjectType().getName()));
        subjectCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        supplierCol = new TableColumn<>("supplier");
        supplierCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSupplier().getName()));
        supplierCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        this.getColumns().addAll(idCol, nameCol, aliasCol, speciesCol, housingCol, subjectCol, supplierCol);
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

