package animalkeeping.ui;

import animalkeeping.logging.Communicator;
import animalkeeping.model.SubjectType;
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


public class SubjectTypeForm extends VBox {
    private TextField nameField;
    private Label idLabel;
    private TextArea descriptionArea;
    private SubjectType subjectType = null;


    public SubjectTypeForm() {
        this.setFillWidth(true);
        this.init();
    }

    public SubjectTypeForm(SubjectType st) {
        this();
        this.subjectType = st;
        this.init(st);
    }

    private  void init(SubjectType st) {
        if (st == null) {
            return;
        }
        idLabel.setText(st.getId().toString());
        nameField.setText(st.getName());
        descriptionArea.setText(st.getDescription());
    }

    private void init() {
        idLabel = new Label();
        nameField = new TextField();
        descriptionArea = new TextArea();

        GridPane grid = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints(100,100, Double.MAX_VALUE);
        column1.setHgrow(Priority.NEVER);
        ColumnConstraints column2 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
        column2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(column1, column2);
        nameField.prefWidthProperty().bind(column2.maxWidthProperty());
        descriptionArea.prefWidthProperty().bind(this.prefWidthProperty());

        grid.setVgap(5);
        grid.setHgap(2);
        grid.add(new Label("ID:"), 0, 0);
        grid.add(idLabel, 1, 0);

        grid.add(new Label("name:"), 0, 1);
        grid.add(nameField, 1, 1, 1, 1);

        grid.add(new Label("description:"), 0, 2);
        grid.add(descriptionArea, 0, 3, 2, 1 );
        this.getChildren().add(grid);
       }


    public SubjectType persistSubjectType() {
        if (subjectType == null) {
            subjectType = new SubjectType();
        }
        subjectType.setName(nameField.getText());
        subjectType.setDescription(descriptionArea.getText());

        if (nameField.getText() == null){
            System.out.println("Please enter a name.");
            return null;
        }

        Communicator.pushSaveOrUpdate(subjectType);


        return subjectType;
    }

}
