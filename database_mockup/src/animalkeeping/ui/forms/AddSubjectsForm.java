package animalkeeping.ui.forms;

import animalkeeping.logging.Communicator;
import animalkeeping.model.*;
import animalkeeping.ui.widgets.HousingDropDown;
import animalkeeping.ui.widgets.SpecialTextField;
import animalkeeping.util.EntityHelper;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.StringConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static animalkeeping.util.Dialogs.editHousingUnitDialog;


public class AddSubjectsForm extends VBox {
    private HousingDropDown housingUnitCombo;
    private ComboBox<Person> responsiblePersonCombo;
    private ComboBox<SpeciesType> speciesComboBox;
    private ComboBox<SupplierType> supplierComboBox;
    private DatePicker housingDate;
    private Spinner<Integer> startIdSpinner;
    private Spinner<Integer> countSpinner;
    private TextField nameField;
    private SpecialTextField timeField;
    private SubjectType st;


    public AddSubjectsForm() {
        this(null);
    }


    public AddSubjectsForm(HousingUnit unit) {
        super();
        this.setFillWidth(true);
        init(unit);
    }


    private void init(HousingUnit unit) {
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();

        housingUnitCombo = new HousingDropDown();
        housingUnitCombo.setHousingUnit(unit);
        supplierComboBox = new ComboBox<>();
        supplierComboBox.setConverter(new StringConverter<SupplierType>() {
            @Override
            public String toString(SupplierType object) {
                return object.getName();
            }

            @Override
            public SupplierType fromString(String string) {
                return null;
            }
        });
        speciesComboBox = new ComboBox<>();
        speciesComboBox.setConverter(new StringConverter<SpeciesType>() {
            @Override
            public String toString(SpeciesType object) {
                return object.getName();
            }

            @Override
            public SpeciesType fromString(String string) {
                return null;
            }
        });
        responsiblePersonCombo = new ComboBox<>();
        responsiblePersonCombo.setConverter(new StringConverter<Person>() {
            @Override
            public String toString(Person object) {
                return (object.getFirstName() + " " + object.getLastName());
            }

            @Override
            public Person fromString(String string) {
                return null;
            }
        });
        responsiblePersonCombo.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.BACK_SPACE) {
               responsiblePersonCombo.getSelectionModel().select(null);
        }
    });
        housingDate = new DatePicker(LocalDate.now());
        startIdSpinner = new Spinner<>(0, 9999, 0);
        countSpinner = new Spinner<>(0, 9999, 10);
        timeField = new SpecialTextField("##:##:##");
        timeField.setTooltip(new Tooltip("Import time use HH:mm:ss format"));
        timeField.setText(timeFormat.format(new Date()));

        Button newHousingUnit = new Button("+");
        newHousingUnit.setTooltip(new Tooltip("create a new housing unit"));
        newHousingUnit.setOnAction(event -> createNewHousingButton());

        Button newSpecies = new Button("+");
        newSpecies.setTooltip(new Tooltip("create a new species entry"));
        newSpecies.setDisable(true);

        Button newSupplier = new Button("+");
        newSupplier.setTooltip(new Tooltip("create a new supplier entry"));
        newSupplier.setDisable(true);

        Button newPerson = new Button("+");
        newPerson.setTooltip(new Tooltip("create a new person entry"));
        newPerson.setDisable(true);

        List<SupplierType> supplier = EntityHelper.getEntityList("from SupplierType", SupplierType.class);
        List<SpeciesType> species = EntityHelper.getEntityList("from SpeciesType", SpeciesType.class);
        List<SubjectType> subjectTypes = EntityHelper.getEntityList("from SubjectType where name = 'animal'", SubjectType.class);
        List<Person> persons = EntityHelper.getEntityList("from Person", Person.class);

        st = subjectTypes.get(0);
        supplierComboBox.getItems().addAll(supplier);
        speciesComboBox.getItems().addAll(species);
        responsiblePersonCombo.getItems().addAll(persons);
        nameField = new TextField();

        Label heading = new Label("Add subjects:");
        heading.setFont(new Font(Font.getDefault().getFamily(), 16));
        this.getChildren().add(heading);

        GridPane grid = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints(105,105, Double.MAX_VALUE);
        column1.setHgrow(Priority.NEVER);
        ColumnConstraints column2 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
        column2.setHgrow(Priority.ALWAYS);
        ColumnConstraints column3 = new ColumnConstraints(30, 30, Double.MAX_VALUE);
        column3.setHgrow(Priority.NEVER);
        grid.getColumnConstraints().addAll(column1, column2, column3);

        nameField.prefWidthProperty().bind(column2.maxWidthProperty());
        speciesComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
        supplierComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
        housingUnitCombo.prefWidthProperty().bind(column2.maxWidthProperty());
        responsiblePersonCombo.prefWidthProperty().bind(column2.maxWidthProperty());
        timeField.prefWidthProperty().bind(column2.maxWidthProperty());
        housingDate.prefWidthProperty().bind(column2.maxWidthProperty());
        startIdSpinner.prefWidthProperty().bind(column2.maxWidthProperty());
        countSpinner.prefWidthProperty().bind(column2.maxWidthProperty());

        grid.setVgap(5);
        grid.setHgap(2);
        grid.add(new Label("name pattern(*):"), 0, 0);
        grid.add(nameField, 1, 0, 2, 1);

        grid.add(new Label("housing unit(*):"), 0, 1);
        grid.add(housingUnitCombo, 1, 1, 1, 1 );
        grid.add(newHousingUnit, 2, 1, 1, 1);

        grid.add(new Label("resp. person:"), 0, 2);
        grid.add(responsiblePersonCombo, 1, 2, 1, 1 );
        grid.add(newPerson, 2, 2, 1, 1);

        grid.add(new Label("supplier(*):"), 0, 3);
        grid.add(supplierComboBox, 1, 3, 1,1);
        grid.add(newSupplier, 2, 3, 1, 1);

        grid.add(new Label("species(*):"), 0,4);
        grid.add(speciesComboBox, 1,4, 1, 1);
        grid.add(newSpecies, 2, 4, 1, 1);

        grid.add(new Label("import date(*):"), 0, 5);
        grid.add(housingDate, 1, 5, 2, 1);

        grid.add(new Label("time(*):"), 0, 6);
        grid.add(timeField, 1, 6, 2, 1);

        grid.add(new Label("subject count"), 0, 7);
        grid.add(countSpinner, 1, 7, 2, 1);

        grid.add(new Label("start index:"), 0, 8);
        grid.add(startIdSpinner, 1, 8, 2, 1);

        grid.add(new Label("(*) required"), 0, 9);

        this.getChildren().add(grid);
    }


    private String createName(String prefix, String name, Integer count, Integer digit_count) {
        String full_name = prefix + name;
        String count_str = count.toString();
        int fill = digit_count - count_str.length();
        for ( int i=0; i < fill; i++) {
            full_name += "0";
        }
        full_name += count_str;
        return full_name;
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


    public Integer persistSubjects() {
        LocalDate hdate = housingDate.getValue();
        String d = hdate.toString();
        if (!validateTime(timeField.getText())) {
            return -1;
        }
        String t = timeField.getText();
        String datetimestr = d + " " + t;
        DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date;
        try {
            date = dateTimeFormat.parse(datetimestr);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        String prefix = String.valueOf(hdate.getYear());
        String name = nameField.getText();
        Integer initial_id = startIdSpinner.getValue();

        for (int i = 0; i < countSpinner.getValue(); i++) {
            String full_name = createName(prefix, name, initial_id + i, 4);
            Subject s = new Subject();
            s.setName(full_name);
            s.setSpeciesType(speciesComboBox.getValue());
            s.setSupplier(supplierComboBox.getValue());
            s.setGender(Gender.unknown);
            s.setResponsiblePerson(responsiblePersonCombo.getValue());
            s.setSubjectType(st);

            Housing h = new Housing(s, housingUnitCombo.getHousingUnit(), date);
            HashSet<Housing> housings = new HashSet<>(1);
            housings.add(h);
            s.setHousings(housings);
            Communicator.pushSaveOrUpdate(s);
        }
        return 0;
    }

    private void createNewHousingButton() {
        HousingUnit u = editHousingUnitDialog(null, this.housingUnitCombo.getHousingUnit());
        if (u != null) {
            this.housingUnitCombo.setHousingUnit(u);
        }
    }

}
