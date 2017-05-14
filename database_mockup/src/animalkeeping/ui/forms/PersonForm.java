package animalkeeping.ui.forms;

import animalkeeping.logging.Communicator;
import animalkeeping.model.Person;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Created by jan on 02.03.17.
 */
public class PersonForm extends VBox {
    private TextField firstnameField, lastnameField, emailField;
    private Label idLabel;
    private Person person = null;

    public PersonForm() {
        this.setFillWidth(true);
        this.init();
    }

    public PersonForm(Person p) {
        this();
        this.person = p;
        this.init(p);
    }

    private  void init(Person p) {
        if (p == null) {
            return;
        }
        idLabel.setText(p.getId().toString());
        firstnameField.setText(p.getFirstName());
        lastnameField.setText(p.getLastName());
        emailField.setText(p.getEmail());
    }

    private void init() {
        idLabel = new Label();
        firstnameField = new TextField();
        emailField = new TextField();
        lastnameField = new TextField();

        GridPane grid = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints(100,100, Double.MAX_VALUE);
        column1.setHgrow(Priority.NEVER);
        ColumnConstraints column2 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
        column2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(column1, column2);

        firstnameField.prefWidthProperty().bind(column2.maxWidthProperty());
        lastnameField.prefWidthProperty().bind(column2.maxWidthProperty());
        emailField.prefWidthProperty().bind(column2.maxWidthProperty());

        grid.setVgap(5);
        grid.setHgap(2);
        grid.add(new Label("ID:"), 0, 0);
        grid.add(idLabel, 1, 0);

        grid.add(new Label("First Name:"), 0, 1);
        grid.add(firstnameField, 1, 1, 1,1);

        grid.add(new Label("Last Name:"), 0,2);
        grid.add(lastnameField, 1,2, 1, 1);

        grid.add(new Label("E-mail:"), 0, 3);
        grid.add(emailField, 1, 3, 1, 1);

        this.getChildren().add(grid);
    }


    public Person persistPerson() {
        if (person == null) {
            person = new Person();
        }
        person.setLastName(lastnameField.getText());
        person.setEmail(emailField.getText());
        person.setFirstName(firstnameField.getText());

        if (Communicator.pushSaveOrUpdate(person))
            return person;
        return null;
    }
}
