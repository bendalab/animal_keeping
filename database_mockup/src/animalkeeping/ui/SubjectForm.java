package animalkeeping.ui;

import animalkeeping.logging.Communicator;
import animalkeeping.model.*;
import animalkeeping.util.DateTimeHelper;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static animalkeeping.util.DateTimeHelper.getDateTime;
import static animalkeeping.util.DateTimeHelper.localDateToUtilDate;
import static animalkeeping.util.DateTimeHelper.toLocalDate;


public class SubjectForm extends VBox {
    private ComboBox<SpeciesType> speciesComboBox;
    private ComboBox<SubjectType> subjectTypeComboBox;
    private ComboBox<SupplierType> supplierComboBox;
    private ComboBox<HousingUnit> housingUnitComboBox;
    private ComboBox<Gender> genderComboBox;
    private DatePicker importDate, birthDate;
    private TextField nameField, aliasField;
    private SpecialTextField importTimeField;
    private Label idLabel;
    private Subject subject = null;

    public SubjectForm() {
        this.setFillWidth(true);
        this.init();
    }

    public SubjectForm(Subject s) {
        this();
        this.subject = s;
        this.init(s);
    }

    private  void init(Subject s) {
        if (s == null) {
            return;
        }
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        idLabel.setText(s.getId().toString());
        supplierComboBox.getSelectionModel().select(s.getSupplier());
        speciesComboBox.getSelectionModel().select(s.getSpeciesType());
        subjectTypeComboBox.getSelectionModel().select(s.getSubjectType());
        genderComboBox.getSelectionModel().select(s.getGender());
        birthDate.setValue(s.getBirthday() != null ? toLocalDate(s.getBirthday()) : null);
        Housing h = s.getHousings().iterator().next();
        LocalDate sd = DateTimeHelper.toLocalDate(h.getStart());
        importDate.setValue(sd);
        importTimeField.setText(timeFormat.format(h.getStart()));
        nameField.setText(s.getName());
        aliasField.setText(s.getAlias());
        housingUnitComboBox.getSelectionModel().select(h.getHousing());
        //housingTable.getSelectionModel().select(h.getHousing());
    }

