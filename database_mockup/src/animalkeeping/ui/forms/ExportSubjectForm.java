package animalkeeping.ui.forms;

import animalkeeping.logging.Communicator;
import animalkeeping.model.*;
import animalkeeping.ui.Main;
import animalkeeping.ui.widgets.SpecialTextField;
import animalkeeping.util.DateTimeHelper;
import animalkeeping.util.EntityHelper;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.prefs.Preferences;

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

 * Created by jan on 22.08.17.

 *****************************************************************************/

public class ExportSubjectForm extends VBox {
    private Preferences prefs;
    private TextArea commentArea;
    private SpecialTextField tf;
    private DatePicker dp;
    private ComboBox<Person> personComboBox;
    private ComboBox<Subject> subjectComboBox;

    public ExportSubjectForm() {
       this(null);
    }


    public ExportSubjectForm(Subject subject) {
        super();
        init();
        setSubject(subject);
        applyPreferences();
    }


    private void init() {
        prefs = Preferences.userNodeForPackage(ExportSubjectForm.class);

        dp = new DatePicker();
        dp.setValue(LocalDate.now());
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        tf = new SpecialTextField("##:##:##");
        tf.setTooltip(new Tooltip("Import time use HH:mm:ss format"));
        tf.setText(timeFormat.format(new Date()));

        personComboBox = new ComboBox<>();
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
        List<Person> persons;
        if (Main.getSettings().getBoolean("app_settings_activePersonSelection", true)) {
            persons = EntityHelper.getEntityList("from Person where active = True order by lastName asc", Person.class);
        } else {
            persons = EntityHelper.getEntityList("from Person order by lastName asc", Person.class);
        }
        personComboBox.getItems().addAll(persons);

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
        List<Subject> subjects;
        if (Main.getSettings().getBoolean("app_settings_availableSubjectsSelection", true)) {
            subjects = EntityHelper.getEntityList("SELECT s FROM Subject s, Housing h WHERE h.subject = s and h.end IS NULL order by s.name asc", Subject.class);
        } else {
            subjects = EntityHelper.getEntityList("FROM Subject order by name asc", Subject.class);
        }
        subjectComboBox.getItems().addAll(subjects);

        commentArea = new TextArea();

        GridPane grid = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints(100, 100, Double.MAX_VALUE);
        column1.setHgrow(Priority.NEVER);
        ColumnConstraints column2 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
        column2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(column1, column2);
        dp.prefWidthProperty().bind(column2.maxWidthProperty());
        tf.prefWidthProperty().bind(column2.maxWidthProperty());
        personComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
        commentArea.prefWidthProperty().bind(column2.maxWidthProperty());

        grid.setVgap(5);
        grid.setHgap(2);
        grid.add(new Label("subject: "), 0, 0);
        grid.add(subjectComboBox, 1, 0);

        grid.add(new Label("date:"), 0, 1);
        grid.add(dp, 1, 1);

        grid.add(new Label("time:"), 0, 2);
        grid.add(tf, 1, 2);

        grid.add(new Label("person:"), 0, 3);
        grid.add(personComboBox, 1, 3);

        grid.add(new Label("comment:"), 0, 4);
        grid.add(commentArea, 0, 5, 2, 4);

        this.getChildren().add(grid);
    }

    private void setSubject(Subject subject) {
        subjectComboBox.getSelectionModel().select(subject);
    }


    public Subject persist() {
        Subject s = subjectComboBox.getValue();
        if (s == null) {
            return null;
        }
        Housing h = s.getCurrentHousing();
        Date exportDate = DateTimeHelper.getDateTime(dp.getValue(), tf.getText());
        h.setEnd(exportDate);
        SubjectNote note = new SubjectNote("removed from stock", commentArea.getText(), exportDate, s);
        note.setPerson(personComboBox.getValue());
        Communicator.pushSaveOrUpdate(note);
        Communicator.pushSaveOrUpdate(h);
        storePreferences();
        return s;
    }


    private void applyPreferences() {
        if (!prefs.get("export_comment", "").isEmpty()) {
            commentArea.setText(prefs.get("export_comment",""));
        }
        if (!prefs.get("export_date", "").isEmpty()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                dp.setValue(DateTimeHelper.toLocalDate(dateFormat.parse(prefs.get("import_date", ""))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!prefs.get("export_time", "").isEmpty()) {
            DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            try {
                tf.setText(timeFormat.parse(prefs.get("export_time", "00:00:00")).toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!prefs.get("export_person", "").isEmpty()) {
            List<Person> persons = EntityHelper.getEntityList("from Person where id = " + prefs.get("export_person", ""),  Person.class);
            personComboBox.getSelectionModel().select(persons.get(0));
        }
    }

    private void storePreferences() {
        prefs.put("export_date", dp.getValue().toString());
        prefs.put("export_time", tf.getText());
        prefs.put("export_person", personComboBox.getValue().getId().toString());
        prefs.put("export_comment", commentArea.getText());
    }

    public boolean validate(Vector<String> messages) {
        boolean valid = true;
        if (subjectComboBox.getValue() == null) {
            messages.add("Subject entry must not be empty!");
            valid = false;
        } else if (subjectComboBox.getValue().getExitDate() != null){
            messages.add("Selected subject is no longer available!");
            valid = false;
        }
        if (subjectComboBox.getValue() != null && dp.getValue().isBefore(DateTimeHelper.toLocalDate(subjectComboBox.getValue().getImportDate()))) {
            messages.add("Export date is before import date of the selected subject!");
            valid = false;
        }
        return valid;
    }
}
