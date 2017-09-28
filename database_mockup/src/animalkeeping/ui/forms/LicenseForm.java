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

package animalkeeping.ui.forms;

import animalkeeping.logging.Communicator;
import animalkeeping.model.*;
import animalkeeping.ui.Main;
import animalkeeping.util.DateTimeHelper;
import animalkeeping.util.EntityHelper;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
import java.util.Vector;

import static animalkeeping.util.DateTimeHelper.getDateTime;
import static animalkeeping.util.DateTimeHelper.localDateToUtilDate;
import static animalkeeping.util.Dialogs.showInfo;


public class LicenseForm extends VBox {
    private ComboBox<Person> respPersonCombo;
    private ComboBox<Person> deputyPersonCombo;
    private DatePicker startDate, endDate;
    private TextField fileNoField, nameField, agencyField;
    private Label idLabel;
    private License license;
    private boolean isEdit;

    public LicenseForm() {
        this.setFillWidth(true);
        this.init();
        this.isEdit = false;
    }

    public LicenseForm(License l) {
        this();
        this.license = l;
        this.init(l);
        this.isEdit = l != null;
    }

    private  void init(License l) {
        if (l == null) {
            return;
        }
        idLabel.setText(l.getId().toString());
        respPersonCombo.getSelectionModel().select(l.getResponsiblePerson());
        deputyPersonCombo.getSelectionModel().select(l.getDeputy());
        if (l.getStartDate() != null) {
            startDate.setValue(DateTimeHelper.toLocalDate(l.getStartDate()));
        }
        if (l.getEndDate() != null) {
            endDate.setValue(DateTimeHelper.toLocalDate(l.getEndDate()));
        }

        fileNoField.setText(l.getNumber());
        nameField.setText(l.getName());
        agencyField.setText(l.getAgency());
    }

    private void init() {
        idLabel = new Label();
        respPersonCombo = new ComboBox<>();
        respPersonCombo.setConverter(new StringConverter<Person>() {
            @Override
            public String toString(Person object) {
                return object.getLastName() + ", " + object.getFirstName();
            }

            @Override
            public Person fromString(String string) {
                return null;
            }
        });
        respPersonCombo.setDisable(false);
        deputyPersonCombo = new ComboBox<>();
        deputyPersonCombo.setConverter(new StringConverter<Person>() {
            @Override
            public String toString(Person object) {
                return object.getLastName() + ", " + object.getFirstName();
            }

            @Override
            public Person fromString(String string) {
                return null;
            }
        });
        deputyPersonCombo.setDisable(false);
        startDate = new DatePicker();
        startDate.setValue(LocalDate.now());
        endDate = new DatePicker();
        endDate.setValue(LocalDate.now());

        fileNoField = new TextField();
        agencyField = new TextField();
        nameField = new TextField();

        Button newPersonButton = new Button("+");
        newPersonButton.setTooltip(new Tooltip("create a new person entry"));
        newPersonButton.setDisable(true);
        Button newPersonButton2 = new Button("+");
        newPersonButton2.setTooltip(new Tooltip("create a new person entry"));
        newPersonButton2.setDisable(true);

        GridPane grid = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints(100,100, Double.MAX_VALUE);
        column1.setHgrow(Priority.NEVER);
        ColumnConstraints column2 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
        column2.setHgrow(Priority.ALWAYS);
        ColumnConstraints column3 = new ColumnConstraints(30, 30, Double.MAX_VALUE);
        column3.setHgrow(Priority.NEVER);
        grid.getColumnConstraints().addAll(column1, column2, column3);
        respPersonCombo.prefWidthProperty().bind(column2.maxWidthProperty());
        deputyPersonCombo.prefWidthProperty().bind(column2.maxWidthProperty());
        startDate.prefWidthProperty().bind(column2.maxWidthProperty());
        fileNoField.prefWidthProperty().bind(column2.maxWidthProperty());
        nameField.prefWidthProperty().bind(column2.maxWidthProperty());
        agencyField.prefWidthProperty().bind(column2.maxWidthProperty());

        grid.setVgap(5);
        grid.setHgap(2);
        grid.add(new Label("ID:"), 0, 0);
        grid.add(idLabel, 1, 0);

        grid.add(new Label("name:"), 0,1);
        grid.add(nameField, 1,1, 2, 1);

        grid.add(new Label("file no.:"), 0, 2);
        grid.add(fileNoField, 1, 2, 2,1);

        grid.add(new Label("filing agency:"), 0, 3);
        grid.add(agencyField, 1, 3, 2, 1);

        grid.add(new Label("responsible person:"), 0, 4);
        grid.add(respPersonCombo, 1, 4, 1, 1 );
        grid.add(newPersonButton, 2, 4, 1, 1);

        grid.add(new Label("deputy:"), 0, 5);
        grid.add(deputyPersonCombo, 1, 5, 1, 1 );
        grid.add(newPersonButton2, 2, 5, 1, 1);

        grid.add(new Label("start date:"), 0, 6);
        grid.add(startDate, 1, 6, 2,1);

        grid.add(new Label("end date:"), 0, 7);
        grid.add(endDate, 1, 7, 2,1);

        this.getChildren().add(grid);

        List<Person> persons;
        if (Main.getSettings().getBoolean("app_settings_activePersonSelection", true)) {
            persons = EntityHelper.getEntityList("from Person where active = True", Person.class);
        } else {
            persons = EntityHelper.getEntityList("from Person", Person.class);
        }
        respPersonCombo.getItems().addAll(persons);
        deputyPersonCombo.getItems().addAll(persons);
    }


