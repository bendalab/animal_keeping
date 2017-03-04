package animalkeeping.ui;

import animalkeeping.model.Treatment;
import animalkeeping.util.EntityHelper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class TreatmentsTable extends TableView<Treatment>{
    private TableColumn<Treatment, Number> idCol;
    private TableColumn<Treatment, String> subjectCol;
    private TableColumn<Treatment, String> personCol;
    private TableColumn<Treatment, String> treatmentCol;
    private TableColumn<Treatment, Date> startCol;
    private TableColumn<Treatment, Date> endCol;
    private TableColumn<Treatment, Boolean> finalCol;
    private ObservableList<Treatment> masterList = FXCollections.observableArrayList();

    public TreatmentsTable() {
        super();
        idCol = new TableColumn<Treatment, Number>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        idCol.prefWidthProperty().bind(this.widthProperty().multiply(0.09));

        subjectCol = new TableColumn<Treatment, String>("subject");
        subjectCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSubject().getName()));
        subjectCol.prefWidthProperty().bind(this.widthProperty().multiply(0.13));

        personCol = new TableColumn<Treatment, String>("by person");
        personCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPerson().getFirstName() +
        " " + data.getValue().getPerson().getLastName()));
        personCol.prefWidthProperty().bind(this.widthProperty().multiply(0.13));

        treatmentCol = new TableColumn<Treatment, String>("treatment");
        treatmentCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getType().getName()));
        treatmentCol.prefWidthProperty().bind(this.widthProperty().multiply(0.17));

        startCol = new TableColumn<Treatment, Date>("start");
        startCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Date>(data.getValue().getStart()));
        startCol.prefWidthProperty().bind(this.widthProperty().multiply(0.19));

        endCol = new TableColumn<Treatment, Date>("end");
        endCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Date>(data.getValue().getEnd()));
        endCol.prefWidthProperty().bind(this.widthProperty().multiply(0.19));

        finalCol = new TableColumn<Treatment, Boolean>("is final");
        finalCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Boolean>(data.getValue().getType().isInvasive()));
        finalCol.prefWidthProperty().bind(this.widthProperty().multiply(0.8));


        this.getColumns().addAll(idCol, subjectCol, personCol, treatmentCol, startCol, endCol, finalCol);
        init();
    }


    private void init() {
        List<Treatment> treatments = EntityHelper.getEntityList("from Treatment", Treatment.class);
        masterList.addAll(treatments);
        this.setItems(masterList);
    }

    public void setTreatments(Collection<Treatment> treatments) {
        masterList.clear();
        if (treatments != null) {
            masterList.addAll(treatments);
        }
    }
}
