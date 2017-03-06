package animalkeeping.ui;

import animalkeeping.model.Treatment;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class TreatmentsTable extends TableView<Treatment>{
    private TableColumn<Treatment, Number> idCol;
    private TableColumn<Treatment, String> subectCol;
    private TableColumn<Treatment, String> personCol;
    private TableColumn<Treatment, String> treatmentCol;
    private TableColumn<Treatment, Date> startCol;
    private TableColumn<Treatment, Date> endCol;
    private TableColumn<Treatment, Boolean> finalCol;


    public TreatmentsTable() {
        super();
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
        init();
    }

  /*  public TreatmentsTable(ObservableList<Treatment> items) {
        this();
        this.setItems(items);
    }*/

    private void init() {
        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            List result = session.createQuery("from Treatment").list();

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

    public void setTreatments(Set<Treatment> treatments) {
        this.getItems().clear();
        this.getItems().addAll(treatments);
    }
}
