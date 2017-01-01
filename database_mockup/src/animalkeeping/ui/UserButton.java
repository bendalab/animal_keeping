package animalkeeping.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.util.ArrayList;

/**
 * Created by huben on 14.12.16.
 */
public class UserButton extends Button {
    ConnectionManager connector = new ConnectionManager();

    UserButton(ButtonService parent, String text){
        super(text);
        super.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                parent.update_grid();
                try {
                    connector.linkTableFromDatabase(parent.grid, "id, first, last", "census_person", "TRUE", "id", PersonLink.class, parent);
                }
                catch (Exception ex) {
                    System.out.println( ex.getMessage() );
                }

            }
        });
    }

}
