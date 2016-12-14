package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.Scene;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import java.sql.*;
import java.util.*;

/**
 * Created by huben on 14.12.16.
 */
public class ButtonService {
    ArrayList<Button> buttonList = new ArrayList<Button>();

    public void addButtons(GridPane grid, ArrayList<Button> buttons) {

        for (int i = 0; i < buttons.size(); i++) {
            grid.add(buttons.get(i), 0, i);
        }
    }

    public void add(Button new_button){
        buttonList.add(new_button);

    }

    public void initialize(){
        Button count_btn = new Button("Count fishes");
        add(count_btn);

        count_btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                GridPane root = builder.build();

                for (int i = 1; i <= "COUNT(DISTINCT species_id FROM census_subject"; i++ )

                tableFromDatabase(root,"first, last FROM census_person");
                buttonControl.addButtons(root, buttonControl.buttonList);
                primaryStage.setScene(new Scene(root, 640, 480));

            }
        });


    }

}
