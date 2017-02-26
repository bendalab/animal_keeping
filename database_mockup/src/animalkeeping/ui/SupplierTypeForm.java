package animalkeeping.ui;

        import animalkeeping.model.SpeciesType;
        import animalkeeping.model.SupplierType;
        import javafx.scene.control.Label;
        import javafx.scene.control.TextArea;
        import javafx.scene.control.TextField;
        import javafx.scene.layout.ColumnConstraints;
        import javafx.scene.layout.GridPane;
        import javafx.scene.layout.Priority;
        import javafx.scene.layout.VBox;
        import org.hibernate.HibernateException;
        import org.hibernate.Session;

        import static animalkeeping.util.Dialogs.showInfo;


public class SupplierTypeForm extends VBox {
    private TextField nameField;
    private Label idLabel;
    private TextArea addressArea;
    private SupplierType supplierType = null;


    public SupplierTypeForm() {
        this.setFillWidth(true);
        this.init();
    }

    public SupplierTypeForm(SupplierType st) {
        this();
        this.supplierType = st;
        this.init(st);
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

        grid.add(new Label("description:"), 0, 2);
        grid.add(addressArea, 0, 3, 2, 1 );
        this.getChildren().add(grid);
    }


    public SupplierType persistSupplierType() {
        if (supplierType == null) {
            supplierType = new SupplierType();
        }
        supplierType.setName(nameField.getText());
        supplierType.setAddress(addressArea.getText());

        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.saveOrUpdate(supplierType);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException he) {
            showInfo(he.getLocalizedMessage());
            session.close();
        }
        return supplierType;
    }
}
