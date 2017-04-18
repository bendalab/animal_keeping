package animalkeeping.ui;

import animalkeeping.logging.Communicator;
import animalkeeping.model.Subject;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;


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
    private MenuItem newItem, editItem, deleteItem, addTreatmentItem, reportDeadItem, observationItem, moveItem;

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
        this.setRowFactory( tv -> {
            TableRow<Subject> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Subject s = row.getItem();
                    s = Dialogs.editSubjectDialog(s);
                    if (s != null) {
                        refresh();
                        setSelectedSubject(s);
                    }
                }
            });
            return row ;
        });

        this.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Subject>() {
            @Override
            public void onChanged(Change<? extends Subject> c) {
                int sel_count = c.getList().size();
                editItem.setDisable(sel_count == 0);
                deleteItem.setDisable(sel_count == 0);
                addTreatmentItem.setDisable(sel_count == 0);
                if (sel_count > 0) {
                    Subject s = c.getList().get(0);
                    boolean alive = s.getCurrentHousing() != null;
                    moveItem.setDisable(!alive);
                    addTreatmentItem.setDisable(!alive);
                    reportDeadItem.setDisable(!alive);
                }
            }
        });

        ContextMenu cmenu = new ContextMenu();
        newItem = new MenuItem("new subject");
        newItem.setOnAction(event -> editSubject(null));

        editItem = new MenuItem("edit subject");
        editItem.setDisable(true);
        editItem.setOnAction(event -> editSubject(this.getSelectionModel().getSelectedItem()));

        deleteItem = new MenuItem("delete subject");
        deleteItem.setDisable(true);
        deleteItem.setOnAction(event -> deleteSubject(this.getSelectionModel().getSelectedItem()));

        addTreatmentItem = new MenuItem("add treatment");
        addTreatmentItem.setDisable(true);
        addTreatmentItem.setOnAction(event -> addTreatment(this.getSelectionModel().getSelectedItem()));

        observationItem = new MenuItem("add observation");
        observationItem.setDisable(true);
        observationItem.setOnAction(event -> addObservation(this.getSelectionModel().getSelectedItem()));

        reportDeadItem = new MenuItem("report subject dead");
        reportDeadItem.setDisable(true);
        reportDeadItem.setOnAction(event -> reportSubjectDead(this.getSelectionModel().getSelectedItem()));

        moveItem = new MenuItem("move subject");
        moveItem.setDisable(true);
        moveItem.setOnAction(event -> moveSubject(this.getSelectionModel().getSelectedItem()));

        cmenu.getItems().addAll(newItem, editItem, deleteItem, addTreatmentItem, observationItem, reportDeadItem, moveItem);
        this.setContextMenu(cmenu);
        init();
    }

    public SubjectsTable(ObservableList<Subject> items) {
        this();
        this.setItems(items);
    }

    private void init() {
        masterList.clear();
        List<Subject> subjects = EntityHelper.getEntityList("from Subject", Subject.class);
        masterList.addAll(subjects);
        filteredList = new FilteredList<>(masterList, p -> true);
        SortedList<Subject> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(this.comparatorProperty());
        this.setItems(sortedList);
        setAliveFilter(true);
    }


    public void setAliveFilter(Boolean set) {
        if (set) {
            filteredList.setPredicate(subject -> subject.getCurrentHousing() != null);
        } else {
            filteredList.setPredicate(null);
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


    public void refresh() {
        init();
    }


    public void setSelectedSubject(Subject s) {
        this.getSelectionModel().select(s);
    }


    public void setIdFilter(Long id) {
        filteredList.setPredicate(subject -> id == null || id.equals(subject.getId()));
    }


    public void editSubject(Subject s) {
        Subject sbct = Dialogs.editSubjectDialog(s);
        if (sbct != null) {
            refresh();
            setSelectedSubject(s);
        }
     }

    public void deleteSubject(Subject s) {
        if (!s.getTreatments().isEmpty()) {
            Dialogs.showInfo("Cannot delete subject " + s.getName() + " since it is referenced by " +
                    Integer.toString(s.getTreatments().size()) + " treatment entries! Delete them first.");
        } else {
            Communicator.pushDelete(s);
        }
        refresh();
        setSelectedSubject(null);
    }

    public void addTreatment(Subject s) {
        Dialogs.editTreatmentDialog(s);
        refresh();
        setSelectedSubject(s);
    }

    public void addObservation(Subject s) {
        Dialogs.editSubjectNoteDialog(s);

    }

    public void reportSubjectDead(Subject s) {
        if (s == null) {
            return;
        }
        Subject subject = Dialogs.reportSubjectDead(s);
        if (subject != null) {
            refresh();
            setSelectedSubject(subject);
        }
    }

    public void moveSubject(Subject s) {
        if (s == null) {
            return;
        }
        Subject subject = Dialogs.relocateSubjectDialog(s);
        if (subject != null) {
            refresh();
            setSelectedSubject(subject);
        }
    }
}

