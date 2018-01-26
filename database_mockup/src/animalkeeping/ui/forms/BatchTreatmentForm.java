package animalkeeping.ui.forms;

import animalkeeping.logging.Communicator;
import animalkeeping.model.*;
import animalkeeping.ui.Main;
import animalkeeping.ui.widgets.HousingDropDown;
import animalkeeping.ui.widgets.SpecialTextField;
import animalkeeping.util.DateTimeHelper;
import animalkeeping.util.EntityHelper;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.StringConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.prefs.Preferences;

import static animalkeeping.util.Dialogs.editHousingUnitDialog;


public class BatchTreatmentForm extends VBox {
    private HousingDropDown housingUnitComboBox;
    private ComboBox<TreatmentType> treatmentComboBox;
    private ComboBox<Person> personComboBox;
    private DatePicker treatmentStartDate, treatmentEndDate;
    private SpecialTextField startTimeField, endTimeField;
    private TextField commentNameField;
    private TextArea commentArea;
    private Preferences prefs;

    public BatchTreatmentForm() {
        this(null);
    }


    public BatchTreatmentForm(HousingUnit unit) {
        super();
        this.setFillWidth(true);
        init(unit);
        applyPreferences();
    }

    private void init(HousingUnit unit) {
        prefs = Preferences.userNodeForPackage(BatchTreatmentForm.class);

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();

        housingUnitComboBox = new HousingDropDown();
        housingUnitComboBox.setHousingUnit(unit);

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
        startTimeField = new SpecialTextField("##:##:##");
        startTimeField.setTooltip(new Tooltip("Start time of treatment, use hh:mm:ss format"));
        endTimeField = new SpecialTextField("##:##:##");
        endTimeField.setTooltip(new Tooltip("End time of treatment, use hh:mm:ss format"));
        commentNameField = new TextField();
        commentArea = new TextArea();

        Button newHousingUnit = new Button("+");
        newHousingUnit.setTooltip(new Tooltip("create a new housing unit"));
        newHousingUnit.setOnAction(event -> editHousingUnitDialog(null));

        Button newTreatmentType = new Button("+");
        newTreatmentType.setTooltip(new Tooltip("create a new type of treatment"));
        newTreatmentType.setDisable(true);

        Button newPerson = new Button("+");
        newPerson.setTooltip(new Tooltip("create a new supplier entry"));
        newPerson.setDisable(true);

        List<Person> persons;
        if (Main.getSettings().getBoolean("app_settings_activePersonSelection", true)) {
            persons = EntityHelper.getEntityList("from Person where active = True order by lastName asc", Person.class);
        } else {
            persons = EntityHelper.getEntityList("from Person order by lastName asc", Person.class);
        }
        List<TreatmentType> types;
        if (Main.getSettings().getBoolean("app_settings_validTreatmentsSelection", true)) {
            types = EntityHelper.getEntityList( "from TreatmentType where license_id is NULL OR license_id in (select id from License where end_date > CURDATE() or end_date is NULL) order by name asc", TreatmentType.class);
        } else {
           types = EntityHelper.getEntityList("from TreatmentType order by name asc", TreatmentType.class);
        }

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
        startTimeField.prefWidthProperty().bind(column2.maxWidthProperty().add(column3.maxWidthProperty()));
        treatmentStartDate.prefWidthProperty().bind(column2.maxWidthProperty());
        endTimeField.prefWidthProperty().bind(column2.maxWidthProperty().add(column3.maxWidthProperty()));
        treatmentEndDate.prefWidthProperty().bind(column2.maxWidthProperty());
        commentNameField.prefWidthProperty().bind(column2.maxWidthProperty().add(column3.maxWidthProperty()));

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
        grid.add(startTimeField, 1, 4, 2, 1);
        startTimeField.setText(dateFormat.format(date));

        grid.add(new Label("end date:"), 0, 5);
        grid.add(treatmentEndDate, 1, 5, 2, 1);
        treatmentEndDate.setOnAction(event -> endTimeField.setText(dateFormat.format(date)));
        grid.add(new Label("end time:"), 0, 6);
        grid.add(endTimeField, 1, 6, 2, 1);

        grid.add(new Separator(Orientation.HORIZONTAL), 0, 7, 3, 1);

        grid.add(new Label("comment title:"), 0, 8);
        grid.add(commentNameField, 1, 8, 2, 1);

        grid.add(new Label("comment:"), 0, 9);
        grid.add(commentArea, 0, 10, 3,3);

        grid.add(new Label("(*) required"), 0, 13);

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


    public List<Treatment> persist() {
        HousingUnit unit = housingUnitComboBox.getHousingUnit();
        if(unit == null) {
            return null;
        }
        LocalDate sdate = treatmentStartDate.getValue();
        Date startDate = getDateTime(sdate, startTimeField.getText());
        Date endDate = null;
        if (treatmentEndDate.getValue() != null) {
            endDate = getDateTime(treatmentEndDate.getValue(), endTimeField.getText());
        }

        Set<Housing> housings = unit.getAllHousings(true);
        ArrayList<Treatment> treatments = new ArrayList<>(housings.size());
        for (Housing h : housings) {
            Treatment treatment = new Treatment(startDate, h.getSubject(), personComboBox.getValue(),
                                                treatmentComboBox.getValue());
            if (endDate != null) {
                treatment.setEnd(endDate);
                if (treatment.getTreatmentType().isInvasive()) {
                    h.setEnd(endDate);
                    Communicator.pushSaveOrUpdate(h);
                }
            }
            Communicator.pushSaveOrUpdate(treatment);
            treatments.add(treatment);
            if (!commentNameField.getText().isEmpty()) {
                TreatmentNote tn = new TreatmentNote();
                tn.setName(commentNameField.getText());
                tn.setDate(startDate);
                tn.setPerson(personComboBox.getValue());
                tn.setComment(commentArea.getText());
                tn.setTreatment(treatment);
                Communicator.pushSaveOrUpdate(tn);
            }
        }
        storePreferences();
        return treatments;
    }

    private void applyPreferences() {
        if (housingUnitComboBox.getHousingUnit() == null && !prefs.get("batch_treatment_unit", "").isEmpty()) {
            List<HousingUnit> hs = EntityHelper.getEntityList("from HousingUnit where id = " + prefs.get("batch_treatment_unit", ""), HousingUnit.class);
            housingUnitComboBox.setHousingUnit(hs.get(0));
        }
        if (!prefs.get("batch_treatment_type", "").isEmpty()) {
            List<TreatmentType> sppl = EntityHelper.getEntityList("from TreatmentType where id = " + prefs.get("batch_treatment_type", ""),  TreatmentType.class);
            treatmentComboBox.getSelectionModel().select(sppl.get(0));
        }
        if (!prefs.get("batch_treatment_person", "").isEmpty()) {
            List<Person> persons = EntityHelper.getEntityList("from Person where id = " + prefs.get("batch_treatment_person", ""),  Person.class);
            personComboBox.getSelectionModel().select(persons.get(0));
        }
        if (!prefs.get("batch_treatment_startdate", "").isEmpty()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                treatmentStartDate.setValue(DateTimeHelper.toLocalDate(dateFormat.parse(prefs.get("batch_treatment_startdate", ""))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!prefs.get("batch_treatment_enddate", "").isEmpty()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                treatmentEndDate.setValue(DateTimeHelper.toLocalDate(dateFormat.parse(prefs.get("batch_treatment_enddate", ""))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void storePreferences() {
        prefs.put("batch_treatment_unit", housingUnitComboBox.getHousingUnit().getId().toString());
        prefs.put("batch_treatment_type", treatmentComboBox.getValue().getId().toString());
        prefs.put("batch_treatment_person", personComboBox.getValue().getId().toString());
        prefs.put("batch_treatment_startdate", treatmentStartDate.getValue().toString());
        prefs.put("batch_treatment_enddate", treatmentEndDate.getValue().toString());
    }


    public boolean validate(Vector<String> messages) {
        boolean valid = true;
        if (!housingUnitComboBox.getHousingUnit().getHousingType().getCanHoldSubjects()) {
            messages.add("The selected housing unit can not hold subjects!");
            valid = false;
        }
        if (housingUnitComboBox.getHousingUnit().getPopulation() == 0 &&
                treatmentComboBox.getValue().getTarget() == TreatmentTarget.subject) {
            messages.add("The selected housing unit does not hold any subjects!");
            valid = false;
        }
        if (!personComboBox.getValue().getActive() && treatmentStartDate.getValue().isBefore(LocalDate.now().minusDays(7))) {
            messages.add("The selected responsible person must not be marked inactive. Cross check with the person info.");
            valid = false;
        }
        return valid;
    }
}

