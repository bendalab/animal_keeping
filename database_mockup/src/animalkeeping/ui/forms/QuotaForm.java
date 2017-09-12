package animalkeeping.ui.forms;

import animalkeeping.logging.Communicator;
import animalkeeping.model.Gender;
import animalkeeping.model.License;
import animalkeeping.model.Quota;
import animalkeeping.model.SpeciesType;
import animalkeeping.ui.Main;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

import static animalkeeping.util.Dialogs.showInfo;

public class QuotaForm extends VBox {
    private ComboBox<License> licenseCombo;
    private ComboBox<SpeciesType> speciesCombo;
    private ComboBox<Gender> genderComboBox;
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
        licenseCombo.getSelectionModel().select(l);
    }

    private  void init(Quota q) {
        if (q == null) {
            return;
        }
        idLabel.setText(q.getId().toString());
        licenseCombo.getSelectionModel().select(q.getLicense());
        speciesCombo.getSelectionModel().select(q.getSpeciesType());
        genderComboBox.getSelectionModel().select(q.getGender());
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
        numberField = new TextField();

        Button newSpeciesTypeButton = new Button("+");
        newSpeciesTypeButton.setTooltip(new Tooltip("create a new species entry"));
        newSpeciesTypeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SpeciesType st = Dialogs.editSpeciesTypeDialog(null);
                if (st != null) {
                    List<SpeciesType> species = EntityHelper.getEntityList("From SpeciesType", SpeciesType.class);
                    speciesCombo.getItems().clear();
                    speciesCombo.getItems().addAll(species);
                    speciesCombo.getSelectionModel().select(st);
                }
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
        licenseCombo.prefWidthProperty().bind(column2.maxWidthProperty());
        speciesCombo.prefWidthProperty().bind(column2.maxWidthProperty());
        numberField.prefWidthProperty().bind(column2.maxWidthProperty());
        genderComboBox.prefWidthProperty().bind(column2.maxWidthProperty());

        grid.setVgap(5);
        grid.setHgap(2);
        grid.add(new Label("ID:"), 0, 0);
        grid.add(idLabel, 1, 0);

        grid.add(new Label("license:"), 0, 1);
        grid.add(licenseCombo, 1, 1, 2, 1 );

        grid.add(new Label("species:"), 0, 2);
        grid.add(speciesCombo, 1, 2, 1, 1 );
        grid.add(newSpeciesTypeButton, 2, 2, 1, 1);

        grid.add(new Label("gender:"), 0, 3);
        grid.add(genderComboBox, 1, 3, 2, 1 );

        grid.add(new Label("count:"), 0,4);
        numberField.setTooltip(new Tooltip("Maximum number of granted subjects."));
        grid.add(numberField, 1,4, 2, 1);

        this.getChildren().add(grid);

        List<SpeciesType> species = EntityHelper.getEntityList("From SpeciesType", SpeciesType.class);

        List<License> licenses;
        if (Main.getSettings().getBoolean("app_settings_validLicensesSelection", true)) {
            licenses = EntityHelper.getEntityList("from License where end_date > CURDATE()", License.class);
        } else {
            licenses = EntityHelper.getEntityList("from License", License.class);
        }

        speciesCombo.getItems().addAll(species);
        licenseCombo.getItems().addAll(licenses);
        List<Gender> sexes = new ArrayList<>(4);
        sexes.add(Gender.unknown);
        sexes.add(Gender.female);
        sexes.add(Gender.male);
        sexes.add(Gender.hermaphrodite);
        genderComboBox.getItems().addAll(sexes);
        genderComboBox.getSelectionModel().select(Gender.unknown);
    }


    public Quota persistQuota() {
        if (quota == null) {
            quota = new Quota();
        }
        quota.setNumber(numberField.getText().isEmpty() ? 0 : Long.valueOf(numberField.getText()));
        quota.setLicense(licenseCombo.getValue());
        quota.setGender(genderComboBox.getValue());
        quota.setSpeciesType(speciesCombo.getValue());
        System.out.println(quota);
        if (!Communicator.pushSaveOrUpdate(quota)) {
            showInfo("Error: Quota entry could not be persisted! Missing required information?");
            return null;
        }
        return quota;
    }
}
