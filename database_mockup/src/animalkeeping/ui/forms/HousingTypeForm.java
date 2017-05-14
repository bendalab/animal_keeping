package animalkeeping.ui.forms;

import animalkeeping.model.HousingType;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class HousingTypeForm extends VBox {
    private TextField nameField;
    private Label idLabel;
    private TextArea descriptionArea;
    private HousingType type;


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

        GridPane grid = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints(100,100, Double.MAX_VALUE);
        column1.setHgrow(Priority.NEVER);
        ColumnConstraints column2 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
        column2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(column1, column2);

        idLabel.prefWidthProperty().bind(column2.maxWidthProperty());
        nameField.prefWidthProperty().bind(column2.maxWidthProperty());
        descriptionArea.prefWidthProperty().bind(column2.maxWidthProperty());

        grid.setPadding(new Insets(5, 0, 5, 0));
        grid.add(new Label("ID"), 0, 0);
        grid.add(idLabel, 1, 0);

        grid.add(new Label("name (*)"), 0, 1);
        grid.add(nameField, 1, 1, 2,1);

        grid.add(new Label("description"), 0, 2);
        grid.add(descriptionArea, 0, 3, 3, 3);

        grid.add(new Label("(*) required"), 0, 6);

        this.getChildren().add(grid);
    }


    public void setHousingType(HousingType type) {
        this.type = type != null ? type : new HousingType();
        this.nameField.setText(type != null ? type.getName() : "");
        this.idLabel.setText(type != null ? type.getId().toString() : "");
        this.descriptionArea.setText(type != null ? type.getDescription() : "");
    }


    public HousingType getHousingType() {
        if (this.nameField.getText().isEmpty()) {
            return null;
        }
        type.setName(nameField.getText());
        type.setDescription(descriptionArea.getText());
        return type;
    }
}
