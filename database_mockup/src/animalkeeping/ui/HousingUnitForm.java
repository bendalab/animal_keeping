package animalkeeping.ui;

import animalkeeping.logging.Communicator;
import animalkeeping.model.HousingType;
import animalkeeping.model.HousingUnit;
import animalkeeping.util.EntityHelper;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.List;


public class HousingUnitForm extends VBox {
    private TextField nameField, dimensionsField;
    private Label idLabel;
    private TextArea descriptionArea;
    private ComboBox<HousingUnit> parentUnitComboBox;
    private ComboBox<HousingType> typeComboBox;
    private HousingUnit unit;


    public HousingUnitForm() {
        super();
        this.setFillWidth(true);
        this.unit = new HousingUnit();
        init();
    }

    public HousingUnitForm(HousingUnit unit) {
        this();
        setHousingUnit(unit);
    }

    private void init() {
        idLabel = new Label();
        nameField = new TextField();
        dimensionsField = new TextField();
        descriptionArea = new TextArea();
        descriptionArea.setWrapText(true);
        parentUnitComboBox = new ComboBox<>();
        parentUnitComboBox.setConverter(new StringConverter<HousingUnit>() {
            @Override
            public String toString(HousingUnit object) {
                return object.getName();
            }

            @Override
            public HousingUnit fromString(String string) {
                return null;
            }
        });
        parentUnitComboBox.setCellFactory(new Callback<ListView<HousingUnit>, ListCell<HousingUnit>>() {
            @Override
            public ListCell<HousingUnit> call(ListView<HousingUnit> p) {
                final ListCell<HousingUnit> cell = new ListCell<HousingUnit>() {
                    @Override
                    protected void updateItem(HousingUnit t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.getName());
                        } else {
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        });

        typeComboBox = new ComboBox<>();
        typeComboBox.setCellFactory(new Callback<ListView<HousingType>, ListCell<HousingType>>() {
            @Override
            public ListCell<HousingType> call(ListView<HousingType> param) {
                final ListCell<HousingType> cell = new ListCell<HousingType>() {
                    @Override
                    protected void updateItem(HousingType t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.getName());
                        } else {
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        });
        typeComboBox.setConverter(new StringConverter<HousingType>() {
            @Override
            public String toString(HousingType object) {
                return object.getName();
            }

            @Override
            public HousingType fromString(String string) {
                return null;
            }
        });
        List<HousingUnit> housingUnits = EntityHelper.getEntityList("from HousingUnit", HousingUnit.class);
        List<HousingType> housingTypes = EntityHelper.getEntityList("from HousingType", HousingType.class);
        typeComboBox.getItems().addAll(housingTypes);
        parentUnitComboBox.getItems().addAll(housingUnits);

        GridPane grid = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints(100,100, Double.MAX_VALUE);
        column1.setHgrow(Priority.NEVER);
        ColumnConstraints column2 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
        column2.setHgrow(Priority.ALWAYS);
        ColumnConstraints column3 = new ColumnConstraints(30, 30, Double.MAX_VALUE);
        column3.setHgrow(Priority.NEVER);
        grid.getColumnConstraints().addAll(column1, column2, column3);

        grid.setPadding(new Insets(5, 0, 5, 0));
        grid.add(new Label("ID"), 0, 0);
        grid.add(idLabel, 1, 0);

        grid.add(new Label("parent"), 0, 1);
        grid.add(parentUnitComboBox, 1, 1, 2,1);

        grid.add(new Label("name (*)"), 0, 2);
        grid.add(nameField, 1, 2, 2,1);

        grid.add(new Label("type (*)"), 0, 3);
        grid.add(typeComboBox, 1, 3, 2, 1);

        grid.add(new Label("dimensions"), 0, 4);
        grid.add(dimensionsField, 1, 4, 2, 1);

        grid.add(new Label("description"), 0, 5);
        grid.add(descriptionArea, 0, 6, 3, 3);

        idLabel.prefWidthProperty().bind(column2.maxWidthProperty());
        parentUnitComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
        nameField.prefWidthProperty().bind(column2.maxWidthProperty());
        typeComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
        dimensionsField.prefWidthProperty().bind(column2.maxWidthProperty());

        Label l = new Label("(*) required");
        l.setFont(new Font(Font.getDefault().getFamily(), 10));
        grid.add(l, 0, 9);
        this.getChildren().add(grid);
    }


    public void setHousingUnit(HousingUnit unit) {
        this.unit = unit != null ? unit : new HousingUnit();
        this.nameField.setText(unit != null ? unit.getName() : "");
        this.idLabel.setText(unit != null ? unit.getId().toString() : "");
        this.descriptionArea.setText(unit != null ? unit.getDescription() : "");
        this.dimensionsField.setText(unit != null ? unit.getDimensions() : "");
        this.parentUnitComboBox.getSelectionModel().select(unit != null ? unit.getParentUnit() : null);
        this.typeComboBox.getSelectionModel().select(unit != null ? unit.getHousingType() : null);
    }


    public void setParentUnit(HousingUnit parent) {
        this.parentUnitComboBox.getSelectionModel().select(parent);
    }


    public HousingUnit getHousingUnit() {
        if (nameField.getText().isEmpty() || typeComboBox.getSelectionModel().isEmpty()) {
            return null;
        }
        unit.setName(nameField.getText());
        unit.setDescription(descriptionArea.getText());
        unit.setDimensions(dimensionsField.getText());
        unit.setHousingType(typeComboBox.getSelectionModel().getSelectedItem());
        unit.setParentUnit(parentUnitComboBox.getSelectionModel().getSelectedItem());
        return unit;
    }

    public HousingUnit persistHousingUnit() {
        HousingUnit unit = getHousingUnit();
        Communicator.pushSaveOrUpdate(unit);
        return unit;
    }
}
