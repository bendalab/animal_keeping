package animalkeeping.ui.controller;

import animalkeeping.model.*;
import animalkeeping.ui.*;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.awt.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.dom4j.Text;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;

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
    private TextField nameField;
    @FXML
    private TextField speciesIdField;
    @FXML
    private TextField aliasField;
    @FXML
    private TextField typeIdField;
    @FXML
    private TextField sourceIdField;
    @FXML
    private TextField housingIdField;
    @FXML
    private TextField housingTypeIdField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button addSubject;

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
    private void addSubject(){
        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
        Subject newS = new Subject();
        newS.setName(nameField.getText()) ;

        List<SpeciesType> specTyp = session.createQuery("from SpeciesType where id = " + speciesIdField.getText()).list();

        newS.setSpeciesType(specTyp.get(0));

        newS.setAlias(aliasField.getText());

        List<SubjectType> subTyp = session.createQuery("from SubjectType where id = " + typeIdField.getText()).list();

        newS.setSubjectType(subTyp.get(0));

        List<SupplierType> supp = session.createQuery("from SupplierType where id = " + sourceIdField.getText()).list();

        newS.setSupplier(supp.get(0));

        System.out.println(newS);

        session.save(newS);

        System.out.println(newS);
        //find Housing
        Housing hou = new Housing();

        hou.setSubject(newS);
        hou.setStart(Timestamp.from(Instant.now()));

        System.out.println(hou.getStart());

        List<HousingUnit> houTyp = session.createQuery("from HousingUnit where id = " + housingIdField.getText()).list();

        hou.setHousing(houTyp.get(0));

        session.save(hou);

        System.out.println(hou);



        //TODO?: session.update(HousingUnit());


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
