package animalkeeping.ui;

import animalkeeping.logging.Communicator;
import animalkeeping.model.License;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import java.util.Date;
import java.util.List;

/**
 * Created by jan on 18.02.17.
 */
public class LicenseTable extends TableView<License> {
    private MenuItem editLicenseItem;
    private MenuItem deleteLicenseItem;
    private ObservableList<License> masterList = FXCollections.observableArrayList();

    public LicenseTable() {
        super();
        init();
    }

    public LicenseTable(ObservableList<License> items) {
        this();
        this.setItems(items);
    }

    private void init() {
        FilteredList<License> filteredList = new FilteredList<>(masterList, p -> true);
        SortedList<License> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(this.comparatorProperty());
        setItems(sortedList);

        TableColumn<License, Number> idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        idCol.prefWidthProperty().bind(this.widthProperty().multiply(0.05));

        TableColumn<License, String> nameCol = new TableColumn<>("name");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        nameCol.prefWidthProperty().bind(this.widthProperty().multiply(0.149));

        TableColumn<License, Date> startDateCol = new TableColumn<>("from");
        startDateCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Date>(data.getValue().getStartDate()));
        startDateCol.prefWidthProperty().bind(this.widthProperty().multiply(0.1));

        TableColumn<License, Date> endDateCol = new TableColumn<>("until");
        endDateCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Date>(data.getValue().getEndDate()));
        endDateCol.prefWidthProperty().bind(this.widthProperty().multiply(0.1));

        TableColumn<License, String> agencyCol = new TableColumn<>("filing agency");
        agencyCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getAgency() != null ? data.getValue().getAgency() : ""));
        agencyCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        TableColumn<License, String> fileNumberCol = new TableColumn<>("file number");
        fileNumberCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getNumber()));
        fileNumberCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        TableColumn<License, String> respPersonCol = new TableColumn<>("responsible");
        respPersonCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getResponsiblePerson() != null ?
                data.getValue().getResponsiblePerson().getFirstName() + " " + data.getValue().getResponsiblePerson().getLastName() : ""));
        respPersonCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        TableColumn<License, String> deputyPersonCol = new TableColumn<>("deputy");
        deputyPersonCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDeputy() != null ?
                data.getValue().getDeputy().getFirstName() + " " + data.getValue().getDeputy().getLastName() : ""));
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

        ContextMenu cmenu = new ContextMenu();
        MenuItem newLicenseItem = new MenuItem("new license");
        newLicenseItem.setOnAction(event -> editLicense(null));

        editLicenseItem = new MenuItem("edit license");
        editLicenseItem.setDisable(true);
        editLicenseItem.setOnAction(event -> editLicense(this.getSelectionModel().getSelectedItem()));

        deleteLicenseItem = new MenuItem("delete license");
        deleteLicenseItem.setDisable(true);
        deleteLicenseItem.setOnAction(event -> deleteLicense(this.getSelectionModel().getSelectedItem()));

        cmenu.getItems().addAll(newLicenseItem, editLicenseItem, deleteLicenseItem);
        this.setContextMenu(cmenu);
        this.getSelectionModel().getSelectedItems().addListener((ListChangeListener<License>) c -> {
            int sel_count = c.getList().size();
            editLicenseItem.setDisable(sel_count == 0);
            deleteLicenseItem.setDisable(sel_count == 0);
        });
    }


    @Override
    public void refresh() {
        License l = getSelectionModel().getSelectedItem();
        Task<Void> refreshTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<License> result = EntityHelper.getEntityList("from License", License.class);
                Platform.runLater(() -> {
                    masterList.clear();
                    masterList.addAll(result);
                    getSelectionModel().select(l);
                });
                return null;
            }
        };
        new Thread(refreshTask).start();
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


    private void editLicense(License l) {
        License ls = Dialogs.editLicenseDialog(l);
        refresh();
        setSelectedLicense(ls);
    }

    private void deleteLicense(License l) {
        if (l.getTreatmentTypes().size() != 0) {
            Dialogs.showInfo("Cannot delete License " + l.getName().substring(0, 20) + " since it is referenced by treatment types!");
            return;
        }
        Communicator.pushDelete(l);
    }
}
