package animalkeeping.ui;

import animalkeeping.model.*;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static animalkeeping.util.Dialogs.editHousingUnitDialog;


public class BatchTreatmentForm extends VBox {
    private ComboBox<HousingUnit> housingUnitComboBox;
    private ComboBox<TreatmentType> treatmentComboBox;
    private ComboBox<Person> personComboBox;
    private DatePicker treatmentStartDate, treatmentEndDate;
    private TextField startTimeField, endTimeField;


    public BatchTreatmentForm() {
        this(null);
    }


    public BatchTreatmentForm(HousingUnit unit) {
        super();
        this.setFillWidth(true);
        init(unit);
    }

    private void init(HousingUnit unit) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();

        housingUnitComboBox = new ComboBox<>();
        housingUnitComboBox.setConverter(new StringConverter<HousingUnit>() {
            @Override
            public String toString(HousingUnit object) {
                return object.getName();
            }

            @Override
            public HousingUnit fromString(String string) {
                return null;
            }
        });

        treatmentComboBox = new ComboBox<>();
        treatmentComboBox.setConverter(new StringConverter<TreatmentType>() {
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

        treatmentStartDate = new DatePicker(LocalDate.now());
        treatmentEndDate = new DatePicker();
        startTimeField = new TextField();
        startTimeField.setTooltip(new Tooltip("Start time of treatment, use HH:mm:ss format"));
        endTimeField = new TextField();
        endTimeField.setTooltip(new Tooltip("End time of treatment, use HH:mm:ss format"));

        Button newHousingUnit = new Button("+");
        newHousingUnit.setTooltip(new Tooltip("create a new housing unit"));
        newHousingUnit.setOnAction(event -> editHousingUnitDialog(null));

        Button newTreatmentType = new Button("+");
        newTreatmentType.setTooltip(new Tooltip("create a new type of treatment"));
        newTreatmentType.setDisable(true);

        Button newPerson = new Button("+");
        newPerson.setTooltip(new Tooltip("create a new supplier entry"));
        newPerson.setDisable(true);

        Session session = Main.sessionFactory.openSession();
        List<HousingUnit> housingUnits = new ArrayList<>(0);
        List<Person> persons = new ArrayList<>(0);
        List<TreatmentType> types = new ArrayList<>(0);
        try {
            session.beginTransaction();
            housingUnits = session.createQuery("from HousingUnit", HousingUnit.class).list();
            session.getTransaction().commit();
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
        housingUnitComboBox.getItems().addAll(housingUnits);
        housingUnitComboBox.getSelectionModel().select(unit);
        personComboBox.getItems().addAll(persons);
        personComboBox.getSelectionModel().select(0);
        treatmentComboBox.getItems().addAll(types);
        treatmentComboBox.getSelectionModel().select(0);

        Label heading = new Label("Batch treatment to all subjects in a housing unit:");
        heading.setFont(new Font(Font.getDefault().getFamily(), 16));
        this.getChildren().add(heading);

        GridPane grid = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints(100, 100, Double.MAX_VALUE);
        column1.setHgrow(Priority.NEVER);
        ColumnConstraints column2 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
        column2.setHgrow(Priority.ALWAYS);
        ColumnConstraints column3 = new ColumnConstraints(30, 30, Double.MAX_VALUE);
        column3.setHgrow(Priority.NEVER);
        grid.getColumnConstraints().addAll(column1, column2, column3);
        personComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
        treatmentComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
        housingUnitComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
        startTimeField.prefWidthProperty().bind(column2.maxWidthProperty());
        treatmentStartDate.prefWidthProperty().bind(column2.maxWidthProperty());
        endTimeField.prefWidthProperty().bind(column2.maxWidthProperty());
        treatmentEndDate.prefWidthProperty().bind(column2.maxWidthProperty());

        grid.setVgap(5);
        grid.setHgap(2);

        grid.add(new Label("housing unit(*):"), 0, 0);
        grid.add(housingUnitComboBox, 1, 0, 1, 1);
        grid.add(newHousingUnit, 2, 0, 1, 1);

        grid.add(new Label("treatment(*):"), 0, 1);
        grid.add(treatmentComboBox, 1, 1, 1, 1);
        grid.add(newTreatmentType, 2, 1, 1, 1);

        grid.add(new Label("person(*):"), 0, 2);
        grid.add(personComboBox, 1, 2, 1, 1);
        grid.add(newPerson, 2, 2, 1, 1);

        grid.add(new Label("start date(*):"), 0, 3);
        grid.add(treatmentStartDate, 1, 3, 2, 1);

        grid.add(new Label("start time(*):"), 0, 4);
        grid.add(startTimeField, 1, 4, 1, 1);
        startTimeField.setText(dateFormat.format(date));

        grid.add(new Label("end date:"), 0, 5);
        grid.add(treatmentEndDate, 1, 5, 2, 1);
        treatmentEndDate.setOnAction(event -> endTimeField.setText(dateFormat.format(date)));
        grid.add(new Label("end time:"), 0, 6);
        grid.add(endTimeField, 1, 6, 1, 1);

        grid.add(new Label("(*) required"), 0, 9);

        this.getChildren().add(grid);
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


    private Date getDateTime(LocalDate ld, String timeStr) {
        String d = ld.toString();
        if (!validateTime(timeStr)) {
            return null;
        }

        String datetimestr = d + " " + timeStr;
        DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date datetime;

        try {
            datetime = dateTimeFormat.parse(datetimestr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return datetime;
    }


    public Boolean persist() {
        HousingUnit unit = housingUnitComboBox.getValue();
        if(unit == null) {
            return false;
        }
        LocalDate sdate = treatmentStartDate.getValue();
        Date startDate = getDateTime(sdate, startTimeField.getText());
        Date endDate = null;
        if (treatmentEndDate.getValue() != null) {
            endDate = getDateTime(treatmentEndDate.getValue(), endTimeField.getText());
        }

        Session session = Main.sessionFactory.openSession();
        Set<Housing> housings = unit.getAllHousings(true);
        for (Housing h : housings) {
            Treatment treatment = new Treatment(startDate, h.getSubject(), personComboBox.getValue(),
                                                treatmentComboBox.getValue());
            if (endDate != null) {
                treatment.setEnd(endDate);
                if (treatment.getType().isInvasive()) {
                    h.setEnd(endDate);
                }
            }

            try {
                session.beginTransaction();
                session.saveOrUpdate(treatment);
                session.saveOrUpdate(h);
                session.getTransaction().commit();
            } catch (HibernateException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}

