package animalkeeping.ui;

import animalkeeping.model.License;
import animalkeeping.model.TreatmentType;
import animalkeeping.util.EntityHelper;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.List;

import static animalkeeping.util.Dialogs.showInfo;

/**
 * Created by jan on 04.03.17.
 */
public class TreatmentTypeForm extends VBox {
    private ComboBox<License> licenseComboBox;
    private TextField nameField;
    private TextArea descriptionArea;
    private CheckBox invasiveBox;
    private TreatmentType type;
    private Label idLabel;

    public TreatmentTypeForm() {
        super();
        this.setFillWidth(true);
        this.init();
    }

    public TreatmentTypeForm(TreatmentType t) {
        this();
        this.init(t);
        this.type = t;
    }


    private void init(TreatmentType t) {
        idLabel.setText(t != null ? t.getId().toString() : "");
        nameField.setText(t!= null ? t.getName() : "");
        descriptionArea.setText(t != null ? t.getDescription() : "");
        invasiveBox.setSelected(t != null ? t.isInvasive() : false);
        licenseComboBox.getSelectionModel().select(t != null ? t.getLicense() : null);
    }


    private void init() {
        idLabel = new Label();
        nameField = new TextField();
        invasiveBox = new CheckBox("is invasive/final");
        invasiveBox.setSelected(false);
        descriptionArea = new TextArea();

        licenseComboBox = new ComboBox<>();
        licenseComboBox.setConverter(new StringConverter<License>() {
            @Override
            public String toString(License object) {
                return object.getName();
            }

            @Override
            public License fromString(String string) {
                return null;
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
        descriptionArea.prefWidthProperty().bind(column2.maxWidthProperty());

        grid.setVgap(5);
        grid.setHgap(2);
        grid.add(new Label("ID:"), 0, 0);
        grid.add(idLabel, 1, 0);

        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1, 1, 1);

        grid.add(invasiveBox, 1, 2, 1, 1 );

        grid.add(new Label("License:"), 0, 3);
        grid.add(licenseComboBox, 1, 3, 2,1);

        grid.add(new Label("Description:"), 0,4);
        grid.add(descriptionArea, 0,5, 2, 2);

        this.getChildren().add(grid);

        List<License> licenses = EntityHelper.getEntityList("from License", License.class);
        licenseComboBox.getItems().addAll(licenses);
    }

    public TreatmentType persistType() {
        if (type == null) {
            type = new TreatmentType();
        }
        type.setName(nameField.getText());
        type.setInvasive(invasiveBox.isSelected());
        type.setDescription(descriptionArea.getText());
        type.setLicense(licenseComboBox.getValue());

        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.saveOrUpdate(type);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException he) {
            showInfo(he.getLocalizedMessage());
        }
        return type;
    }
}


