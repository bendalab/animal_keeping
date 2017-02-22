package animalkeeping.ui;

import animalkeeping.model.License;
import animalkeeping.model.Quota;
import animalkeeping.model.SpeciesType;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

import static animalkeeping.util.Dialogs.showInfo;

public class QuotaForm extends VBox {
    private ComboBox<License> licenseCombo;
    private ComboBox<SpeciesType> speciesCombo;
    private TextField numberField;
    private Label idLabel;
    private Quota quota;

    public QuotaForm() {
        this.setFillWidth(true);
        this.init();
    }

    public QuotaForm(Quota q) {
        this();
        this.quota = q;
        this.init(q);
    }

    public QuotaForm(License l) {
        this();
        init();
        licenseCombo.getSelectionModel().select(l);
    }

    private  void init(Quota q) {
        if (q == null) {
            return;
        }
        idLabel.setText(q.getId().toString());
        licenseCombo.getSelectionModel().select(q.getLicense());
        speciesCombo.getSelectionModel().select(q.getSpeciesType());
        numberField.setText(q.getNumber().toString());
    }

    private void init() {
        idLabel = new Label();
        licenseCombo = new ComboBox<>();
        licenseCombo.setConverter(new StringConverter<License>() {
            @Override
            public String toString(License object) {
                return object.getName();
            }

            @Override
            public License fromString(String string) {
                return null;
            }
        });
        speciesCombo = new ComboBox<>();
        speciesCombo.setConverter(new StringConverter<SpeciesType>() {
            @Override
            public String toString(SpeciesType object) {
                return object.getName();
            }

            @Override
            public SpeciesType fromString(String string) {
                return null;
            }
        });
        numberField = new TextField();

        Button newSpeciesTypeButton = new Button("+");
        newSpeciesTypeButton.setTooltip(new Tooltip("create a new species entry"));
        newSpeciesTypeButton.setDisable(true);

        GridPane grid = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints(100,100, Double.MAX_VALUE);
        column1.setHgrow(Priority.NEVER);
        ColumnConstraints column2 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
        column2.setHgrow(Priority.ALWAYS);
        ColumnConstraints column3 = new ColumnConstraints(30, 30, Double.MAX_VALUE);
        column3.setHgrow(Priority.NEVER);
        grid.getColumnConstraints().addAll(column1, column2, column3);
        licenseCombo.prefWidthProperty().bind(column2.maxWidthProperty());
        speciesCombo.prefWidthProperty().bind(column2.maxWidthProperty());
        numberField.prefWidthProperty().bind(column2.maxWidthProperty());

        grid.setVgap(5);
        grid.setHgap(2);
        grid.add(new Label("ID:"), 0, 0);
        grid.add(idLabel, 1, 0);

        grid.add(new Label("license:"), 0, 1);
        grid.add(licenseCombo, 1, 1, 2, 1 );

        grid.add(new Label("species:"), 0, 2);
        grid.add(speciesCombo, 1, 2, 1, 1 );
        grid.add(newSpeciesTypeButton, 2, 2, 1, 1);

        grid.add(new Label("count:"), 0,3);
        numberField.setTooltip(new Tooltip("Maximum number of granted subjects."));
        grid.add(numberField, 1,3, 2, 1);

        this.getChildren().add(grid);

        Session session = Main.sessionFactory.openSession();
        List<SpeciesType> species = new ArrayList<>(0);
        List<License> licenses = new ArrayList<>(0);
        try {
            session.beginTransaction();
            species = session.createQuery("from SpeciesType", SpeciesType.class).list();
            session.getTransaction().commit();
            session.beginTransaction();
            licenses = session.createQuery("from License", License.class).list();
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }
        speciesCombo.getItems().addAll(species);
        licenseCombo.getItems().addAll(licenses);
    }


    public Quota persistQuota() {
        if (quota== null) {
            quota = new Quota();
        }
        quota.setNumber(Long.valueOf(numberField.getText()));
        quota.setLicense(licenseCombo.getValue());
        quota.setSpeciesType(speciesCombo.getValue());

        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.saveOrUpdate(quota);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException he) {
            showInfo(he.getLocalizedMessage());
            session.close();
        }
        return quota;
    }

}
