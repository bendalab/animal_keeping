package animalkeeping.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.Scene;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import java.sql.*;
import java.util.*;
import javafx.scene.control.ScrollBar;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;

/**
 * Created by huben on 14.12.16.
 */
public class ButtonService {
    ArrayList<Button> buttonList = new ArrayList<Button>();
    ConnectionManager connector = new ConnectionManager();
    GridPane grid;
    GridPaneBuilder builder;
    Stage primaryStage;

    ButtonService(GridPane grid, GridPaneBuilder builder, Stage primaryStage){
        this.grid = grid;
        this.builder = builder;
        this. primaryStage = primaryStage;
    }


    public void addButtons() {

        for (int i = 0; i < buttonList.size(); i++) {
            grid.add(buttonList.get(i), 0, i);
        }
    }

    public void add(Button new_button){
        buttonList.add(new_button);

    }

    public void update_grid(){
        grid = builder.build();
        ScrollPane sc = new ScrollPane(grid);
        addButtons();
        primaryStage.setScene(new Scene(sc, 640, 480));


    }

    public void initialize(){
        FishButton count_btn = new FishButton(this, "Count fishes");
        add(count_btn);
        UserButton user_btn = new UserButton(this, "Tell me who works here");
        add(user_btn);
        primaryStage.setScene(new Scene(grid, 640, 480));

    }

}
