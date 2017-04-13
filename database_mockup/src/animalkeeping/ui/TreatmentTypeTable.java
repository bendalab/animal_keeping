package animalkeeping.ui;

import animalkeeping.model.Treatment;
import animalkeeping.model.TreatmentType;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
    private TableColumn<TreatmentType, Number> idCol;
    private TableColumn<TreatmentType, String> nameCol;
    private TableColumn<TreatmentType, Boolean> invasiveCol;
    private TableColumn<TreatmentType, Boolean> finalCol;
    private TableColumn<TreatmentType, String> targetCol;
    private TableColumn<TreatmentType, String> licenseCol;
    private TableColumn<TreatmentType, String> descriptionCol;
    private ObservableList<TreatmentType> masterList = FXCollections.observableArrayList();
    private FilteredList<TreatmentType> filteredList;


    public TreatmentTypeTable() {
        super();
        idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        idCol.prefWidthProperty().bind(this.widthProperty().multiply(0.04));

        nameCol = new TableColumn<>("name");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        nameCol.prefWidthProperty().bind(this.widthProperty().multiply(0.20));

        licenseCol = new TableColumn<>("license");
        licenseCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getLicense() != null ? data.getValue().getLicense().getName() : ""));
        licenseCol.prefWidthProperty().bind(this.widthProperty().multiply(0.20));

        invasiveCol = new TableColumn<>("invasive");
        invasiveCol.setCellValueFactory(data -> new ReadOnlyBooleanWrapper(data.getValue().isInvasive()));
        invasiveCol.setCellFactory( tc -> new CheckBoxTableCell<>());
        invasiveCol.prefWidthProperty().bind(this.widthProperty().multiply(0.07));

        finalCol = new TableColumn<>("final");
        finalCol.setCellValueFactory(data -> new ReadOnlyBooleanWrapper(data.getValue().isFinalExperiment()));
        finalCol.prefWidthProperty().bind(this.widthProperty().multiply(0.07));
        finalCol.setCellFactory( tc -> new CheckBoxTableCell<>());

        targetCol = new TableColumn<>("target");
        targetCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTarget().toString()));
        targetCol.prefWidthProperty().bind(this.widthProperty().multiply(0.1));

        descriptionCol = new TableColumn<>("description");
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
                        setSelectedTreatmentType(tt);
                    }
                }
            });
            return row ;
        });

        init();
    }


    public TreatmentTypeTable(ObservableList<TreatmentType> items) {
        this();
        this.setItems(items);
    }


    private void init() {
        List<TreatmentType> result = EntityHelper.getEntityList("from TreatmentType", TreatmentType.class);
        masterList.addAll(result);
        filteredList = new FilteredList<>(masterList, p -> true);
        SortedList<TreatmentType> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(this.comparatorProperty());
        this.setItems(sortedList);
    }

    public void setTreatmentTypes(Collection<TreatmentType> types) {
        this.masterList.clear();
        this.masterList.addAll(types);
    }

    @Override
    public void refresh() {
        masterList.clear();
        init();
        super.refresh();
    }

    public void setSelectedTreatmentType(TreatmentType treatmentType) {
        this.getSelectionModel().select(treatmentType);
    }
}

