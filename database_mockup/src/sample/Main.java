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

public class Main extends Application {
    ButtonService buttonControl = new ButtonService();






        @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Database contents");
        ArrayList<Button> buttonList = new ArrayList<>();

        GridPaneBuilder builder = GridPaneBuilder.create();
        builder.hgap(10);
        builder.vgap(5);
        GridPane root = builder.build();

        Button usr_btn = new Button("Show User Information");
        buttonControl.add(usr_btn);

        usr_btn.setOnAction(new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent e) {
            GridPane root = builder.build();
            tableFromDatabase(root,"first, last FROM census_person");
            buttonControl.addButtons(root, buttonControl.buttonList);
            primaryStage.setScene(new Scene(root, 640, 480));

        }
            });

        Button fish_btn = new Button("Show Fish Information");
        buttonControl.add(fish_btn);

        fish_btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {

                GridPane root = builder.build();
                buttonControl.addButtons(root, buttonControl.buttonList);
                tableFromDatabase(root,"name, species_id, source_id FROM census_subject");
                primaryStage.setScene(new Scene(root, 640, 480));

            }
        });
        buttonControl.addButtons(root, buttonControl.buttonList);

        primaryStage.setScene(new Scene(root, 640, 480));
        //tableFromDatabase(root);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
