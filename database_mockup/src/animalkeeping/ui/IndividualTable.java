package animalkeeping.ui;

import animalkeeping.model.*;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.Date;
import java.util.List;


/**
 * Created by huben on 10.01.17.
 */
public class IndividualTable extends javafx.scene.control.TableView<Treatment> {

    private Long id;
    private String name;
    private TableColumn<Treatment, Number> idCol;
    private TableColumn<Treatment, String> subectCol;
    private TableColumn<Treatment, String> personCol;
    private TableColumn<Treatment, String> treatmentCol;
    private TableColumn<Treatment, Date> startCol;
    private TableColumn<Treatment, Date> endCol;
    private TableColumn<Treatment, Boolean> finalCol;

    public IndividualTable(Long id){
        super();
        this.id = id;
        idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        subectCol = new TableColumn<>("subject");
        subectCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSubject().getName()));
        personCol = new TableColumn<>("by person");
        personCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPerson().getFirstName() +
                " " + data.getValue().getPerson().getLastName()));
        treatmentCol = new TableColumn<>("treatment");
        treatmentCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getType().getName()));
        startCol = new TableColumn<>("start");
        startCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getStart()));
        endCol = new TableColumn<>("end");
        endCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getEnd()));
        finalCol = new TableColumn<>("is final");
        finalCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getType().isInvasive()));


        this.getColumns().addAll(idCol, subectCol, personCol, treatmentCol, startCol, endCol, finalCol);
        initId();
    }
    public IndividualTable(String name){
        super();
        this.name = name;
        idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        subectCol = new TableColumn<>("subject");
        subectCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSubject().getName()));
        personCol = new TableColumn<>("by person");
        personCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPerson().getFirstName() +
                " " + data.getValue().getPerson().getLastName()));
        treatmentCol = new TableColumn<>("treatment");
        treatmentCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getType().getName()));
        startCol = new TableColumn<>("start");
        startCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getStart()));
        endCol = new TableColumn<>("end");
        endCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getEnd()));
        finalCol = new TableColumn<>("is final");
        finalCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getType().isInvasive()));


        this.getColumns().addAll(idCol, subectCol, personCol, treatmentCol, startCol, endCol, finalCol);
        initName();
    }



    private void initId() {
        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();

            List<Treatment> result = session.createQuery("from Treatment where subject.id = " + this.id.toString(),
                    Treatment.class).list();

            this.getItems().addAll(result);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }

    }

    private void initName(){
        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();

            List<Treatment> result = session.createQuery("from Treatment where subject.name = \'" + this.name + "\'",
                    Treatment.class).list();

            this.getItems().addAll(result);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }
    }

}
