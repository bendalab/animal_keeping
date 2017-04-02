package animalkeeping.ui;

import animalkeeping.model.Entity;
import animalkeeping.model.Note;
import animalkeeping.model.Person;
import animalkeeping.ui.Main;
import animalkeeping.util.DateTimeHelper;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public abstract class NotesFrom<T extends Note, E extends Entity> extends VBox {
    protected E entity;
    protected T note_entity;
    protected TextField nameField;
    protected SpecialTextField timeField;
    protected DatePicker datePicker;
    protected ComboBox<Person> personComboBox;
    private Label idLabel;
    protected TextArea commentArea;

    NotesFrom(E entity){
        init();
        this.entity = entity;
    }

    NotesFrom(T note, E entity) {
        this(entity);
        this.note_entity = note;
        set_defaults();
    }

    private void set_defaults() {
        if (this.note_entity == null) {
            return;
        }
        idLabel.setText(note_entity.getId().toString());
        nameField.setText(note_entity.getName());
        commentArea.setText(note_entity.getComment());
        personComboBox.getSelectionModel().select(note_entity.getPerson());
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        LocalDate d = DateTimeHelper.toLocalDate(note_entity.getDate());
        datePicker.setValue(d);
        timeField.setText(timeFormat.format(note_entity.getDate()));
    }

    private void init() {
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        idLabel = new Label();

        personComboBox= new ComboBox<>();
        personComboBox.setConverter(new StringConverter<Person>() {
            @Override
            public String toString(Person object) {
                return object.getLastName() + ", " + object.getFirstName();
            }

            @Override
            public Person fromString(String string) {
                return null;
            }
        });

        datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());
        timeField = new SpecialTextField("##:##:##");
        timeField.setText(timeFormat.format(new Date()));

        nameField = new TextField();
        commentArea = new TextArea();
        commentArea.setWrapText(true);

        GridPane grid = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints(100,100, Double.MAX_VALUE);
        column1.setHgrow(Priority.NEVER);
        ColumnConstraints column2 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
        column2.setHgrow(Priority.ALWAYS);
        ColumnConstraints column3 = new ColumnConstraints(30, 30, Double.MAX_VALUE);
        column3.setHgrow(Priority.NEVER);
        grid.getColumnConstraints().addAll(column1, column2, column3);
        personComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
        datePicker.prefWidthProperty().bind(column2.maxWidthProperty());
        timeField.prefWidthProperty().bind(column2.maxWidthProperty());
        nameField.prefWidthProperty().bind(column2.maxWidthProperty());

        grid.setVgap(5);
        grid.setHgap(2);
        grid.add(new Label("ID:"), 0, 0);
        grid.add(idLabel, 0, 0);

        grid.add(new Label("name:"), 0,1);
        grid.add(nameField, 1,1, 2, 1);

        grid.add(new Label("person:"), 0, 2);
        grid.add(personComboBox, 1, 2, 2, 1);

        grid.add(new Label("date:"), 0, 3);
        grid.add(datePicker, 1, 3, 1, 1 );

        grid.add(new Label("time:"), 0, 4);
        grid.add(timeField, 1, 4, 1, 1 );

        this.getChildren().add(grid);
        this.getChildren().add(commentArea);

        Session session = Main.sessionFactory.openSession();
        List<Person> persons = new ArrayList<>(0);
        try {
            session.beginTransaction();
            persons = session.createQuery("from Person", Person.class).list();
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }
        personComboBox.getItems().addAll(persons);
    }


    public abstract T persist();

}
