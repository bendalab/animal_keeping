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
public class IndividualTable extends javafx.scene.control.TableView {

    private Integer id;
    private String name;
    private TableColumn<Treatment, Number> idCol;
    private TableColumn<Treatment, String> subectCol;
    private TableColumn<Treatment, String> personCol;
    private TableColumn<Treatment, String> treatmentCol;
    private TableColumn<Treatment, Date> startCol;
    private TableColumn<Treatment, Date> endCol;
    private TableColumn<Treatment, Boolean> finalCol;

    public IndividualTable(int id){
        super();
        this.id = id;
        idCol = new TableColumn<Treatment, Number>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        subectCol = new TableColumn<Treatment, String>("subject");
        subectCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSubject().getName()));
        personCol = new TableColumn<Treatment, String>("by person");
        personCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPerson().getFirstName() +
                " " + data.getValue().getPerson().getLastName()));
        treatmentCol = new TableColumn<Treatment, String>("treatment");
        treatmentCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getType().getName()));
        startCol = new TableColumn<Treatment, Date>("start");
        startCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Date>(data.getValue().getStart()));
        endCol = new TableColumn<Treatment, Date>("end");
        endCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Date>(data.getValue().getEnd()));
        finalCol = new TableColumn<Treatment, Boolean>("is final");
        finalCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Boolean>(data.getValue().getType().isInvasive()));


        this.getColumns().addAll(idCol, subectCol, personCol, treatmentCol, startCol, endCol, finalCol);
        initId();
    }
    public IndividualTable(String name){
        super();
        this.name = name;
        idCol = new TableColumn<Treatment, Number>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        subectCol = new TableColumn<Treatment, String>("subject");
        subectCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSubject().getName()));
        personCol = new TableColumn<Treatment, String>("by person");
        personCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPerson().getFirstName() +
                " " + data.getValue().getPerson().getLastName()));
        treatmentCol = new TableColumn<Treatment, String>("treatment");
        treatmentCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getType().getName()));
        startCol = new TableColumn<Treatment, Date>("start");
        startCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Date>(data.getValue().getStart()));
        endCol = new TableColumn<Treatment, Date>("end");
        endCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Date>(data.getValue().getEnd()));
        finalCol = new TableColumn<Treatment, Boolean>("is final");
        finalCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Boolean>(data.getValue().getType().isInvasive()));


        this.getColumns().addAll(idCol, subectCol, personCol, treatmentCol, startCol, endCol, finalCol);
        initName();
    }



    public void initId() {
        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();

            List<Subject> result = session.createQuery("from Treatment where subject.id = " + this.id.toString()).list();

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

    public void initName(){
        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();

            List<Subject> result = session.createQuery("from Treatment where subject.name = \'" + this.name + "\'").list();


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
