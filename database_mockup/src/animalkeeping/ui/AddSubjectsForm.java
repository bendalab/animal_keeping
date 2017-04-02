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
import java.util.HashSet;
import java.util.List;

import static animalkeeping.util.Dialogs.editHousingUnitDialog;


public class AddSubjectsForm extends VBox {
    private ComboBox<HousingUnit> housingUnitComboBox;
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
        housingDate = new DatePicker(LocalDate.now());
        startIdSpinner = new Spinner<>(0, 9999, 0);
        countSpinner = new Spinner<>(0, 9999, 10);
        timeField = new SpecialTextField("##:##:##");
        timeField.setTooltip(new Tooltip("Import time use HH:mm:ss format"));

        Button newHousingUnit = new Button("+");
        newHousingUnit.setTooltip(new Tooltip("create a new housing unit"));
        newHousingUnit.setOnAction(event -> createNewHousingButton());

        Button newSpecies = new Button("+");
        newSpecies.setTooltip(new Tooltip("create a new species entry"));
        newSpecies.setDisable(true);

        Button newSupplier = new Button("+");
        newSupplier.setTooltip(new Tooltip("create a new supplier entry"));
        newSupplier.setDisable(true);

        Session session = Main.sessionFactory.openSession();
        List<HousingUnit> housingUnits = new ArrayList<>(0);
        List<SupplierType> supplier = new ArrayList<>(0);
        List<SpeciesType> species = new ArrayList<>(0);
        try {
            session.beginTransaction();
            housingUnits = session.createQuery("from HousingUnit", HousingUnit.class).list();
            session.getTransaction().commit();
            session.beginTransaction();
            supplier = session.createQuery("from SupplierType", SupplierType.class).list();
            session.getTransaction().commit();
            session.beginTransaction();
            species = session.createQuery("from SpeciesType", SpeciesType.class).list();
            session.getTransaction().commit();
            session.beginTransaction();
            List<SubjectType> subjectTypes = session.createQuery("from SubjectType where name = 'animal'",
                                                                SubjectType.class).list();
            st = subjectTypes.get(0);
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
        supplierComboBox.getItems().addAll(supplier);
        speciesComboBox.getItems().addAll(species);
        nameField = new TextField();

        Label heading = new Label("Add subjects:");
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
        speciesComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
        supplierComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
        housingUnitComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
        timeField.prefWidthProperty().bind(column2.maxWidthProperty());
        housingDate.prefWidthProperty().bind(column2.maxWidthProperty());
        startIdSpinner.prefWidthProperty().bind(column2.maxWidthProperty());
        countSpinner.prefWidthProperty().bind(column2.maxWidthProperty());

        grid.setVgap(5);
        grid.setHgap(2);
        grid.add(new Label("name pattern(*):"), 0, 0);
        grid.add(nameField, 1, 0, 1, 1);

        grid.add(new Label("housing unit(*):"), 0, 1);
        grid.add(housingUnitComboBox, 1, 1, 1, 1 );
        grid.add(newHousingUnit, 2, 1, 1, 1);

        grid.add(new Label("supplier(*):"), 0, 2);
        grid.add(supplierComboBox, 1, 2, 1,1);
        grid.add(newSupplier, 2, 2, 1, 1);

        grid.add(new Label("species(*):"), 0,3);
        grid.add(speciesComboBox, 1,3, 1, 1);
        grid.add(newSpecies, 2, 3, 1, 1);


        grid.add(new Label("import date(*):"), 0, 5);
        grid.add(housingDate, 1, 5, 2, 1);

        grid.add(new Label("time(*):"), 0, 6);
        grid.add(timeField, 1, 6, 1, 1);
        timeField.setText(dateFormat.format(date));

        grid.add(new Label("subject count"), 0, 7);
        grid.add(countSpinner, 1, 7, 1, 1);

        grid.add(new Label("start index:"), 0, 8);
        grid.add(startIdSpinner, 1, 8, 1, 1);

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


    public Boolean persistSubjects() {
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
                return false;
            }
        }
        session.close();
        return true;
    }


    private void fillHousingUnitsCombo() { //TODO maybe move all such methods to yet another util class?
        List<HousingUnit> housingUnits = new ArrayList<>(0);
        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            housingUnits = session.createQuery("from HousingUnit", HousingUnit.class).list();
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }
        this.housingUnitComboBox.getItems().clear();
        this.housingUnitComboBox.getItems().addAll(housingUnits);
    }


    private void createNewHousingButton() {
        HousingUnit u = editHousingUnitDialog(null, this.housingUnitComboBox.getValue());
        if (u != null) {
            fillHousingUnitsCombo();
            this.housingUnitComboBox.getSelectionModel().select(u);
        }
    }

}
