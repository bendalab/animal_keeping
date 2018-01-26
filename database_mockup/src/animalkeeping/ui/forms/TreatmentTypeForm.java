package animalkeeping.ui.forms;

import animalkeeping.logging.Communicator;
import animalkeeping.model.License;
import animalkeeping.model.TreatmentTarget;
import animalkeeping.model.TreatmentType;
import animalkeeping.util.EntityHelper;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by jan on 04.03.17.
 */
public class TreatmentTypeForm extends VBox {
    private ComboBox<License> licenseComboBox;
    private ComboBox<TreatmentTarget> targetComboBox;
    private TextField nameField;
    private TextArea descriptionArea;
    private CheckBox invasiveBox;
    private CheckBox isFinalBox;
    private TreatmentType type;
    private Label idLabel;
    private boolean isEdit;

    public TreatmentTypeForm() {
        super();
        this.setFillWidth(true);
        this.init();
        this.isEdit = false;
    }

    public TreatmentTypeForm(TreatmentType t) {
        this();
        this.init(t);
        this.type = t;
        this.isEdit = t != null;
    }


    private void init(TreatmentType t) {
        idLabel.setText(t != null ? t.getId().toString() : "");
        nameField.setText(t!= null ? t.getName() : "");
        descriptionArea.setText(t != null ? t.getDescription() : "");
        invasiveBox.setSelected(t != null ? t.isInvasive() : false);
        isFinalBox.setSelected(t != null ? t.isFinalExperiment() : false);
        licenseComboBox.getSelectionModel().select(t != null ? t.getLicense() : null);
        targetComboBox.getSelectionModel().select(t != null ? t.getTarget() : null);
    }


    private void init() {
        idLabel = new Label();
        nameField = new TextField();
        invasiveBox = new CheckBox("is invasive");
        isFinalBox = new CheckBox("experiment is final");
        invasiveBox.setSelected(false);
        isFinalBox.setSelected(false);
        descriptionArea = new TextArea();

        licenseComboBox = new ComboBox<>();
        licenseComboBox.setConverter(new StringConverter<License>() {
            @Override
            public String toString(License object) {
                if (object.getId() == null)
                    return "None";
                else
                    return object.getName();
            }

            @Override
            public License fromString(String string) {
                return null;
            }
        });

        targetComboBox = new ComboBox<>();
        targetComboBox.setConverter(new StringConverter<TreatmentTarget>() {
            @Override
            public String toString(TreatmentTarget object) {
                return object.toString();
            }

            @Override
            public TreatmentTarget fromString(String string) {
                return string.equals("subject") ? TreatmentTarget.subject : TreatmentTarget.housing;
            }
        });

        GridPane grid = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints(100,100, Double.MAX_VALUE);
        column1.setHgrow(Priority.NEVER);
        ColumnConstraints column2 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
        column2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(column1, column2);

        licenseComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
        nameField.prefWidthProperty().bind(column2.maxWidthProperty());
        invasiveBox.prefWidthProperty().bind(column2.maxWidthProperty());
        isFinalBox.prefWidthProperty().bind(column2.maxWidthProperty());
        descriptionArea.prefWidthProperty().bind(column2.maxWidthProperty());

        grid.setVgap(5);
        grid.setHgap(2);
        grid.add(new Label("ID:"), 0, 0);
        grid.add(idLabel, 1, 0);

        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1, 1, 1);

        grid.add(invasiveBox, 1, 2, 1, 1 );
        grid.add(isFinalBox, 1, 3, 1, 1 );

        grid.add(new Label("Traget:"), 0, 4);
        grid.add(targetComboBox, 1, 4, 2,1);

        grid.add(new Label("License:"), 0, 5);
        grid.add(licenseComboBox, 1, 5, 2,1);

        grid.add(new Label("Description:"), 0,6);
        grid.add(descriptionArea, 0,7, 2, 2);

        this.getChildren().add(grid);

        List<License> licenses = EntityHelper.getEntityList("from License order by name asc", License.class);
        License l = new License();
        licenses.add(l);
        licenseComboBox.getItems().addAll(licenses);

        List<TreatmentTarget> targets = new ArrayList<>(2);
        targets.add(TreatmentTarget.subject);
        targets.add(TreatmentTarget.housing);
        targetComboBox.getItems().addAll(targets);
        targetComboBox.getSelectionModel().select(TreatmentTarget.subject);
    }

    public TreatmentType persistType() {
        if (type == null) {
            type = new TreatmentType();
        }
        type.setName(nameField.getText());
        type.setInvasive(invasiveBox.isSelected());
        type.setFinalExperiment(isFinalBox.isSelected());
        type.setTarget(targetComboBox.getValue());
        type.setDescription(descriptionArea.getText());
        type.setLicense((licenseComboBox.getValue()!= null && licenseComboBox.getValue().getId() != null) ?
                licenseComboBox.getValue() : null);

        if (Communicator.pushSaveOrUpdate(type)) {
            return type;
        }
        return null;
    }

    public boolean validate(Vector<String> messages) {
        boolean valid = true;
        if (nameField.getText().isEmpty()) {
            messages.add("Name must not be empty!");
            valid = false;
        } else {
            if (!isEdit) {
                Vector<String> params = new Vector<>();
                params.add("name");
                Vector<Object> objects = new Vector<>();
                objects.add(nameField.getText());
                if (EntityHelper.getEntityList("From TreatmentType where name like :name", params, objects, TreatmentType.class).size() > 0) {
                    messages.add("Name is already used! Select another name.");
                    valid = false;
                }
            }
        }
        if (targetComboBox.getValue() == null) {
            messages.add("Treatment target must not be empty!");
            valid = false;
        }
        return valid;
    }
}


