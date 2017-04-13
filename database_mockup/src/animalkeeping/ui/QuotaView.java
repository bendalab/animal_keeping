package animalkeeping.ui;

import animalkeeping.model.License;
import animalkeeping.model.Quota;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

public class QuotaView extends VBox implements Initializable {
    @FXML TableView<Quota> quotaTable;
    @FXML private TableColumn<Quota, String> idCol, speciesCol;
    @FXML private TableColumn<Quota, Number> numberCol;
    @FXML private TableColumn<Quota, Number> usedCol;
    @FXML private TableColumn<Quota, Double> progressCol;
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
            return  quotaTable.getSelectionModel().getSelectedItem();
        }
        return q;
    }

    public void setLicense(License license) {
        this.license = license;
        quotaTable.getItems().clear();
        if (license != null) {
            List<Quota> qs = EntityHelper.getEntityList("from Quota q where q.license_id = " + license.getId().toString(), Quota.class);
            setQuota(qs);
        }
        quotaTable.refresh();
    }

    public void refresh() {
        this.setLicense(this.license);
    }

    public void setSelectedQuota(Quota q) {
        quotaTable.getSelectionModel().select(q);
    }
}
