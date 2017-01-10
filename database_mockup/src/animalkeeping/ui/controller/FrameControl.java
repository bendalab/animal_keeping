package animalkeeping.ui.controller;

import animalkeeping.ui.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;
import javafx.scene.Parent;
import javafx.scene.Node;

/**
 * Created by huben on 09.01.17.
 */
public class FrameControl {

    public static SessionFactory sessionFactory;
    private Stage primaryStage;
    private BorderPane rootLayout;

    public FrameControl(Stage primaryStage){this.primaryStage = primaryStage;}

//    static public void startPersonsView(Stage primaryStage) throws Exception {
//
//        FXMLLoader loader = new FXMLLoader();
//        loader.setLocation(Main.class.getResource("fxml/PersonView.fxml"));
//        System.out.println(Main.class.getResource("fxml/PersonView.fxml"));
//        //rootLayout = loader.load();
//
//        // Show the scene containing the root layout.
//        Scene scene = new Scene(loader.load());
//        primaryStage.setScene(scene);
//
//        primaryStage.show();
//    }
}
