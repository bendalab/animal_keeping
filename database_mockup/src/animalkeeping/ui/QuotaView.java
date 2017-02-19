package animalkeeping.ui;

import animalkeeping.model.Quota;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Created by jan on 18.02.17.
 */
public class QuotaView extends VBox implements Initializable {
    @FXML private TableView<Quota> quotaTable;
    @FXML private TableColumn<Quota, String> idCol, speciesCol;
    @FXML private TableColumn<Quota, Number> numberCol;
    @FXML private TableColumn<Quota, Number> usedCol;
    @FXML private TableColumn<Quota, Double> progressCol;

    public QuotaView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/QuotaTable.fxml"));
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

        progressCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Quota, Double>, ObservableValue<Double>>() {
            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<Quota, Double> param) {
                if (param.getValue() == null || param.getValue().getNumber() == null || param.getValue().getNumber() <= 0) {
                    return  new ReadOnlyObjectWrapper<>(0.0);
                }
                return new ReadOnlyObjectWrapper<>(param.getValue().getAvailableFraction());
            }
        });
        progressCol.setCellFactory(ProgressBarTableCell.<Quota>forTableColumn());
        progressCol.prefWidthProperty().bind(this.widthProperty().multiply(0.39));

    }

    public void setQuota(Set<Quota> quota) {
        quotaTable.getItems().clear();
        quotaTable.getItems().addAll(quota);
    }
}
