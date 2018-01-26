package animalkeeping.ui.forms;

import animalkeeping.logging.Communicator;
import animalkeeping.model.*;
import animalkeeping.ui.Main;
import animalkeeping.ui.widgets.HousingDropDown;
import animalkeeping.ui.widgets.SpecialTextField;
import animalkeeping.util.DateTimeHelper;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.prefs.Preferences;

import static animalkeeping.util.DateTimeHelper.getDateTime;
import static animalkeeping.util.DateTimeHelper.localDateToUtilDate;
import static animalkeeping.util.DateTimeHelper.toLocalDate;

/******************************************************************************
 animalBase
 animalkeeping.ui.forms


 Copyright (c) 2017 Neuroethology Lab, University of Tuebingen,
 Jan Grewe <jan.grewe@g-node.org>,
 Dennis Huben <dennis.huben@rwth-aachen.de>

 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this list
 of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice, this
 list of conditions and the following disclaimer in the documentation and/or other
 materials provided with the distribution.

 3. Neither the name of the copyright holder nor the names of its contributors may
 be used to endorse or promote products derived from this software without specific
 prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 DAMAGE.

 *****************************************************************************/

public class SubjectForm extends VBox {
    private ComboBox<SpeciesType> speciesComboBox;
    private ComboBox<SubjectType> subjectTypeComboBox;
    private ComboBox<SupplierType> supplierComboBox;
    private ComboBox<Person> personComboBox;
    private HousingDropDown housingUnitComboBox;
    private ComboBox<Gender> genderComboBox;
    private DatePicker importDate, birthDate;
    private TextField nameField, aliasField;
    private SpecialTextField importTimeField;
    private Label idLabel;
    private Subject subject = null;
    private Preferences prefs;
    private boolean isEdit;

    public SubjectForm() {
        this.setFillWidth(true);
        this.init();
        this.isEdit = false;
    }

    public SubjectForm(Subject s) {
        this();
        this.subject = s;
        applyPreferences();
        this.init(s);
        this.isEdit = s != null;
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
        personComboBox.getSelectionModel().select(s.getResponsiblePerson());
        birthDate.setValue(s.getBirthday() != null ? toLocalDate(s.getBirthday()) : null);
        Housing h = s.getHousings().iterator().next();
        LocalDate sd = DateTimeHelper.toLocalDate(h.getStart());
        importDate.setValue(sd);
        importTimeField.setText(timeFormat.format(h.getStart()));
        nameField.setText(s.getName());
        aliasField.setText(s.getAlias());
        housingUnitComboBox.setHousingUnit(h.getHousing());
    }

