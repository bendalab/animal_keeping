package animalkeeping.ui;

import animalkeeping.model.*;
import animalkeeping.util.DateTimeHelper;
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

    public LicenseForm() {
        this.setFillWidth(true);
        this.init();
    }

    public LicenseForm(License l) {
        this();
        this.license = l;
        this.init(l);
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

        Session session = Main.sessionFactory.openSession();
        List<Person> persons = new ArrayList<>(0);
        try {
            session.beginTransaction();
            persons = session.createQuery("from Person", Person.class).list();
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
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
        license.setName(nameField.getText());
        license.setAgency(agencyField.getText());
        license.setNumber(fileNoField.getText());
        license.setResponsiblePerson(respPersonCombo.getValue());
        license.setDeputy(deputyPersonCombo.getValue());
        license.setStartDate(sd);
        license.setEndDate(ed);

        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.saveOrUpdate(license);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException he) {
            showInfo(he.getLocalizedMessage());
            session.close();
        }
        return license;
    }

}
