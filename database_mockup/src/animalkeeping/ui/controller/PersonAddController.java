package animalkeeping.ui.controller;

import animalkeeping.ui.*;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.awt.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.dom4j.Text;

/**
 * Created by huben on 11.01.17.
 */
public class PersonAddController {
    @FXML
    private TextField firstFld;

    @FXML
    private TextField lastFld;

    @FXML
    private TextField emailFld;

    @FXML
    private Button userAddBtn;

    @FXML
    private Button returnBtn;

    @FXML
    private void addUser(){
        String firstName = firstFld.getText();
        String lastName = lastFld.getText();
        String emailadresse = emailFld.getText();
        //database add new person, give id
    }
    @FXML
    private void returnToMainView() throws Exception{
        Main.getPrimaryStage().setScene(new Scene(FXMLLoader.load(Main.class.getResource("fxml/MainView.fxml"))));

    }


}
