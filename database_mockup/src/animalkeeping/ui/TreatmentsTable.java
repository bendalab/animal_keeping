package animalkeeping.ui;

import animalkeeping.logging.Communicator;
import animalkeeping.model.Housing;
import animalkeeping.model.Subject;
import animalkeeping.model.Treatment;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.util.Pair;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public class TreatmentsTable extends TableView<Treatment>{
    private TableColumn<Treatment, Number> idCol;
    private TableColumn<Treatment, String> subjectCol;
    private TableColumn<Treatment, String> personCol;
    private TableColumn<Treatment, String> treatmentCol;
    private TableColumn<Treatment, Date> startCol;
    private TableColumn<Treatment, Date> endCol;
    private TableColumn<Treatment, Boolean> finalCol;
    private ObservableList<Treatment> masterList = FXCollections.observableArrayList();
    private MenuItem newItem, editItem, deleteItem, endItem;
    private Subject subject = null;


    public TreatmentsTable() {
        super();
        idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        idCol.prefWidthProperty().bind(this.widthProperty().multiply(0.08));

        subjectCol = new TableColumn<>("subject");
        subjectCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSubject().getName()));
        subjectCol.prefWidthProperty().bind(this.widthProperty().multiply(0.13));

        personCol = new TableColumn<>("by person");
        personCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPerson().getFirstName() +
        " " + data.getValue().getPerson().getLastName()));
        personCol.prefWidthProperty().bind(this.widthProperty().multiply(0.13));

        treatmentCol = new TableColumn<>("treatment");
        treatmentCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTreatmentType().getName()));
        treatmentCol.prefWidthProperty().bind(this.widthProperty().multiply(0.17));

        startCol = new TableColumn<>("start");
        startCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Date>(data.getValue().getStart()));
        startCol.prefWidthProperty().bind(this.widthProperty().multiply(0.19));

        endCol = new TableColumn<>("end");
        endCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Date>(data.getValue().getEnd()));
        endCol.prefWidthProperty().bind(this.widthProperty().multiply(0.19));

        finalCol = new TableColumn<>("final");
        finalCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Boolean>(data.getValue().getTreatmentType().isFinalExperiment()));
        finalCol.prefWidthProperty().bind(this.widthProperty().multiply(0.08));

        this.getColumns().addAll(idCol, subjectCol, personCol, treatmentCol, startCol, endCol, finalCol);
        this.setRowFactory( tv -> {
            TableRow<Treatment> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Treatment t = row.getItem();
                    t = Dialogs.editTreatmentDialog(t);
                    if (t != null) {
                        refresh();
                        setSelectedTreatment(t);
                    }
                }
            });
            return row ;
        });
        this.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Treatment>) c -> {
            int sel_count = c.getList().size();
            editItem.setDisable(sel_count == 0);
            deleteItem.setDisable(sel_count == 0);
            endItem.setDisable(sel_count == 0 || c.getList().get(0).getEnd() == null);
        });

        ContextMenu cmenu = new ContextMenu();
        newItem = new MenuItem("new treatment");
        newItem.setOnAction(event -> editTreatment(null));

        editItem = new MenuItem("edit treatment");
        editItem.setDisable(true);
        editItem.setOnAction(event -> editTreatment(this.getSelectionModel().getSelectedItem()));

        deleteItem = new MenuItem("delete treatment");
        deleteItem.setDisable(true);
        deleteItem.setOnAction(event -> deleteTreatment(this.getSelectionModel().getSelectedItem()));

        endItem = new MenuItem("end treatment");
        endItem.setDisable(true);
        endItem.setOnAction(event -> endTreatment(this.getSelectionModel().getSelectedItem()));
        cmenu.getItems().addAll(newItem, editItem, deleteItem, endItem);
        this.setContextMenu(cmenu);
        init();
    }


    private void init() {
        if (this.subject == null) {
            List<Treatment> treatments = EntityHelper.getEntityList("from Treatment", Treatment.class);
            masterList.addAll(treatments);
        } else {
            masterList.addAll(subject.getTreatments());
        }
        this.setItems(masterList);
    }

    public void setTreatments(Collection<Treatment> treatments) {
        masterList.clear();
        if (treatments != null) {
            masterList.addAll(treatments);
        }
    }


    public void setSubject(Subject s) {
        this.subject = s;
        refresh();
    }


    public void refresh() {
        masterList.clear();
        init();
        super.refresh();
    }

    public void setSelectedTreatment(Treatment treatment) {
        this.getSelectionModel().select(treatment);
    }

    public void editTreatment(Treatment t) {
        Treatment treatment = Dialogs.editTreatmentDialog(t);
        if(treatment != null) {
            this.refresh();
            this.setSelectedTreatment(treatment);
        }
    }

    public void deleteTreatment(Treatment treatment) {
        if (treatment == null) {
            return;
        }
        Communicator.pushDelete(treatment);
        this.refresh();
    }

    public void endTreatment(Treatment t) {
        if (t == null)
            return;
        Pair<Date, Date> interval = Dialogs.getDateTimeInterval(t.getStart(), new Date());
        if (interval != null) {
            t.setStart(interval.getKey());
            t.setEnd(interval.getValue());
            Communicator.pushSaveOrUpdate(t);
            if (t.getTreatmentType().isInvasive()) {
                Housing h = t.getSubject().getCurrentHousing();
                h.setEnd(interval.getValue());
                Communicator.pushSaveOrUpdate(h);
            }
            refresh();
        }
    }
}
