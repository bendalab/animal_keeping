package animalkeeping.ui;

import animalkeeping.model.Subject;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

/**
 * Created by huben on 11.01.17.
 */
public class CellFactoryProvider{
    public Callback<TableColumn<Subject, String>, TableCell<Subject, String>> c;

    public CellFactoryProvider(Scene currentScene){
        this.c = new Callback<TableColumn<Subject, String>, TableCell<Subject, String>>() {
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
                        cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                TableCell c = (TableCell) event.getSource();
                                System.out.println("Cell text: " + c.getText());
                                ScrollPane frame = (ScrollPane) currentScene.lookup("#scrollPane");
                                frame.setContent(null);
                                IndividualTable individualTable = new IndividualTable(c.getText());
                                frame.setContent(individualTable);
                            }
                        });
                        return cell;
                    }
                };
    }
}
