package animalkeeping.ui.forms;

import animalkeeping.logging.Communicator;
import animalkeeping.model.HousingType;
import animalkeeping.util.EntityHelper;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Vector;


public class HousingTypeForm extends VBox {
    private TextField nameField;
    private Label idLabel;
    private TextArea descriptionArea;
    private HousingType type;
    private CheckBox canHoldSubjects, canHaveSubunits;


    public HousingTypeForm() {
        super();
        this.setFillWidth(true);
        this.type = new HousingType();
        init();
    }


    public HousingTypeForm(HousingType type) {
        this();
        setHousingType(type);
    }


    private void init() {
        idLabel = new Label();
        nameField = new TextField();
        descriptionArea = new TextArea();
        descriptionArea.setWrapText(true);
        canHaveSubunits = new CheckBox("Can have sub units");
        canHaveSubunits.setTooltip(new Tooltip("Defines whether this housing unit can have child housing units"));
        canHoldSubjects = new CheckBox("Can hold subjects");
        canHoldSubjects.setTooltip(new Tooltip("Defines whether this housing unit is meant to house subjects directly (i.e. a rack may hold housing units but, on its own cannot hold subjects)"));

        GridPane grid = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints(100,100, Double.MAX_VALUE);
        column1.setHgrow(Priority.NEVER);
        ColumnConstraints column2 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
        column2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(column1, column2);

        idLabel.prefWidthProperty().bind(column2.maxWidthProperty());
        nameField.prefWidthProperty().bind(column2.maxWidthProperty());
        descriptionArea.prefWidthProperty().bind(column2.maxWidthProperty());
        canHaveSubunits.prefWidthProperty().bind(column2.maxWidthProperty());
        canHoldSubjects.prefWidthProperty().bind(column2.maxWidthProperty());

        grid.setPadding(new Insets(5, 0, 5, 0));
        grid.add(new Label("ID"), 0, 0);
        grid.add(idLabel, 1, 0);

        grid.add(new Label("name (*)"), 0, 1);
        grid.add(nameField, 1, 1, 2,1);

        grid.add(new Label(""), 0, 2);
        grid.add(canHoldSubjects, 1, 2, 2,1);

        grid.add(new Label(""), 0, 3);
        grid.add(canHaveSubunits, 1, 3, 2,1);

        grid.add(new Label("description"), 0, 4);
        grid.add(descriptionArea, 0, 5, 3, 3);

        grid.add(new Label("(*) required"), 0, 8);

        this.getChildren().add(grid);
    }


    public void setHousingType(HousingType type) {
        this.type = type != null ? type : new HousingType();
        this.nameField.setText(type != null ? type.getName() : "");
        this.idLabel.setText(type != null ? type.getId().toString() : "");
        this.descriptionArea.setText(type != null ? type.getDescription() : "");
        this.canHoldSubjects.setSelected(type != null ? type.getCanHoldSubjects() : true);
        this.canHaveSubunits.setSelected(type != null ? type.getCanHaveChildUnits() : true);
    }


    public HousingType getHousingType() {
        if (this.nameField.getText().isEmpty()) {
            return null;
        }
        type.setName(nameField.getText());
        type.setDescription(descriptionArea.getText());
        type.setCanHaveChildUnits(canHaveSubunits.isSelected());
        type.setCanHoldSubjects(canHoldSubjects.isSelected());
        return type;
    }

    public HousingType persist() {
        HousingType ht = getHousingType();
        Communicator.pushSaveOrUpdate(ht);
        return ht;
    }

    public boolean validate(Vector<String> messages) {
        boolean valid = true;
        if (nameField.getText().isEmpty()) {
            messages.add("Name must not be empty!");
            valid = false;
        }
        Vector<String> param = new Vector<>();
        param.add("name");
        Vector<Object> args = new Vector<>();
        args.add(nameField.getText());
        if (EntityHelper.getEntityList("From HousingType where name like :name", param, args, HousingType.class).size() > 0) {
            messages.add("Name of HousingType is already in use!");
            valid = false;
        }
        return valid;
    }
}
