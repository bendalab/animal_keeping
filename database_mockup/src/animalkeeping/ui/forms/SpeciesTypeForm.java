package animalkeeping.ui.forms;

import animalkeeping.logging.Communicator;
import animalkeeping.model.SpeciesType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import static animalkeeping.util.Dialogs.showInfo;


public class SpeciesTypeForm extends VBox {
    private TextField nameField;
    private Label idLabel;
    private TextField trivialField;
    private SpeciesType speciesType = null;


    public SpeciesTypeForm() {
        this.setFillWidth(true);
        this.init();
    }

    public SpeciesTypeForm(SpeciesType st) {
        this();
        this.speciesType = st;
        this.init(st);
    }

    private  void init(SpeciesType st) {
        if (st == null) {
            return;
        }
        idLabel.setText(st.getId().toString());
        nameField.setText(st.getName());
        trivialField.setText(st.getTrivial());
    }

    private void init() {
        idLabel = new Label();
        nameField = new TextField();
        trivialField = new TextField();

        GridPane grid = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints(100,100, Double.MAX_VALUE);
        column1.setHgrow(Priority.NEVER);
        ColumnConstraints column2 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
        column2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(column1, column2);
        nameField.prefWidthProperty().bind(column2.maxWidthProperty());
        trivialField.prefWidthProperty().bind(column2.prefWidthProperty());

        grid.setVgap(5);
        grid.setHgap(2);
        grid.add(new Label("ID:"), 0, 0);
        grid.add(idLabel, 1, 0);

        grid.add(new Label("species name:"), 0, 1);
        grid.add(nameField, 1, 1, 1, 1);

        grid.add(new Label("trivial name:"), 0, 2);
        grid.add(trivialField, 1, 2, 1, 1 );
        this.getChildren().add(grid);
    }


    public SpeciesType persistSpeciesType() {
        if (speciesType == null) {
            speciesType = new SpeciesType();
        }
        speciesType.setName(nameField.getText());
        speciesType.setTrivial(trivialField.getText());

        if (Communicator.pushSaveOrUpdate(speciesType)) {
            return speciesType;
        }
        return null;
    }
}
