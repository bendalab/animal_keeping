package animalkeeping.ui;

import animalkeeping.logging.Communicator;
import animalkeeping.model.License;
import animalkeeping.model.Quota;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class QuotaView extends VBox implements Initializable {
    @FXML TableView<Quota> quotaTable;
    @FXML private TableColumn<Quota, String> idCol, speciesCol;
    @FXML private TableColumn<Quota, Number> numberCol;
    @FXML private TableColumn<Quota, Number> usedCol;
    @FXML private TableColumn<Quota, Double> progressCol;
    private MenuItem newItem, editItem, deleteItem;
    License license = null;


    public QuotaView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/animalkeeping/ui/fxml/QuotaTable.fxml"));
        loader.setController(this);
        try {
            this.getChildren().add(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getId().toString()));
        idCol.prefWidthProperty().bind(this.widthProperty().multiply(0.1));

        speciesCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSpeciesType().getName()));
        speciesCol.prefWidthProperty().bind(this.widthProperty().multiply(0.2));

        numberCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getNumber() != null ? data.getValue().getNumber() : 0));
        numberCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        usedCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getUsed() != null ? data.getValue().getUsed() : 0));
        usedCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        progressCol.setCellValueFactory(param -> {
            if (param.getValue() == null || param.getValue().getNumber() == null || param.getValue().getNumber() <= 0) {
                return  new ReadOnlyObjectWrapper<>(0.0);
            }
            return new ReadOnlyObjectWrapper<>(param.getValue().getAvailableFraction());
        });
        progressCol.setCellFactory(ProgressBarTableCell.forTableColumn());
        progressCol.prefWidthProperty().bind(this.widthProperty().multiply(0.39));

        quotaTable.setRowFactory( tv -> {
            TableRow<Quota> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Quota q = row.getItem();
                    q = Dialogs.editQuotaDialog(q);
                    if (q != null) {
                        refresh();
                        setSelectedQuota(q);
                    }
                }
            });
            return row ;
        });

        this.quotaTable.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Quota>() {
            @Override
            public void onChanged(Change<? extends Quota> c) {
                int sel_count = c.getList().size();
                editItem.setDisable(sel_count == 0);
                deleteItem.setDisable(sel_count == 0);
            }
        });

        ContextMenu cmenu = new ContextMenu();
        newItem = new MenuItem("new quota");
        newItem.setOnAction(event -> editQuota(null));

        editItem = new MenuItem("edit quota");
        editItem.setDisable(true);
        editItem.setOnAction(event -> editQuota(quotaTable.getSelectionModel().getSelectedItem()));

        deleteItem = new MenuItem("delete quota");
        deleteItem.setDisable(true);
        deleteItem.setOnAction(event -> deleteQuota(quotaTable.getSelectionModel().getSelectedItem()));
        cmenu.getItems().addAll(newItem, editItem, deleteItem);
        this.quotaTable.setContextMenu(cmenu);
    }

    public void setQuota(Collection<Quota> quota) {
        quotaTable.getItems().clear();
        quotaTable.getItems().addAll(quota);
    }

    public Boolean removeItem(Quota q) {
        return quotaTable.getItems().remove(q);
    }

    public Quota getSelectedItem() {
        Quota q = null;
        if (!quotaTable.getSelectionModel().isEmpty()) {
            q = quotaTable.getSelectionModel().getSelectedItem();
        }
        return q;
    }

    public void setLicense(License license) {
        this.setSelectedQuota(null);
        this.license = license;
        quotaTable.getItems().clear();
        quotaTable.getSelectionModel().clearSelection();
        if (license != null) {
            EntityHelper.refreshEntity(license);
            setQuota(license.getQuotas());
        }
        quotaTable.refresh();
    }

    public void refresh() {
        this.setLicense(this.license);
    }

    public void setSelectedQuota(Quota q) {
        if (q != null)
            quotaTable.getSelectionModel().select(q);
        else
            quotaTable.getSelectionModel().clearSelection();
    }

    private void editQuota(Quota q) {
        Quota quota;
        if (q == null)
            quota = Dialogs.editQuotaDialog(license);
        else
            quota = Dialogs.editQuotaDialog(q);
        if (quota != null) {
            setLicense(this.license);
            setSelectedQuota(quota);
        }
    }

    private void deleteQuota(Quota q) {
        if (q.getUsed() > 0) {
            Dialogs.showInfo("Cannot delete Quota " + q.getSpeciesType().getName().substring(0, 20) + " since it is referenced by treatments!");
            return;
        }
        Communicator.pushDelete(q);
        refresh();
    }


}
