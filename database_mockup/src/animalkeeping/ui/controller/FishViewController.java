package animalkeeping.ui.controller;

import animalkeeping.model.Person;
import animalkeeping.ui.Main;
import animalkeeping.ui.FishTable;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.List;

public class FishViewController {

    @FXML
    private FishTable fishTable;

    @FXML
    private TableColumn<Person, Number> idCol;

    @FXML
    private TableColumn<Person, String> firstNameCol;

    @FXML
    private TableColumn<Person, String> lastNameCol;

    @FXML
    private TableColumn<Person, String> emailCol;


    @FXML
    private Button returnBtn;


    @FXML
    private Button refreshBtn;


    public void refreshTable() {
        fishTable.getItems().clear();
   /*     idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        firstNameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getFirstName()));
        lastNameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getLastName()));
        emailCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getEmail()));

        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            List result = session.createQuery("from Person").list();
            personsTable.getItems().addAll(result);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }*/
    }

    @FXML
    private void returnToMainView() throws Exception{
        Main.getPrimaryStage().setScene(new Scene(FXMLLoader.load(Main.class.getResource("fxml/MainView.fxml"))));

    }


}