package animalkeeping.ui;

import animalkeeping.model.License;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by jan on 18.02.17.
 */
public class LicenseTable extends TableView<License> {
    private TableColumn<License, Number> idCol;
    private TableColumn<License, String> nameCol;
    private TableColumn<License, String> agencyCol;
    private TableColumn<License, String> fileNumberCol;
    private TableColumn<License, String> respPersonCol;
    private TableColumn<License, String> deputyPersonCol;
    private TableColumn<License, Date> startDateCol;
    private TableColumn<License, Date> endDateCol;
    private ObservableList<License> masterList = FXCollections.observableArrayList();
    SortedList<License> sortedList;
    private FilteredList<License> filteredList;

    public LicenseTable() {
        super();
        init();
        refresh();
    }

    public LicenseTable(ObservableList<License> items) {
        this();
        this.setItems(items);
    }

    private void init() {
        filteredList = new FilteredList<>(masterList, p -> true);
        sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(this.comparatorProperty());
        setItems(sortedList);

        idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        idCol.prefWidthProperty().bind(this.widthProperty().multiply(0.05));

        nameCol = new TableColumn<>("name");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        nameCol.prefWidthProperty().bind(this.widthProperty().multiply(0.149));

        startDateCol= new TableColumn<>("from");
        startDateCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Date>(data.getValue().getStartDate()));
        startDateCol.prefWidthProperty().bind(this.widthProperty().multiply(0.1));

        endDateCol= new TableColumn<>("until");
        endDateCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Date>(data.getValue().getEndDate()));
        endDateCol.prefWidthProperty().bind(this.widthProperty().multiply(0.1));

        agencyCol = new TableColumn<>("filing agency");
        agencyCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getAgency() != null ? data.getValue().getAgency() : ""));
        agencyCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        fileNumberCol = new TableColumn<>("file number");
        fileNumberCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getNumber()));
        fileNumberCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        respPersonCol = new TableColumn<>("responsible");
        respPersonCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getResponsiblePerson() != null ?
                data.getValue().getResponsiblePerson().getFirstName() + ", " + data.getValue().getResponsiblePerson().getLastName() : ""));
        respPersonCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        deputyPersonCol = new TableColumn<>("deputy");
        deputyPersonCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDeputy() != null ?
                data.getValue().getDeputy().getFirstName() + ", " + data.getValue().getDeputy().getLastName() : ""));
        deputyPersonCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        this.getColumns().addAll(idCol, nameCol, agencyCol, fileNumberCol, respPersonCol, deputyPersonCol, startDateCol, endDateCol);
        this.setRowFactory( tv -> {
            TableRow<License> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty() ) {
                    License l = row.getItem();
                    l = Dialogs.editLicenseDialog(l);
                    if (l != null) {
                        refresh();
                        setSelectedLicense(l);
                    }
                }
            });
            return row ;
        });
    }


    @Override
    public void refresh() {
        List<License> result = EntityHelper.getEntityList("from License", License.class);
        setLicenses(result);
        super.refresh();
    }

    public void remove(License l) {
        if (masterList.contains(l)) {
            this.getSelectionModel().clearSelection();
            masterList.remove(l);
        }
    }

    public void setSelectedLicense(License l) {
        this.getSelectionModel().select(l);
    }

    public void setLicenses(Collection<License> licenses) {
        masterList.clear();
        if (licenses != null) {
            masterList.addAll(licenses);
        }
    }
}
