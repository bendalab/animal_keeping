package animalkeeping.ui;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;


public class TreatmentForm extends VBox {
    private ComboBox<TreatmentType> typeComboBox;
    private ComboBox<Person> personComboBox;
    private DatePicker startDate, endDate;
    private TextField startTimeField, endTimeField;
    private Subject subject;

    public TreatmentForm(Subject s) {
        this.setFillWidth(true);
        this.subject = s;
        this.init();
    }

    private void init() {
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        ComboBox<TreatmentType> typeComboBox = new ComboBox<>();
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
        ComboBox<Person> personComboBox = new ComboBox<>();
        personComboBox.setConverter(new StringConverter<Person>() {
            @Override
            public String toString(Person object) {
                return object.getFirstName() + ", " + object.getLastName();
            }

            @Override
            public Person fromString(String string) {
                return null;
            }
        });

        DatePicker sdp = new DatePicker();
        sdp.setValue(LocalDate.now());
        TextField startTimeField = new TextField(timeFormat.format(new Date()));
        DatePicker edp = new DatePicker();
        TextField endTimeField = new TextField();
        edp.setOnAction(new EventHandler<ActionEvent>() {
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
                edp.setDisable(immediateEnd.isSelected());
                endTimeField.setDisable(immediateEnd.isSelected());
                edp.setValue(sdp.getValue());
                endTimeField.setText(startTimeField.getText());
            }
        });

        Button newTypeButton = new Button("+");
        newTypeButton.setTooltip(new Tooltip("create a new species entry"));
        newTypeButton.setDisable(true);
        Button newPersonButton = new Button("+");
        newPersonButton.setTooltip(new Tooltip("create a new person entry"));
        newPersonButton.setDisable(true);

        Label heading = new Label("Add treatment:");
        heading.setFont(new Font(Font.getDefault().getFamily(), 16));
        this.getChildren().add(heading);

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
        sdp.prefWidthProperty().bind(column2.maxWidthProperty());
        endTimeField.prefWidthProperty().bind(column2.maxWidthProperty());
        edp.prefWidthProperty().bind(column2.maxWidthProperty());
        immediateEnd.prefWidthProperty().bind(column2.maxWidthProperty());

        grid.setVgap(5);
        grid.setHgap(2);
        grid.add(new Label("treatment type:"), 0, 0);
        grid.add(typeComboBox, 1, 0, 1, 1);
        grid.add(newTypeButton, 2, 1, 1, 1);

        grid.add(new Label("person(*):"), 0, 1);
        grid.add(personComboBox, 1, 1, 1, 1 );
        grid.add(newPersonButton, 2, 1, 1, 1);

        grid.add(new Label("start date:"), 0, 2);
        grid.add(sdp, 1, 2, 2,1);

        grid.add(new Label("start time:"), 0,3);
        grid.add(startTimeField, 1,3, 2, 1);

        grid.add(immediateEnd, 2, 4);

        grid.add(new Label("end date:"), 0, 5);
        grid.add(edp, 1, 5, 2, 1);

        grid.add(new Label("end time:"), 0, 6);
        grid.add(endTimeField, 1, 6, 2, 1);

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


    private boolean validateTime(String time_str) {
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        try {
            timeFormat.parse(time_str);
            return true;
        } catch (Exception e) {
            return  false;
        }
    }

    public Treatment persistTreatment() {
        /*
        LocalDate hdate = housingDate.getValue();
        String d = hdate.toString();
        if (!validateTime(timeField.getText())) {
            return false;
        }
        String t = timeField.getText();
        String datetimestr = d + " " + t;
        DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date;
        try {
            date = dateTimeFormat.parse(datetimestr);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        String prefix = String.valueOf(hdate.getYear());
        String name = nameField.getText();
        Integer initial_id = startIdSpinner.getValue();
        Session session = Main.sessionFactory.openSession();

        for (int i = 0; i < countSpinner.getValue(); i++) {
            String full_name = createName(prefix, name, initial_id + i, 4);
            Subject s = new Subject();
            s.setName(full_name);
            s.setSpeciesType(speciesComboBox.getValue());
            s.setSupplier(supplierComboBox.getValue());
            s.setSubjectType(st);

            Housing h = new Housing(s, housingUnitComboBox.getValue(), date);
            HashSet<Housing> housings = new HashSet<>(1);
            housings.add(h);
            s.setHousings(housings);
            try {
                session.beginTransaction();
                session.save(s);
                session.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        session.close();
        */
        return new Treatment();
    }

}
