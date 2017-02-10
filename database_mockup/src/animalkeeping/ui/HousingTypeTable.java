package animalkeeping.ui;

import animalkeeping.model.HousingType;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.Collection;
import java.util.List;

public class HousingTypeTable  extends TableView<HousingType> {
    private TableColumn<HousingType, Number> idCol;
    private TableColumn<HousingType, String> nameCol;
    private TableColumn<HousingType, String> descriptionCol;

    public HousingTypeTable() {
        initTable();
        refresh();
    }


    public HousingTypeTable(Collection<HousingType> types) {
        initTable();
        this.setTypes(types);
    }



    private void initTable() {
        idCol = new TableColumn<HousingType, Number>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        idCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        nameCol = new TableColumn<HousingType, String>("name");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        nameCol.prefWidthProperty().bind(this.widthProperty().multiply(0.25));

        descriptionCol = new TableColumn<HousingType, String>("description");
        descriptionCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDescription()));
        descriptionCol.prefWidthProperty().bind(this.widthProperty().multiply(0.6));

        this.getColumns().addAll(idCol, nameCol, descriptionCol);
    }


    public void setTypes(Collection<HousingType> types) {
        this.getItems().clear();
        this.getItems().addAll(types);
    }

    @Override
    public void refresh() {
        Session session = Main.sessionFactory.openSession();
        List<HousingType> housingTypes = null;
        try {
            session.beginTransaction();
            housingTypes = session.createQuery("from HousingType", HousingType.class).list();
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }
        setTypes(housingTypes);
        super.refresh();
    }
}