    private void init() {
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        idLabel = new Label();
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
        subjectTypeComboBox = new ComboBox<>();
        subjectTypeComboBox.setConverter(new StringConverter<SubjectType>() {
            @Override
            public String toString(SubjectType object) {
                return object.getName();
            }

            @Override
            public SubjectType fromString(String string) {
                return null;
            }
        });
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
        // housingTable = new HousingTable();
        genderComboBox = new ComboBox<>();
        genderComboBox.setConverter(new StringConverter<Gender>() {
            @Override
            public String toString(Gender object) {
                return object.toString();
            }

            @Override
            public Gender fromString(String string) {
                if (string.equals("unknown"))
                    return Gender.unknown;
                if (string.equals("female"))
                    return Gender.female;
                if (string.equals("male"))
                    return Gender.male;
                return Gender.hermaphrodite;
            }
        });

        birthDate = new DatePicker();

        importDate = new DatePicker();
        importDate.setValue(LocalDate.now());
        importTimeField = new SpecialTextField("##:##:##");

        aliasField = new TextField();
        nameField = new TextField();

        Button newSpeciesButton = new Button("+");
        newSpeciesButton.setTooltip(new Tooltip("create a new species entry"));
        newSpeciesButton.setOnAction(event -> {
            SpeciesType st = Dialogs.editSpeciesTypeDialog(null);
            if (st != null) {
                speciesComboBox.getItems().add(st);
                speciesComboBox.getSelectionModel().select(st);
            }
        });
        Button newSupplierButton = new Button("+");
        newSupplierButton.setTooltip(new Tooltip("create a new supplier entry"));
        newSupplierButton.setOnAction(event -> {
            SupplierType st = Dialogs.editSupplierTypeDialog(null);
            if (st != null) {
                supplierComboBox.getItems().add(st);
                supplierComboBox.getSelectionModel().select(st);
            }
        });
        Button newSubjectTypeButton = new Button("+");
        newSubjectTypeButton.setTooltip(new Tooltip("create a new subject type entry"));
        newSubjectTypeButton.setOnAction(event -> {
            SubjectType st = Dialogs.editSubjectTypeDialog(null);
            if (st != null) {
                subjectTypeComboBox.getItems().add(st);
                subjectTypeComboBox.getSelectionModel().select(st);
            }
        });
        Button newHousingUnitButton = new Button("+");
        newHousingUnitButton.setTooltip(new Tooltip("create a new housing unit entry"));
        newHousingUnitButton.setDisable(true);


        GridPane grid = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints(100,100, Double.MAX_VALUE);
        column1.setHgrow(Priority.NEVER);
        ColumnConstraints column2 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
        column2.setHgrow(Priority.ALWAYS);
        ColumnConstraints column3 = new ColumnConstraints(30, 30, Double.MAX_VALUE);
        column3.setHgrow(Priority.NEVER);
        grid.getColumnConstraints().addAll(column1, column2, column3);
        speciesComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
        supplierComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
        subjectTypeComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
        housingUnitComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
        importDate.prefWidthProperty().bind(column2.maxWidthProperty());
        importTimeField.prefWidthProperty().bind(column2.maxWidthProperty());
        nameField.prefWidthProperty().bind(column2.maxWidthProperty());
        aliasField.prefWidthProperty().bind(column2.maxWidthProperty());
        genderComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
        birthDate.prefWidthProperty().bind(column2.maxWidthProperty());

        grid.setVgap(5);
        grid.setHgap(2);
        grid.add(new Label("ID:"), 0, 0);
        grid.add(idLabel, 1, 0);

        grid.add(new Label("name*:"), 0,1);
        grid.add(nameField, 1,1, 2, 1);

        grid.add(new Label("alias:"), 0, 2);
        grid.add(aliasField, 1, 2, 2, 1);

        grid.add(new Label("date of birth:"), 0,3);
        grid.add(birthDate, 1,3, 2, 1);

        grid.add(new Label("gender:"), 0, 4);
        grid.add(genderComboBox, 1, 4, 2, 1);

        grid.add(new Label("species*:"), 0, 5);
        grid.add(speciesComboBox, 1, 5, 1, 1);
        grid.add(newSpeciesButton, 2, 5, 1, 1);

        grid.add(new Label("subject type*:"), 0, 6);
        grid.add(subjectTypeComboBox, 1, 6, 1, 1 );
        grid.add(newSubjectTypeButton, 2, 6, 1, 1);

        grid.add(new Label("supplier*:"), 0, 7);
        grid.add(supplierComboBox, 1, 7, 1, 1 );
        grid.add(newSupplierButton, 2, 7, 1, 1);

        grid.add(new Label("housing unit*:"), 0, 8);
        grid.add(housingUnitComboBox, 1, 8, 1, 1 );
        grid.add(newHousingUnitButton, 2, 8, 1, 1);

        grid.add(new Label("import date*:"), 0, 9);
        grid.add(importDate, 1, 9, 2,1);

        grid.add(new Label("import time*:"), 0, 10);
        grid.add(importTimeField, 1, 10, 2,1);

        grid.add(new Label("(* required)"), 0, 11);

        //this.getChildren().add(new ScrollPane(housingTable));
        this.getChildren().add(grid);

        List<SpeciesType> species= EntityHelper.getEntityList("from SpeciesType", SpeciesType.class);
        List<SubjectType> types = EntityHelper.getEntityList("from SubjectType", SubjectType.class);
        List<SupplierType> supplier = EntityHelper.getEntityList("from SupplierType", SupplierType.class);
        List<HousingUnit> housingUnits = EntityHelper.getEntityList("from HousingUnit", HousingUnit.class);

        subjectTypeComboBox.getItems().addAll(types);
        supplierComboBox.getItems().addAll(supplier);
        speciesComboBox.getItems().addAll(species);
        housingUnitComboBox.getItems().addAll(housingUnits);
        List<Gender> sexes = new ArrayList<>(4);
        sexes.add(Gender.unknown);
        sexes.add(Gender.female);
        sexes.add(Gender.male);
        sexes.add(Gender.hermaphrodite);
        genderComboBox.getItems().addAll(sexes);
        genderComboBox.getSelectionModel().select(Gender.unknown);
    }


    public Subject persistSubject() {
        Date sd = getDateTime(importDate.getValue(), importTimeField.getText());
        if (subject == null) {
            subject = new Subject();
        }
        subject.setName(nameField.getText());
        subject.setAlias(aliasField.getText());
        subject.setSpeciesType(speciesComboBox.getValue());
        subject.setSupplier(supplierComboBox.getValue());
        subject.setSubjectType(subjectTypeComboBox.getValue());
        subject.setGender(genderComboBox.getValue());
        subject.setBirthday(birthDate.getValue() != null ? localDateToUtilDate(birthDate.getValue()) : null);
        Housing h;
        if (subject.getHousings().iterator().hasNext()) {
            h = subject.getHousings().iterator().next();
        } else {
            h = new Housing();
        }
        h.setStart(sd);
        h.setHousing(housingUnitComboBox.getValue());
        h.setSubject(subject);

        Communicator.pushSaveOrUpdate(subject);
        Communicator.pushSaveOrUpdate(h);
        return subject;
    }

}
