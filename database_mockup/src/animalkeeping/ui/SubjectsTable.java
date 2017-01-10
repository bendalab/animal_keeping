package animalkeeping.ui;

import animalkeeping.model.*;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.util.Callback;
import javafx.scene.input.MouseEvent;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.Parent;

/**
 * Created by jan on 01.01.17.
 */
public class SubjectsTable extends TableView {
    private TableColumn<Subject, Number> idCol;
    private TableColumn<Subject, String> nameCol;
    private TableColumn<Subject, String> aliasCol;
    private TableColumn<Subject, String> speciesCol;
    private TableColumn<Subject, String> subjectCol;
    private TableColumn<Subject, String> supplierCol;


    public SubjectsTable() {
        super();
        Callback<TableColumn<Subject,String>, TableCell<Subject,String>> subjectCellFactory =
                new Callback<TableColumn<Subject,String>, TableCell<Subject,String>>() {
                    public TableCell call(TableColumn p) {
                        TableCell cell = new TableCell<Subject, String>() {

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                setText(empty ? null : getString());
                                setGraphic(null);
                            }

                            private String getString() {
                                return getItem() == null ? "" : getItem().toString();

                            }


                        };
                        cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()      {
                            @Override
                            public void handle(MouseEvent event) {
                                TableCell c = (TableCell) event.getSource();
                                System.out.println("Cell text: " + c.getText());
                                ScrollPane frame = (ScrollPane) getScene().lookup("#scrollPane");
                                frame.setContent(null);
                                IndividualTable individualTable = new IndividualTable(c.getText());
                                frame.setContent(individualTable);
                            }
                        });
                        return cell;
                    }
                };
        idCol = new TableColumn<Subject, Number>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        nameCol = new TableColumn<Subject, String>("name");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        nameCol.setCellFactory(subjectCellFactory);
        aliasCol = new TableColumn<Subject, String>("alias");
        aliasCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getAlias()));
        speciesCol = new TableColumn<Subject, String>("species");
        speciesCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSpeciesType().getName()));
        subjectCol = new TableColumn<Subject, String>("subject");
        subjectCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSubjectType().getName()));
        supplierCol = new TableColumn<Subject, String>("supplier");
        supplierCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSupplier().getName()));
        this.getColumns().addAll(idCol, nameCol, aliasCol, speciesCol, subjectCol, supplierCol);
        init();
    }

    public SubjectsTable(ObservableList<Subject> items) {
        this();
        this.setItems(items);
    }

    private void init() {

        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();

            List<Subject> result = session.createQuery("from Subject").list();

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