    public License persistLicense() {
        Date sd = localDateToUtilDate(startDate.getValue());
        Date ed = localDateToUtilDate(endDate.getValue());
        if (license == null) {
            license = new License();
        }
        license.setName(nameField.getText().isEmpty() ? null : nameField.getText());
        license.setAgency(agencyField.getText());
        license.setNumber(fileNoField.getText().isEmpty() ? null : fileNoField.getText());
        license.setResponsiblePerson(respPersonCombo.getValue());
        license.setDeputy(deputyPersonCombo.getValue());
        license.setStartDate(sd);
        license.setEndDate(ed);

        if (!Communicator.pushSaveOrUpdate(license)) {
            showInfo("Error: License entry could not be persisted! Missing required information?");
            return null;
        }
        return license;
    }

    public boolean validate(Vector<String> messages) {
        boolean valid = true;
        if (nameField.getText().isEmpty()) {
            messages.add("License name must not be empty");
            valid = false;
        }
        if (fileNoField.getText().isEmpty()) {
            messages.add("Lincense file number must not be empty!");
            valid = false;
        }
        if (respPersonCombo.getValue() == null) {
            messages.add("A responsible person must be given!");
        } else {
            if (!isEdit && !respPersonCombo.getValue().getActive()) {
                messages.add("Selected resp. person is marked inactive!");
                valid = false;
            }
        }
        if (deputyPersonCombo.getValue() != null && !isEdit && !deputyPersonCombo.getValue().getActive()) {
            messages.add("Selected deputy person is marked inactive!");
            valid = false;
        }
        if (deputyPersonCombo.getValue() != null && respPersonCombo.getValue() != null &&
                deputyPersonCombo.getValue() == respPersonCombo.getValue()) {
            messages.add("Responsible person and deputy must not be the same person!");
            valid = false;
        }
        if (endDate.getValue().isBefore(startDate.getValue()) || endDate.getValue().isEqual(startDate.getValue())) {
            messages.add("Lincense end date is equal or before the start date!");
            valid = false;
        }
        if (!nameField.getText().isEmpty() && !isEdit) {
            Vector<String> params = new Vector<>();
            params.add("name");
            Vector<Object> objects = new Vector<>();
            objects.add(nameField.getText());
            List<License> licenses = EntityHelper.getEntityList("From License where name like :name", params, objects, License.class);
            if (licenses.size() > 0) {
                messages.add("License name is already used! Select a new name!");
                valid = false;
            }
        } else if (!nameField.getText().isEmpty() && isEdit) {
            Vector<String> params = new Vector<>();
            params.add("name");
            params.add("id");
            Vector<Object> objects = new Vector<>();
            objects.add(nameField.getText());
            objects.add(license.getId());
            List<License> licenses = EntityHelper.getEntityList("From License where name like :name and id != :id", params, objects, License.class);
            if (licenses.size() > 0) {
                messages.add("License name is already used! Select a new name!");
                valid = false;
            }
        }
        return valid;
    }
}
