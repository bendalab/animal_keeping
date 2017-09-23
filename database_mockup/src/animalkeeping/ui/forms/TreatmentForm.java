package animalkeeping.ui.forms;

import animalkeeping.logging.Communicator;
import animalkeeping.model.*;
import animalkeeping.ui.Main;
import animalkeeping.ui.widgets.SpecialTextField;
import animalkeeping.util.DateTimeHelper;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.prefs.Preferences;

import static animalkeeping.util.DateTimeHelper.getDateTime;


public class TreatmentForm extends VBox {
    private ComboBox<TreatmentType> typeComboBox;
    private ComboBox<Person> personComboBox;
    private ComboBox<Subject> subjectComboBox;
    private DatePicker startDate, endDate;
    private SpecialTextField startTimeField, endTimeField;
    private TextField commentNameField;
    private CheckBox immediateEnd;
    private TextArea commentArea;
    private Subject subject;
    private Treatment treatment;
    private TreatmentType type;
    private Label idLabel;
    private Preferences prefs;


    public TreatmentForm() {
        this.setFillWidth(true);
        this.subject = null;
        this.treatment = null;
        this.type = null;
        this.init();
        applyPreferences();
    }

    public TreatmentForm(Subject s) {
        this();
        setSubject(s);
    }

    public TreatmentForm(Treatment t) {
        this();
        this.setTreatment(t);
    }

    public TreatmentForm(TreatmentType type) {
        this();
        setTreatmentType(type);
    }

    private  void setTreatment(Treatment t) {
        if (t == null)
            return;
        this.treatment = t;
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        idLabel.setText(t.getId().toString());
        personComboBox.getSelectionModel().select(t.getPerson());
        subjectComboBox.getSelectionModel().select(t.getSubject());
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
        prefs = Preferences.userNodeForPackage(TreatmentForm.class);

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
        subjectComboBox = new ComboBox<>();
        subjectComboBox.setConverter(new StringConverter<Subject>() {
            @Override
            public String toString(Subject object) {
                return object.getName();
            }

            @Override
            public Subject fromString(String string) {
                return null;
            }
        });
        startDate = new DatePicker();
        startDate.setValue(LocalDate.now());
        startTimeField = new SpecialTextField("##:##:##");
        startTimeField.setText(timeFormat.format(new Date()));
        endDate = new DatePicker();
        endTimeField = new SpecialTextField("##:##:##");
        endDate.setOnAction(event -> endTimeField.setText(timeFormat.format(new Date())));
        commentArea = new TextArea();
        commentArea.setWrapText(true);
        commentNameField = new TextField();

        immediateEnd = new CheckBox("treatment ends immediately");
        immediateEnd.setSelected(false);
        immediateEnd.setOnAction(event -> {
            endDate.setDisable(immediateEnd.isSelected());
            endTimeField.setDisable(immediateEnd.isSelected());
            endDate.setValue(startDate.getValue());
            endTimeField.setText(startTimeField.getText());
        });

        Button newTypeButton = new Button("+");
        newTypeButton.setTooltip(new Tooltip("create a new treatment type entry"));
        newTypeButton.setOnAction(event -> {
            TreatmentType treatmentType = Dialogs.editTreatmentTypeDialog(null);
            if (treatmentType != null) {
                List<TreatmentType> types = EntityHelper.getEntityList("From TreatmentType", TreatmentType.class);
                typeComboBox.getItems().clear();
                typeComboBox.getItems().addAll(types);
                typeComboBox.getSelectionModel().select(treatmentType);
            }
        });
        Button newPersonButton = new Button("+");
        newPersonButton.setTooltip(new Tooltip("create a new person entry"));
        newPersonButton.setOnAction(event -> {
            Person p = Dialogs.editPersonDialog(null);
            if (p != null) {
                List<Person> persons = EntityHelper.getEntityList("From Person", Person.class);
                personComboBox.getItems().clear();
                personComboBox.getItems().addAll(persons);
                personComboBox.getSelectionModel().select(p);
            }
        });
        Button newSubjectButton = new Button("+");
        newSubjectButton.setTooltip(new Tooltip("create a new subject entry"));
        newSubjectButton.setOnAction(event -> {
            Subject s = Dialogs.editSubjectDialog(null);
            if(s != null) {
                List<Subject> subjects = EntityHelper.getEntityList("From Subject", Subject.class);
                subjectComboBox.getItems().clear();
                subjectComboBox.getItems().addAll(subjects);
                subjectComboBox.getSelectionModel().select(s);
            }
        });

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
        subjectComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
        startTimeField.prefWidthProperty().bind(column2.prefWidthProperty().add(column3.prefWidthProperty()));
        startDate.prefWidthProperty().bind(column2.maxWidthProperty().add(column3.prefWidthProperty()));
        endTimeField.prefWidthProperty().bind(column2.prefWidthProperty().add(column3.prefWidthProperty()));
        endDate.prefWidthProperty().bind(column2.maxWidthProperty().add(column3.prefWidthProperty()));
        immediateEnd.prefWidthProperty().bind(column2.maxWidthProperty());
        immediateEnd.setFont(new Font(Font.getDefault().getFamily(), 9));
        commentNameField.prefWidthProperty().bind(column2.prefWidthProperty().add(column3.prefWidthProperty()));

        grid.setVgap(5);
        grid.setHgap(2);
        grid.add(new Label("ID:"), 0, 0);
        grid.add(idLabel, 1, 0);

        grid.add(new Label("treatment(*):"), 0, 1);
        grid.add(typeComboBox, 1, 1, 1, 1);
        grid.add(newTypeButton, 2, 1, 1, 1);

        grid.add(new Label("subject(*):"), 0, 2);
        grid.add(subjectComboBox, 1, 2, 1, 1);
        grid.add(newSubjectButton, 2, 2, 1, 1);

        grid.add(new Label("person(*):"), 0, 3);
        grid.add(personComboBox, 1, 3, 1, 1 );
        grid.add(newPersonButton, 2, 3, 1, 1);

        grid.add(new Label("start date(*):"), 0, 4);
        grid.add(startDate, 1, 4, 2,1);

        grid.add(new Label("start time(*):"), 0,5);
        grid.add(startTimeField, 1,5, 2, 1);

        grid.add(immediateEnd, 1, 6);

        grid.add(new Label("end date:"), 0, 7);
        grid.add(endDate, 1, 7, 2, 1);

        grid.add(new Label("end time:"), 0, 8);
        grid.add(endTimeField, 1, 8, 2, 1);
        grid.add(new Label("(*required)"), 0, 9);

        grid.add(new Separator(Orientation.HORIZONTAL), 0, 10, 3, 1);

        grid.add(new Label("comment title:"), 0, 11);
        grid.add(commentNameField, 1, 11, 2, 1);
        grid.add(new Label("comment:"), 0,12);
        grid.add(commentArea, 0, 13, 3, 3);
        this.getChildren().add(grid);

        Preferences settings = Preferences.userNodeForPackage(FilterSettings.class);
        List<Person> persons;
        if (settings.getBoolean("app_settings_activePersonSelection", true)) {
            persons = EntityHelper.getEntityList("from Person where active = True", Person.class);
        } else {
            persons = EntityHelper.getEntityList("from Person", Person.class);
        }
        List<TreatmentType> types;
        if (settings.getBoolean("app_settings_validTreatmentsSelection", true)) {
            types = EntityHelper.getEntityList( "from TreatmentType where license_id is NULL OR license_id in (select id from License where end_date > CURDATE() or end_date is NULL)", TreatmentType.class);
        } else {
            types = EntityHelper.getEntityList("from TreatmentType", TreatmentType.class);
        }
        List<Subject> subjects;
        if (settings.getBoolean("app_settings_availableSubjectsSelection", true)) {
            subjects = EntityHelper.getEntityList("SELECT s FROM Subject s, Housing h WHERE h.subject = s and h.end IS NULL", Subject.class);
        } else {
            subjects = EntityHelper.getEntityList("FROM Subject", Subject.class);
        }
        typeComboBox.getItems().addAll(types);
        personComboBox.getItems().addAll(persons);
        subjectComboBox.getItems().addAll(subjects);
    }