    private void init() {
        prefs = Preferences.userNodeForPackage(SubjectForm.class);

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
        housingUnitComboBox = new HousingDropDown();
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
        personComboBox = new ComboBox<>();
        personComboBox.setConverter(new StringConverter<Person>() {
            @Override
            public String toString(Person object) {
                if (object.getId() == null) {
                    return "None";
                } else {
                    return (object.getFirstName() + " " + object.getLastName());
                }
            }

            @Override
            public Person fromString(String string) {
                return null;
            }
        });
        personComboBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.BACK_SPACE) {
                personComboBox.getSelectionModel().select(null);
            }
        });

        birthDate = new DatePicker();
        birthDate.getEditor().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.BACK_SPACE) {
                birthDate.setValue(null);
            }
        });
        importDate = new DatePicker();
        importDate.setValue(LocalDate.now());
        importTimeField = new SpecialTextField("##:##:##");
        importTimeField.setText("00:00:00");
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

        Button newPersonButton = new Button("+");
        newPersonButton.setTooltip(new Tooltip("create a new person entry"));
        newPersonButton.setDisable(true);

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
        personComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
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

        grid.add(new Label("resp. person:"), 0, 9);
        grid.add(personComboBox, 1, 9, 1, 1 );
        grid.add(newPersonButton, 2, 9, 1, 1);

        grid.add(new Label("import date*:"), 0, 10);
        grid.add(importDate, 1, 10, 2,1);

        grid.add(new Label("import time*:"), 0, 11);
        grid.add(importTimeField, 1, 11, 2,1);

        grid.add(new Label("(* required)"), 0, 12);

        this.getChildren().add(grid);

        List<SpeciesType> species= EntityHelper.getEntityList("from SpeciesType order by name asc", SpeciesType.class);
        List<SubjectType> types = EntityHelper.getEntityList("from SubjectType order by name asc", SubjectType.class);
        List<SupplierType> supplier = EntityHelper.getEntityList("from SupplierType order by name asc", SupplierType.class);
        List<Person> persons;

        if (Main.getSettings().getBoolean("app_settings_activePersonSelection", true)) {
            persons = EntityHelper.getEntityList("from Person where active = True order by lastName asc", Person.class);
        } else {
            persons = EntityHelper.getEntityList("from Person order by lastName asc", Person.class);
        }
        Person p = new Person();
        persons.add(p);

        subjectTypeComboBox.getItems().addAll(types);
        subjectTypeComboBox.getSelectionModel().select(0);
        supplierComboBox.getItems().addAll(supplier);
        speciesComboBox.getItems().addAll(species);
        personComboBox.getItems().addAll(persons);

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
        subject.setResponsiblePerson((personComboBox.getValue() != null && personComboBox.getValue().getId() != null) ?
                personComboBox.getValue() : null);
        subject.setBirthday(birthDate.getValue() != null ? localDateToUtilDate(birthDate.getValue()) : null);
        Housing h;
        if (subject.getHousings().iterator().hasNext()) {
            h = subject.getHousings().iterator().next();
        } else {
            h = new Housing();
        }
        h.setStart(sd);
        h.setHousing(housingUnitComboBox.getHousingUnit());
        Communicator.pushSaveOrUpdate(subject);
        EntityHelper.refreshEntity(subject);
        h.setSubject(subject);
        Communicator.pushSaveOrUpdate(h);
        storePreferences();
        return subject;
    }

    private void applyPreferences() {
        String gender = prefs.get("subject_gender", "");
        if (!gender.isEmpty()) {
            if (gender.equalsIgnoreCase("female"))
                genderComboBox.getSelectionModel().select(Gender.female);
            else if (gender.equalsIgnoreCase("male"))
                genderComboBox.getSelectionModel().select(Gender.male);
            else if (gender.equalsIgnoreCase("unknown"))
                genderComboBox.getSelectionModel().select(Gender.unknown);
            else
                genderComboBox.getSelectionModel().select(Gender.hermaphrodite);
        }
        if (!prefs.get("subject_person", "").isEmpty()) {
            List<Person> persons = EntityHelper.getEntityList("from Person where id = " + prefs.get("subject_person", ""),  Person.class);
            personComboBox.getSelectionModel().select(persons.get(0));
        }
        if (!prefs.get("subject_type", "").isEmpty()) {
            List<SubjectType> types = EntityHelper.getEntityList("from SubjectType where id = " + prefs.get("subject_type", ""),  SubjectType.class);
            subjectTypeComboBox.getSelectionModel().select(types.get(0));
        }
        if (!prefs.get("subject_importdate", "").isEmpty()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                importDate.setValue(DateTimeHelper.toLocalDate(dateFormat.parse(prefs.get("subject_importdate", ""))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!prefs.get("subject_birthdate", "").isEmpty()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                birthDate.setValue(DateTimeHelper.toLocalDate(dateFormat.parse(prefs.get("subject_birthdate", ""))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!prefs.get("subject_supplier", "").isEmpty()) {
            List<SupplierType> types = EntityHelper.getEntityList("from SupplierType where id = " + prefs.get("subject_supplier", ""),  SupplierType.class);
            supplierComboBox.getSelectionModel().select(types.get(0));
        }
        if (!prefs.get("subject_species", "").isEmpty()) {
            List<SpeciesType> types = EntityHelper.getEntityList("from SpeciesType where id = " + prefs.get("subject_species", ""),  SpeciesType.class);
            speciesComboBox.getSelectionModel().select(types.get(0));
        }
        if (!prefs.get("subject_housingunit", "").isEmpty()) {
            List<HousingUnit> units = EntityHelper.getEntityList("from HousingUnit where id = " + prefs.get("subject_housingunit", ""),  HousingUnit.class);
            housingUnitComboBox.setHousingUnit(units.get(0));
        }
    }

    private void storePreferences() {
        prefs.put("subject_gender", genderComboBox.getValue().toString());
        prefs.put("subject_person", (personComboBox.getValue() != null && personComboBox.getValue().getId() != null) ?
                personComboBox.getValue().getId().toString() : "");
        prefs.put("subject_supplier", supplierComboBox.getValue().getId().toString());
        prefs.put("subject_type", subjectTypeComboBox.getValue().getId().toString());
        prefs.put("subject_species", speciesComboBox.getValue().getId().toString());
        prefs.put("subject_housingunit", housingUnitComboBox.getHousingUnit().getId().toString());
        prefs.put("subject_birthdate", birthDate.getValue() != null ? birthDate.getValue().toString() : "");
        prefs.put("subject_importdate", importDate.getValue() != null ? importDate.getValue().toString() : "");
    }


    public boolean validate(Vector<String> messages) {
        boolean valid = true;
        if (nameField.getText().isEmpty()) {
            messages.add("Subject name must not be empty!");
            valid = false;
        } else {
            if (!isEdit) {
                Vector<String> params = new Vector<>();
                params.add("name");
                Vector<Object> objects = new Vector<>();
                objects.add(nameField.getText());
                if (EntityHelper.getEntityList("From Subject where name like :name", params, objects, Subject.class).size() > 0) {
                    messages.add("Subject name is already used! Select another name.");
                    valid = false;
                }
            }
        }
        if (importDate.getValue() == null) {
            messages.add("An import date must be given!");
            valid = false;
        } else if (birthDate.getValue() != null && birthDate.getValue().isAfter(importDate.getValue())) {
            messages.add("Import date is before subject birth date!");
            valid = false;
        }
        if (speciesComboBox.getValue() == null) {
            messages.add("Species information must be given!");
            valid = false;
        }
        if (subjectTypeComboBox.getValue() == null) {
            messages.add("Subject type must be given!");
            valid = false;
        }
        if (supplierComboBox.getValue() == null) {
            messages.add("Supplier information must be provided!");
            valid = false;
        }
        if (housingUnitComboBox.getHousingUnit() == null) {
            messages.add("A Housing unit must be selected!");
            valid = false;
        } else if (!housingUnitComboBox.getHousingUnit().getHousingType().getCanHoldSubjects()) {
            messages.add("The selected housing unit can not hold subjects!");
            valid = false;
        }
        if ((personComboBox.getValue() != null) && (personComboBox.getValue().getId() != null) &&
                !personComboBox.getValue().getActive()) {
            messages.add("The selected responsible person is marked inactive!");
            valid = false;
        }
        return  valid;
    }
}
