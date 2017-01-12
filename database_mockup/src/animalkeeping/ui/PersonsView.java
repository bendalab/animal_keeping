package animalkeeping.ui;

import animalkeeping.model.Person;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by grewe on 1/12/17.
 */
public class PersonsView  extends VBox implements Initializable {
    @FXML private ScrollPane tableScrollPane;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField idField;
    @FXML private TableView treatmentTable;
    private PersonsTable personsTable;
    private TableColumn<Treatment, Number> idCol;
    private TableColumn<Treatment, String> typeCol;
    private TableColumn<Treatment, Date> startDateCol;
    private TableColumn<Treatment, Date> endDateCol;
    private TableColumn<Treatment, String> subjectCol;

    public PersonsView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/PersonsView.fxml"));
        loader.setController(this);
        try {
            this.getChildren().add(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        personsTable = new PersonsTable();

        //personsTable.resize();
        this.tableScrollPane.setContent(personsTable);
        idField.setEditable(false);
        idField.setText("");
        firstNameField.setText("");
        firstNameField.setEditable(false);
        lastNameField.setEditable(false);
        lastNameField.setText("");
        emailField.setEditable(false);
        emailField.setText("");

        personsTable.getSelectionModel().getSelectedItems().addListener(new PersonTableListChangeListener());
        idCol = new TableColumn<Treatment, Number>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        subjectCol = new TableColumn<Treatment, String>("subject");
        subjectCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSubject().getName()));
        typeCol = new TableColumn<Treatment, String>("treatment");
        typeCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getType().getName()));
        startDateCol = new TableColumn<Treatment, Date>("start");
        startDateCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Date>(data.getValue().getStart()));
        endDateCol = new TableColumn<Treatment, Date>("end");
        endDateCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Date>(data.getValue().getEnd()));
        treatmentTable.getColumns().clear();
        treatmentTable.getColumns().addAll(idCol, subjectCol, typeCol, startDateCol, endDateCol);
    }


    private void personSelected(Person p) {
        if (p != null) {
            idField.setText(p.getId().toString());
            firstNameField.setText(p.getFirstName());
            lastNameField.setText(p.getLastName());
            emailField.setText(p.getEmail());
            System.out.println(p.getTreatments().size());
            treatmentTable.getItems().clear();
            treatmentTable.getItems().addAll(p.getTreatments());
        } else {
            idField.setText("");
            firstNameField.setText("");
            lastNameField.setText("");
            emailField.setText("");
            treatmentTable.getItems().clear();
        }
    }


    private class PersonTableListChangeListener implements ListChangeListener<Person> {

        @Override
        public void onChanged(Change<? extends Person> c) {
            if (c.getList().size() > 0) {
                personSelected(c.getList().get(0));
            }
        }
    }

}