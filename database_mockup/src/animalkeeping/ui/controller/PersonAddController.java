package animalkeeping.ui.controller;

import animalkeeping.model.Person;
import animalkeeping.ui.*;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.awt.*;
import java.util.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.dom4j.Text;
import org.hibernate.HibernateException;
import org.hibernate.Session;

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
        Person nP = new Person();
        nP.setFirstName(firstFld.getText());
        nP.setLastName(lastFld.getText());
        nP.setEmail(emailFld.getText());

        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();

            System.out.println(nP);

            session.save(nP);

            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }


        //database add new person, give id
    }
    @FXML
    private void returnToMainView() throws Exception{
        Main.getPrimaryStage().setScene(new Scene(FXMLLoader.load(Main.class.getResource("fxml/MainView.fxml"))));

    }


}
