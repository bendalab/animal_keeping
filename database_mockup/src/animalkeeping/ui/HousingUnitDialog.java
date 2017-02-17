package animalkeeping.ui;

import animalkeeping.model.HousingType;
import animalkeeping.model.HousingUnit;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class HousingUnitDialog extends VBox implements Initializable {
    private TextField nameField, dimensionsField;
    private Label idLabel;
    private TextArea descriptionArea;
    private ComboBox<HousingUnit> parentUnitComboBox;
    private ComboBox<HousingType> typeComboBox;
    private HousingUnit unit;


    public HousingUnitDialog() {
        super();
        this.setFillWidth(true);
        this.unit = new HousingUnit();
        init();
    }

    public HousingUnitDialog(HousingUnit unit) {
        this();
        setHousingUnit(unit);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
        Session session = Main.sessionFactory.openSession();
        List<HousingUnit> housingUnits = null;
        List<HousingType> housingTypes = null;
        try {
            session.beginTransaction();
            housingUnits = session.createQuery("from HousingUnit", HousingUnit.class).list();
            session.getTransaction().commit();
            session.beginTransaction();
            housingTypes = session.createQuery("from HousingType", HousingType.class).list();
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }
        typeComboBox.getItems().addAll(housingTypes);
        parentUnitComboBox.getItems().addAll(housingUnits);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5, 0, 5, 0));
        grid.add(new Label("ID"), 1, 1);
        grid.add(idLabel, 2, 1);

        grid.add(new Label("parent"), 1, 2);
        grid.add(parentUnitComboBox, 2, 2, 2,1);

        grid.add(new Label("name (*)"), 1, 3);
        grid.add(nameField, 2, 3, 2,1);

        grid.add(new Label("type (*)"), 1, 4);
        grid.add(typeComboBox, 2, 4, 2, 1);

        grid.add(new Label("dimensions"), 1, 5);
        grid.add(dimensionsField, 2, 5, 2, 1);

        grid.add(new Label("description"), 1, 6);
        grid.add(descriptionArea, 1, 7, 3, 3);

        Label l = new Label("(*) required");
        l.setFont(new Font(Font.getDefault().getFamily(), 10));
        grid.add(l, 1, 10);

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
}
