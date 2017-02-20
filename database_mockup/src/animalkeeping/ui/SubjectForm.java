package animalkeeping.ui;

import animalkeeping.model.*;
import animalkeeping.util.DateTimeHelper;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static animalkeeping.util.DateTimeHelper.getDateTime;
import static animalkeeping.util.Dialogs.showInfo;


public class SubjectForm extends VBox {
    private ComboBox<SpeciesType> speciesComboBox;
    private ComboBox<SubjectType> subjectTypeComboBox;
    private ComboBox<SupplierType> supplierComboBox;
    private ComboBox<HousingUnit> housingUnitComboBox;
    private DatePicker importDate;
    private TextField importTimeField, nameField, aliasField;
    private Label idLabel;
    private Subject subject;

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

        importDate = new DatePicker();
        importDate.setValue(LocalDate.now());
        importTimeField = new TextField(timeFormat.format(new Date()));

        aliasField = new TextField();
        nameField = new TextField();

        Button newSpeciesButton = new Button("+");
        newSpeciesButton.setTooltip(new Tooltip("create a new species entry"));
        newSpeciesButton.setDisable(true);
        Button newSupplierButton = new Button("+");
        newSupplierButton.setTooltip(new Tooltip("create a new supplier entry"));
        newSupplierButton.setDisable(true);
        Button newSubjectTypeButton = new Button("+");
        newSubjectTypeButton.setTooltip(new Tooltip("create a new subject type entry"));
        newSubjectTypeButton.setDisable(true);
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

        grid.setVgap(5);
        grid.setHgap(2);
        grid.add(new Label("ID:"), 0, 0);
        grid.add(idLabel, 1, 0);

        grid.add(new Label("species:"), 0, 1);
        grid.add(speciesComboBox, 1, 1, 1, 1);
        grid.add(newSpeciesButton, 2, 1, 1, 1);

        grid.add(new Label("subject type:"), 0, 2);
        grid.add(subjectTypeComboBox, 1, 2, 1, 1 );
        grid.add(newSubjectTypeButton, 2, 2, 1, 1);

        grid.add(new Label("supplier:"), 0, 3);
        grid.add(supplierComboBox, 1, 3, 1, 1 );
        grid.add(newSupplierButton, 2, 3, 1, 1);

        grid.add(new Label("housing unit:"), 0, 4);
        grid.add(housingUnitComboBox, 1, 4, 1, 1 );
        grid.add(newHousingUnitButton, 2, 4, 1, 1);

        grid.add(new Label("import date:"), 0, 5);
        grid.add(importDate, 1, 5, 2,1);

        grid.add(new Label("import time:"), 0, 6);
        grid.add(importTimeField, 1, 6, 2,1);

        grid.add(new Label("name:"), 0,7);
        grid.add(nameField, 1,7, 2, 1);

        grid.add(new Label("alias:"), 0, 8);
        grid.add(aliasField, 1, 8, 2, 1);

        //this.getChildren().add(new ScrollPane(housingTable));
        this.getChildren().add(grid);

        Session session = Main.sessionFactory.openSession();
        List<SpeciesType> species= new ArrayList<>(0);
        List<SubjectType> types = new ArrayList<>(0);
        List<SupplierType> supplier = new ArrayList<>(0);
        List<HousingUnit> housingUnits = new ArrayList<>(0);
        try {
            session.beginTransaction();
            species = session.createQuery("from SpeciesType", SpeciesType.class).list();
            session.getTransaction().commit();
            session.beginTransaction();
            types = session.createQuery("from SubjectType", SubjectType.class).list();
            session.getTransaction().commit();
            session.beginTransaction();
            supplier = session.createQuery("from SupplierType", SupplierType.class).list();
            session.getTransaction().commit();
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
        subjectTypeComboBox.getItems().addAll(types);
        supplierComboBox.getItems().addAll(supplier);
        speciesComboBox.getItems().addAll(species);
        housingUnitComboBox.getItems().addAll(housingUnits);
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

        Housing h = null;
        if (subject.getHousings().iterator().hasNext()) {
            h = subject.getHousings().iterator().next();
        } else {
            h = new Housing();
        }
        h.setStart(sd);
        h.setHousing(housingUnitComboBox.getValue());
        h.setSubject(subject);

        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.saveOrUpdate(subject);
            session.saveOrUpdate(h);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException he) {
            showInfo(he.getLocalizedMessage());
            session.close();
        }

        return subject;
    }

}
