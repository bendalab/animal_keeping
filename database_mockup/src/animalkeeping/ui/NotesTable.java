package animalkeeping.ui;

import animalkeeping.model.Note;
import animalkeeping.model.Person;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by grewe on 2/15/17.
 */
public class NotesTable<T extends Note> extends TableView<T>{
    private TableColumn<T, Number> idCol;
    private TableColumn<T, String> personCol;
    private TableColumn<T, String> nameCol;
    private TableColumn<T, String> commentCol;
    private TableColumn<T, Date> dateCol;

    public NotesTable() {
        super();
        init();
    }

    public NotesTable(ObservableList<T> items) {
        this();
        this.setItems(items);
    }

    private void init() {
        idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        idCol.prefWidthProperty().bind(this.widthProperty().multiply(0.08));

        nameCol = new TableColumn<>("name");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        nameCol.prefWidthProperty().bind(this.widthProperty().multiply(0.2));

        dateCol= new TableColumn<>("from");
        dateCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Date>(data.getValue().getDate()));
        dateCol.prefWidthProperty().bind(this.widthProperty().multiply(0.2));

        personCol = new TableColumn<>("person");
        personCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPerson() != null ? data.getValue().getPerson().getLastName() + ", " + data.getValue().getPerson().getFirstName() : ""));
        personCol.prefWidthProperty().bind(this.widthProperty().multiply(0.2));

        commentCol = new TableColumn<>("last name");
        commentCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getComment()));
        commentCol.prefWidthProperty().bind(this.widthProperty().multiply(0.32));
        this.getColumns().addAll(idCol, nameCol, dateCol, personCol, commentCol);
    }

    public void setNotes(Set<T> notes) {
        this.getItems().clear();
        this.getItems().addAll(notes);
    }
}
