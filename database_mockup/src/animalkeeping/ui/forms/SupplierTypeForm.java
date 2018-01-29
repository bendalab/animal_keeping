package animalkeeping.ui.forms;

import animalkeeping.logging.Communicator;
import animalkeeping.model.SupplierType;
import animalkeeping.model.TreatmentType;
import animalkeeping.util.EntityHelper;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.Vector;

import static animalkeeping.util.Dialogs.showInfo;


public class SupplierTypeForm extends VBox {
    private TextField nameField;
    private Label idLabel;
    private TextArea addressArea;
    private SupplierType supplierType = null;
    private boolean isEdit;

    public SupplierTypeForm() {
        this.setFillWidth(true);
        this.init();
        this.isEdit = false;
    }

    public SupplierTypeForm(SupplierType st) {
        this();
        this.supplierType = st;
        this.init(st);
        this.isEdit = (st != null) && (st.getId() !=null);
    }

    private  void init(SupplierType st) {
        if (st == null) {
            return;
        }
        idLabel.setText(st.getId().toString());
        nameField.setText(st.getName());
        addressArea.setText(st.getAddress());
    }

    private void init() {
        idLabel = new Label();
        nameField = new TextField();
        addressArea = new TextArea();

        GridPane grid = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints(100,100, Double.MAX_VALUE);
        column1.setHgrow(Priority.NEVER);
        ColumnConstraints column2 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
        column2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(column1, column2);
        nameField.prefWidthProperty().bind(column2.maxWidthProperty());
        addressArea.prefWidthProperty().bind(this.prefWidthProperty());

        grid.setVgap(5);
        grid.setHgap(2);
        grid.add(new Label("ID:"), 0, 0);
        grid.add(idLabel, 1, 0);

        grid.add(new Label("name:"), 0, 1);
        grid.add(nameField, 1, 1, 1, 1);

        grid.add(new Label("address:"), 0, 2);
        grid.add(addressArea, 0, 3, 2, 1 );
        this.getChildren().add(grid);
    }


    public SupplierType persistSupplierType() {
        if (supplierType == null) {
            supplierType = new SupplierType();
        }
        supplierType.setName(nameField.getText());
        supplierType.setAddress(addressArea.getText());

        if (Communicator.pushSaveOrUpdate(supplierType)) {
            return supplierType;
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
                if (EntityHelper.getEntityList("From SupplierType where name like :name", params, objects, SupplierType.class).size() > 0) {
                    messages.add("Name is already used! Select another name.");
                    valid = false;
                }
            }
        }
        return valid;
    }
}