    public void setSubject(Subject s) {
        this.subject = s;
        subjectComboBox.getSelectionModel().select(s);
        subjectComboBox.setDisable(true);
    }


    public void setTreatmentType(TreatmentType type) {
        this.type = type;
        this.typeComboBox.getSelectionModel().select(type);
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
        treatment.setSubject(subjectComboBox.getValue());
        treatment.setPerson(personComboBox.getValue());
        treatment.setTreatmentType(typeComboBox.getValue());
        Communicator.pushSaveOrUpdate(treatment);
        if (treatment.getTreatmentType().isInvasive() && treatment.getEnd() != null) {
            Housing h = treatment.getSubject().getCurrentHousing();
            h.setEnd(treatment.getEnd());
            Communicator.pushSaveOrUpdate(h);
        }
        if (!commentArea.getText().isEmpty()) {
            TreatmentNote note = new TreatmentNote();
            note.setName(commentNameField.getText());
            note.setComment(commentArea.getText());
            note.setPerson(personComboBox.getValue());
            note.setDate(sd);
            note.setTreatment(treatment);
            if (!Communicator.pushSaveOrUpdate(note)) {
                return null;
            }
        }
        storePreferences();
        return treatment;
    }

    private void applyPreferences() {
        if (!prefs.get("treatment_type", "").isEmpty()) {
            List<TreatmentType> sppl = EntityHelper.getEntityList("from TreatmentType where id = " + prefs.get("treatment_type", ""),  TreatmentType.class);
            typeComboBox.getSelectionModel().select(sppl.get(0));
        }
        if (!prefs.get("treatment_person", "").isEmpty()) {
            List<Person> persons = EntityHelper.getEntityList("from Person where id = " + prefs.get("treatment_person", ""),  Person.class);
            personComboBox.getSelectionModel().select(persons.get(0));
        }
        if (!prefs.get("treatment_subject", "").isEmpty()) {
            List<Subject> subjects = EntityHelper.getEntityList("from Subject where id = " + prefs.get("treatment_subject", ""),  Subject.class);
            subjectComboBox.getSelectionModel().select(subjects.get(0));
        }
        if (!prefs.get("treatment_startdate", "").isEmpty()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                startDate.setValue(DateTimeHelper.toLocalDate(dateFormat.parse(prefs.get("treatment_startdate", ""))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!prefs.get("treatment_enddate", "").isEmpty()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                endDate.setValue(DateTimeHelper.toLocalDate(dateFormat.parse(prefs.get("treatment_enddate", ""))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        immediateEnd.setSelected(prefs.getBoolean("treatment_immediate_end", false));
        immediateEnd.fire();
    }

    private void storePreferences() {
        prefs.put("treatment_type", typeComboBox.getValue().getId().toString());
        prefs.put("treatment_person", personComboBox.getValue().getId().toString());
        prefs.put("treatment_subject", subjectComboBox.getValue().getId().toString());
        prefs.put("treatment_startdate", startDate.getValue().toString());
        prefs.put("treatment_enddate", endDate.getValue().toString());
        prefs.putBoolean("treatment_immediate_end", immediateEnd.isSelected());
    }

}
