package animalkeeping.ui;

import animalkeeping.model.TreatmentType;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.util.Collection;
import java.util.List;

/**
 * Created by jan on 19.02.17.
 */
public class TreatmentTypeTable extends TableView<TreatmentType> {


    public TreatmentTypeTable() {
        super();
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        TableColumn<TreatmentType, Number> idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        idCol.prefWidthProperty().bind(this.widthProperty().multiply(0.04));

        TableColumn<TreatmentType, String> nameCol = new TableColumn<>("name");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        nameCol.prefWidthProperty().bind(this.widthProperty().multiply(0.20));

        TableColumn<TreatmentType, String> licenseCol = new TableColumn<>("license");
        licenseCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getLicense() != null ? data.getValue().getLicense().getName() : ""));
        licenseCol.prefWidthProperty().bind(this.widthProperty().multiply(0.20));

        TableColumn<TreatmentType, Boolean> invasiveCol = new TableColumn<>("invasive");
        invasiveCol.setCellValueFactory(data -> new ReadOnlyBooleanWrapper(data.getValue().isInvasive()));
        invasiveCol.setCellFactory(tc -> new CheckBoxTableCell<>());
        invasiveCol.prefWidthProperty().bind(this.widthProperty().multiply(0.07));

        TableColumn<TreatmentType, Boolean> finalCol = new TableColumn<>("final");
        finalCol.setCellValueFactory(data -> new ReadOnlyBooleanWrapper(data.getValue().isFinalExperiment()));
        finalCol.prefWidthProperty().bind(this.widthProperty().multiply(0.07));
        finalCol.setCellFactory(tc -> new CheckBoxTableCell<>());

        TableColumn<TreatmentType, String> targetCol = new TableColumn<>("target");
        targetCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTarget().toString()));
        targetCol.prefWidthProperty().bind(this.widthProperty().multiply(0.1));

        TableColumn<TreatmentType, String> descriptionCol = new TableColumn<>("description");
        descriptionCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDescription() != null ?
                data.getValue().getDescription() : ""));
        descriptionCol.prefWidthProperty().bind(this.widthProperty().multiply(0.33));

        this.getColumns().addAll(idCol, nameCol, licenseCol, invasiveCol, finalCol, targetCol, descriptionCol);
        this.setRowFactory( tv -> {
            TableRow<TreatmentType> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    TreatmentType tt = row.getItem();
                    tt = Dialogs.editTreatmentTypeDialog(tt);
                    if (tt != null) {
                        refresh();
                        getSelectionModel().select(tt);
                    }
                }
            });
            return row ;
        });
    }


    public TreatmentTypeTable(ObservableList<TreatmentType> items) {
        this();
        this.setItems(items);
    }


    private void init() {
        TreatmentType t  = getSelectionModel().getSelectedItem();
        Task<Void> refreshTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<TreatmentType> tts = EntityHelper.getEntityList("from TreatmentType", TreatmentType.class);

                Platform.runLater(() -> {
                    getItems().clear();
                    getItems().addAll(tts);
                    getSelectionModel().select(t);
                });
                return null;
            }
        };
        new Thread(refreshTask).start();
    }

    public void setTreatmentTypes(Collection<TreatmentType> types) {
        this.getItems().clear();
        this.getItems().addAll(types);
    }

    @Override
    public void refresh() {
        init();
        super.refresh();
    }

    /*
    public void setSelectedTreatmentType(TreatmentType treatmentType) {
        System.out.println(this.getItems().contains(treatmentType));
        int index = getItems().indexOf(treatmentType);
        this.getSelectionModel().select(index);
    }
    */
}

