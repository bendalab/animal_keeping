    package animalkeeping.ui;

import animalkeeping.logging.Communicator;
import animalkeeping.model.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static animalkeeping.util.DateTimeHelper.getDateTime;
import static animalkeeping.util.Dialogs.showInfo;


public class TreatmentForm extends VBox {
    private ComboBox<TreatmentType> typeComboBox;
    private ComboBox<Person> personComboBox;
    private DatePicker startDate, endDate;
    private TextField startTimeField, endTimeField;
    private Subject subject;
    private Treatment treatment;
    private Label idLabel;


    public TreatmentForm(Subject s) {
        this.setFillWidth(true);
        this.subject = s;
        this.treatment = null;
        this.init();
    }

    public TreatmentForm(Treatment t) {
        this.setFillWidth(true);
        this.treatment = t;
        this.subject = t.getSubject();
        this.init(t);
    }

    private  void init(Treatment t) {
        init();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        idLabel.setText(t.getId().toString());
        personComboBox.getSelectionModel().select(t.getPerson());
        typeComboBox.getSelectionModel().select(t.getTreatmentType());
        LocalDate sd = t.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        startDate.setValue(sd);
        startTimeField.setText(timeFormat.format(t.getStart()));
        if (t.getEnd() != null) {
            LocalDate ed = t.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            endDate.setValue(ed);
            endTimeField.setText(timeFormat.format(t.getEnd()));
        }
    }

    private void init() {
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        idLabel = new Label();
        typeComboBox = new ComboBox<>();
        typeComboBox.setConverter(new StringConverter<TreatmentType>() {
            @Override
            public String toString(TreatmentType object) {
                return object.getName();
            }

            @Override
            public TreatmentType fromString(String string) {
                return null;
            }
        });
        personComboBox = new ComboBox<>();
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

        startDate = new DatePicker();
        startDate.setValue(LocalDate.now());
        startTimeField = new TextField(timeFormat.format(new Date()));
        endDate = new DatePicker();
        endTimeField = new TextField();
        endDate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                endTimeField.setText(timeFormat.format(new Date()));
            }
        });

        CheckBox immediateEnd = new CheckBox("treatment ends immediately");
        immediateEnd.setSelected(false);
        immediateEnd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                endDate.setDisable(immediateEnd.isSelected());
                endTimeField.setDisable(immediateEnd.isSelected());
                endDate.setValue(startDate.getValue());
                endTimeField.setText(startTimeField.getText());
            }
        });

        Button newTypeButton = new Button("+");
        newTypeButton.setTooltip(new Tooltip("create a new species entry"));
        newTypeButton.setDisable(true);
        Button newPersonButton = new Button("+");
        newPersonButton.setTooltip(new Tooltip("create a new person entry"));
        newPersonButton.setDisable(true);

        GridPane grid = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints(100,100, Double.MAX_VALUE);
        column1.setHgrow(Priority.NEVER);
        ColumnConstraints column2 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
        column2.setHgrow(Priority.ALWAYS);
        ColumnConstraints column3 = new ColumnConstraints(30, 30, Double.MAX_VALUE);
        column3.setHgrow(Priority.NEVER);
        grid.getColumnConstraints().addAll(column1, column2, column3);
        typeComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
        personComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
        startTimeField.prefWidthProperty().bind(column2.maxWidthProperty());
        startDate.prefWidthProperty().bind(column2.maxWidthProperty());
        endTimeField.prefWidthProperty().bind(column2.maxWidthProperty());
        endDate.prefWidthProperty().bind(column2.maxWidthProperty());
        immediateEnd.prefWidthProperty().bind(column2.maxWidthProperty());
        immediateEnd.setFont(new Font(Font.getDefault().getFamily(), 9));

        grid.setVgap(5);
        grid.setHgap(2);
        grid.add(new Label("ID:"), 0, 0);
        grid.add(idLabel, 0, 1);

        grid.add(new Label("type:"), 0, 1);
        grid.add(typeComboBox, 1, 1, 1, 1);
        grid.add(newTypeButton, 2, 1, 1, 1);

        grid.add(new Label("person:"), 0, 2);
        grid.add(personComboBox, 1, 2, 1, 1 );
        grid.add(newPersonButton, 2, 2, 1, 1);

        grid.add(new Label("start date:"), 0, 3);
        grid.add(startDate, 1, 3, 2,1);

        grid.add(new Label("start time:"), 0,4);
        grid.add(startTimeField, 1,4, 2, 1);

        grid.add(immediateEnd, 1, 5);

        grid.add(new Label("end date:"), 0, 6);
        grid.add(endDate, 1, 6, 2, 1);

        grid.add(new Label("end time:"), 0, 7);
        grid.add(endTimeField, 1, 7, 2, 1);

        this.getChildren().add(grid);

        Session session = Main.sessionFactory.openSession();
        List<Person> persons = new ArrayList<>(0);
        List<TreatmentType> types = new ArrayList<>(0);
        try {
            session.beginTransaction();
            persons = session.createQuery("from Person", Person.class).list();
            session.getTransaction().commit();
            session.beginTransaction();
            types = session.createQuery("from TreatmentType", TreatmentType.class).list();
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }
        typeComboBox.getItems().addAll(types);
        personComboBox.getItems().addAll(persons);

    }


    public Treatment persistTreatment() {
        Date sd = getDateTime(startDate.getValue(), startTimeField.getText());
        Date ed = null;
        if (endDate.getValue() != null) {
            ed = getDateTime(endDate.getValue(), endTimeField.getText());
        }
        if (treatment == null) {
            treatment = new Treatment();
        }
        treatment.setStart(sd);
        treatment.setEnd(ed);
        treatment.setSubject(subject);
        treatment.setPerson(personComboBox.getValue());
        treatment.setTreatmentType(typeComboBox.getValue());

        Communicator.pushSaveOrUpdate(treatment);

        return treatment;
    }

}
