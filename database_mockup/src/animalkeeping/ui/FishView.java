package animalkeeping.ui;

import animalkeeping.model.Subject;
import animalkeeping.model.Treatment;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by grewe on 1/12/17.
 */
public class FishView extends VBox implements Initializable {
    @FXML private ScrollPane tableScrollPane;
    @FXML private TextField idField;
    @FXML private TextField aliasField;
    @FXML private TextField speciesField;
    @FXML private TextField supplierField;
    @FXML private Label aliveField;
    @FXML private TableView treatmentTable;

    private FishTable fishTable;
    private TableColumn<Treatment, Number> idCol;
    private TableColumn<Treatment, String> typeCol;
    private TableColumn<Treatment, Date> startDateCol;
    private TableColumn<Treatment, Date> endDateCol;
    private TableColumn<Treatment, String> aliasCol;

    public FishView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/FishView.fxml"));
        loader.setController(this);
        try {
            this.getChildren().add(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FishTable fishTable = new FishTable();

        //personsTable.resize();
        this.tableScrollPane.setContent(fishTable);
        idField.setEditable(false);
        idField.setText("");
        aliasField.setText("");
        aliasField.setEditable(false);
        speciesField.setEditable(false);
        speciesField.setText("");
        supplierField.setEditable(false);
        supplierField.setText("");
        aliveField.setText("");

        fishTable.getSelectionModel().getSelectedItems().addListener(new FishTableListChangeListener());
        idCol = new TableColumn<Treatment, Number>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        aliasCol = new TableColumn<Treatment, String>("alias");
        aliasCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSubject().getName()));
        typeCol = new TableColumn<Treatment, String>("treatment");
        typeCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getType().getName()));
        startDateCol = new TableColumn<Treatment, Date>("start");
        startDateCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Date>(data.getValue().getStart()));
        endDateCol = new TableColumn<Treatment, Date>("end");
        endDateCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Date>(data.getValue().getEnd()));
        treatmentTable.getColumns().clear();
        treatmentTable.getColumns().addAll(idCol, aliasCol, typeCol, startDateCol, endDateCol);
    }


    private void subjectSelected(Subject s) {
        if (s != null) {
            idField.setText(s.getId().toString());
            aliasField.setText(s.getName());
            speciesField.setText(s.getSpeciesType().getName());
            supplierField.setText(s.getSupplier().getName());
            aliveField.setText("Possibly");
            System.out.println(s.getTreatments().size());
            treatmentTable.getItems().clear();
            treatmentTable.getItems().addAll(s.getTreatments());
        } else {
            idField.setText("");
            aliasField.setText("");
            supplierField.setText("");
            speciesField.setText("");
            aliveField.setText("");
            treatmentTable.getItems().clear();
        }
    }


    private class FishTableListChangeListener implements ListChangeListener<Subject> {

        @Override
        public void onChanged(Change<? extends Subject> c) {
            if (c.getList().size() > 0) {
                subjectSelected(c.getList().get(0));
            }
        }
    }

}