package animalkeeping.ui;

import animalkeeping.model.*;
import animalkeeping.ui.controller.TimelineController;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.hibernate.Session;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static animalkeeping.util.DateTimeHelper.getDateTime;

public class FishView extends VBox implements Initializable, View {
    @FXML private ScrollPane tableScrollPane;
    @FXML private Label idLabel;
    @FXML private Label nameLabel;
    @FXML private Label housingStartLabel;
    @FXML private Label housingEndLabel;
    @FXML private Label statusLabel;
    @FXML private Label originLabel;
    @FXML private Label speciesLabel;
    @FXML private TableView<Treatment> treatmentTable;
    @FXML private Tab housingHistoryTab;
    @FXML private Tab observationsTab;
    @FXML private VBox timelineVBox;
    @FXML private RadioButton deadOrAliveRadioBtn;

    private SubjectsTable fishTable;
    private HousingTable housingTable;
    private NotesTable<SubjectNote> notesTable;
    private TimelineController timeline;
    private TableColumn<Treatment, Number> idCol;
    private TableColumn<Treatment, String> typeCol;
    private TableColumn<Treatment, Date> startDateCol;
    private TableColumn<Treatment, Date> endDateCol;
    private TableColumn<Treatment, String> nameCol;
    private TableColumn<Treatment, String> personCol;
    private ControlLabel reportDead;
    private ControlLabel moveSubjectLabel;
    private ControlLabel editSubjectLabel;
    private ControlLabel deleteSubjectLabel;
    private ControlLabel addTreatmentLabel;
    private ControlLabel editTreatmentLabel;
    private ControlLabel deleteTreatmentLabel;
    private ControlLabel newComment;
    private ControlLabel editComment;
    private ControlLabel deleteComment;
    private VBox controls;


    public FishView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/FishView.fxml"));
        loader.setController(this);
        try {
            this.getChildren().add(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fishTable = new SubjectsTable();
        fishTable.getSelectionModel().getSelectedItems().addListener(new FishTableListChangeListener());
        fishTable.setAliveFilter(true);
        timeline = new TimelineController();

        //personsTable.resize();
        this.tableScrollPane.setContent(fishTable);
        this.timelineVBox.getChildren().add(timeline);
        idLabel.setText("");
        nameLabel.setText("");
        speciesLabel.setText("");
        originLabel.setText("");
        statusLabel.setText("");
        housingEndLabel.setText("");
        housingStartLabel.setText("");

        idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        idCol.prefWidthProperty().bind(treatmentTable.widthProperty().multiply(0.08));

        typeCol = new TableColumn<>("treatment");
        typeCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getType().getName()));
        typeCol.prefWidthProperty().bind(treatmentTable.widthProperty().multiply(0.18));

        startDateCol = new TableColumn<>("start");
        startDateCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getStart()));
        startDateCol.prefWidthProperty().bind(treatmentTable.widthProperty().multiply(0.20));

        endDateCol = new TableColumn<>("end");
        endDateCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getEnd()));
        endDateCol.prefWidthProperty().bind(treatmentTable.widthProperty().multiply(0.20));

        nameCol = new TableColumn<>("name");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSubject().getName()));
        nameCol.prefWidthProperty().bind(treatmentTable.widthProperty().multiply(0.16));

        personCol = new TableColumn<>("person");
        personCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPerson().getLastName() +
         ", " + data.getValue().getPerson().getFirstName()));
        personCol.prefWidthProperty().bind(treatmentTable.widthProperty().multiply(0.16));

        treatmentTable.getColumns().clear();
        treatmentTable.getColumns().addAll(idCol, typeCol, startDateCol, endDateCol, nameCol, personCol);
        treatmentTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Treatment>) c -> treatmentSelected(c.getList().size() > 0 ? c.getList().get(0) : null));

        housingTable = new HousingTable();
        housingHistoryTab.setContent(housingTable);

        notesTable = new NotesTable<>();
        notesTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<SubjectNote>) c -> noteSelected(c.getList().size() > 0 ? c.getList().get(0) : null));
        observationsTab.setContent(notesTable);

        controls = new VBox();
        ControlLabel newSubjectLabel = new ControlLabel("new subject", false);
        newSubjectLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                newSubject();
            }
        });
        controls.getChildren().add(newSubjectLabel);
        editSubjectLabel = new ControlLabel("edit subject", true);
        editSubjectLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                editSubject(fishTable.getSelectionModel().getSelectedItem());
            }
        });
        controls.getChildren().add(editSubjectLabel);
        deleteSubjectLabel = new ControlLabel("delete subject", true);
        deleteSubjectLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                deleteSubject();
                fishTable.refresh(); //TODO check refresh methods!
            }
        });
        controls.getChildren().add(deleteSubjectLabel);

        controls.getChildren().add(new Separator(Orientation.HORIZONTAL));

        addTreatmentLabel = new ControlLabel("new treatment", true);
        addTreatmentLabel.setTooltip(new Tooltip("add a treatment entry for the selected subject"));
        addTreatmentLabel.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                addTreatment(fishTable.getSelectionModel().getSelectedItem());
                treatmentTable.refresh();
            }
        });
        controls.getChildren().add(addTreatmentLabel);
        editTreatmentLabel = new ControlLabel("edit treatment", true);
        editTreatmentLabel.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                editTreatment(treatmentTable.getSelectionModel().getSelectedItem());
                treatmentTable.refresh();
            }
        });
        controls.getChildren().add(editTreatmentLabel);
        deleteTreatmentLabel = new ControlLabel("remove treatment", true);
        deleteTreatmentLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                deleteTreatment();
                treatmentTable.refresh();
            }
        });
        controls.getChildren().add(deleteTreatmentLabel);

        controls.getChildren().add(new Separator(Orientation.HORIZONTAL));
        newComment = new ControlLabel("add observation", true);
        newComment.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                newSubjectObservation(fishTable.getSelectionModel().getSelectedItem());
            }
        });
        controls.getChildren().add(newComment);
        editComment = new ControlLabel("edit observation", true);
        editComment.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                editSubjectObservation(notesTable.getSelectionModel().getSelectedItem());
            }
        });
        controls.getChildren().add(editComment);
        deleteComment = new ControlLabel("delete observation", true);
        deleteComment.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                deleteObservation(notesTable.getSelectionModel().getSelectedItem());
            }
        });
        controls.getChildren().add(deleteComment);

        controls.getChildren().add(new Separator(Orientation.HORIZONTAL));
        moveSubjectLabel = new ControlLabel("move subject", true);
        moveSubjectLabel.setTooltip(new Tooltip("relocate subject to a different housing unit"));
        moveSubjectLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                moveSubject(fishTable.getSelectionModel().getSelectedItem());
            }
        });
        controls.getChildren().add(moveSubjectLabel);

        reportDead = new ControlLabel("report dead", true);
        reportDead.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                reportDead(fishTable.getSelectionModel().getSelectedItem());
            }
        });
        controls.getChildren().add(reportDead);
    }


    private void subjectSelected(Subject s) {
        if (s != null) {
            idLabel.setText(s.getId().toString());
            nameLabel.setText(s.getName());
            speciesLabel.setText(s.getSpeciesType().getName());
            originLabel.setText(s.getSupplier().getName());
            Iterator<Housing> iter = s.getHousings().iterator();
            Housing firstHousing = null;
            Housing lastHousing = null;
            if (iter.hasNext()) {
                firstHousing = iter.next();
            }
            while (iter.hasNext()) {
                lastHousing = iter.next();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            housingStartLabel.setText(firstHousing != null ? sdf.format(firstHousing.getStart()) : "");
            if (lastHousing != null) {
                housingEndLabel.setText(lastHousing.getEnd() != null ? sdf.format(lastHousing.getEnd()) : "");
            } else {
                housingEndLabel.setText(firstHousing.getEnd() != null ? sdf.format(firstHousing.getEnd()) : "");
            }
            Iterator<Treatment> titer = s.getTreatments().iterator();
            Treatment t = null;
            while (titer.hasNext()) {
                t = titer.next();
            }
            if (t != null && t.getEnd() == null) {
                statusLabel.setText("In treatment: " + t.getType().getName());
            } else if (s.getCurrentHousing() != null) {
                statusLabel.setText("In housing unit: " + s.getCurrentHousing().getHousing().getName());
            } else {
                statusLabel.setText("none");
            }

            treatmentTable.getItems().clear();
            treatmentTable.getItems().addAll(s.getTreatments());
            timeline.setTreatments(s.getTreatments());
            housingTable.setHousings(s.getHousings());
            notesTable.setNotes(s.getNotes());
        } else {
            idLabel.setText("");
            nameLabel.setText("");
            originLabel.setText("");
            speciesLabel.setText("");
            statusLabel.setText("");
            housingEndLabel.setText("");
            housingStartLabel.setText("");
            treatmentTable.getItems().clear();
            timeline.setTreatments(null);
            housingTable.setHousings(null);
            notesTable.setItems(null);
        }
        moveSubjectLabel.setDisable(s == null);
        deleteSubjectLabel.setDisable(s == null);
        editSubjectLabel.setDisable(s == null);
        reportDead.setDisable(s == null);
        addTreatmentLabel.setDisable(s==null);
        newComment.setDisable(s==null);
    }

    private void treatmentSelected(Treatment t) {
        editTreatmentLabel.setDisable(t == null);
        deleteTreatmentLabel.setDisable(t == null);
    }

    private void noteSelected(SubjectNote n) {
        newComment.setDisable(n == null);
        editComment.setDisable(n == null);
        deleteComment.setDisable(n == null);
    }

    public void nameFilter(String name) {
        this.fishTable.setNameFilter(name);
    }


    public void idFilter(Long id) {
        this.fishTable.setIdFilter(id);
    }

    private class FishTableListChangeListener implements ListChangeListener<Subject> {
        @Override
        public void onChanged(Change<? extends Subject> c) {
            if (c.getList().size() > 0) {
                subjectSelected(c.getList().get(0));
            }
        }
    }

    @FXML
    private void showAllOrCurrent() {
        fishTable.setAliveFilter(deadOrAliveRadioBtn.isSelected());
    }

    private void deleteSubject() {
        Subject s = fishTable.getSelectionModel().getSelectedItem();
        if (!s.getTreatments().isEmpty()) {
            showInfo("Cannot delete subject " + s.getName() + " since it is referenced by " +
                    Integer.toString(s.getTreatments().size()) + " treatment entries! Delete them first.");
        } else {
            Session session = Main.sessionFactory.openSession();
            session.beginTransaction();
            session.delete(s);
            session.getTransaction().commit();
            session.close();
        }
        fishTable.getSelectionModel().select(null);
    }


    private void deleteTreatment() {
        Treatment t = treatmentTable.getSelectionModel().getSelectedItem();
        treatmentTable.getSelectionModel().select(null);
        Session session = Main.sessionFactory.openSession();
        session.beginTransaction();
        session.delete(t);
        session.getTransaction().commit();
        session.close();
        treatmentTable.getItems().remove(t);
    }

    private void deleteObservation(SubjectNote n) {
        notesTable.getSelectionModel().select(null);
        Session session = Main.sessionFactory.openSession();
        session.beginTransaction();
        session.delete(n);
        session.getTransaction().commit();
        session.close();
        notesTable.getItems().removeAll(n);
    }

    @Override
    public VBox getControls() {
        return controls;
    }

    private void showInfo(String  info) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(info);
        alert.show();
    }

    private void reportDead(Subject s) {
        Housing current_housing = s.getCurrentHousing();
        Dialog<Date> dialog = new Dialog<>();
        dialog.setTitle("Report subject dead ...");
        dialog.setHeight(200);
        dialog.setWidth(300);
        VBox box = new VBox();
        box.setFillWidth(true);
        HBox dateBox = new HBox();
        dateBox.getChildren().add(new Label("date"));
        DatePicker dp = new DatePicker();
        dp.setValue(LocalDate.now());
        dateBox.getChildren().add(dp);
        HBox timeBox = new HBox();
        timeBox.getChildren().add(new Label("time"));
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        TextField timeField = new TextField(timeFormat.format(new Date()));
        timeBox.getChildren().add(timeField);
        box.getChildren().add(dateBox);
        box.getChildren().add(timeBox);
        ComboBox<Person> personComboBox = new ComboBox<>();
        personComboBox.setConverter(new StringConverter<Person>() {
            @Override
            public String toString(Person object) {
                return object.getFirstName() + ", " + object.getLastName();
            }

            @Override
            public Person fromString(String string) {
                return null;
            }
        });

        Session session = Main.sessionFactory.openSession();
        session.beginTransaction();
        List<Person> persons = session.createQuery("from Person", Person.class).list();
        session.getTransaction().commit();
        personComboBox.getItems().addAll(persons);
        HBox personBox = new HBox();
        personBox.getChildren().add(new Label("person"));
        personBox.getChildren().add(personComboBox);
        box.getChildren().add(personBox);
        box.getChildren().add(new Label("comment"));
        TextArea commentArea = new TextArea();
        box.getChildren().add(commentArea);
        dialog.getDialogPane().setContent(box);

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

        dialog.setResultConverter(new Callback<ButtonType, Date>() {
            @Override
            public Date call(ButtonType b) {
                if (b == buttonTypeOk) {
                    return getDateTime(dp.getValue(), timeField.getText());
                }
                return null;
            }
        });
        Optional<Date> result = dialog.showAndWait();
        if (result.isPresent() && result.get().after(current_housing.getStart())) {
            current_housing.setEnd(result.get());
            SubjectNote note = new SubjectNote("reported dead", commentArea.getText(), result.get(), s);
            note.setPerson(personComboBox.getValue());
            session.beginTransaction();
            session.saveOrUpdate(current_housing);
            session.saveOrUpdate(note);
            session.getTransaction().commit();
        } else {
            showInfo("Error during report subject. Reported date before start date of current housing?!");
        }
        if (session.isOpen()) {
            session.close();
        }
    }

    private void moveSubject(Subject s) {
        HousingUnit current_hu = s.getCurrentHousing().getHousing();

        Dialog<HousingUnit> dialog = new Dialog<>();
        dialog.setTitle("Select a housing unit");
        dialog.setHeight(200);
        dialog.setWidth(400);
        dialog.setResizable(true);
        HousingUnitTable hut = new HousingUnitTable();
        VBox box = new VBox();
        box.setFillWidth(true);
        HBox dateBox = new HBox();
        dateBox.getChildren().add(new Label("relocation date"));
        DatePicker dp = new DatePicker();
        dp.setValue(LocalDate.now());
        dateBox.getChildren().add(dp);
        HBox timeBox = new HBox();
        timeBox.getChildren().add(new Label("relocation time"));
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        TextField timeField = new TextField(timeFormat.format(new Date()));
        timeBox.getChildren().add(timeField);

        box.getChildren().add(dateBox);
        box.getChildren().add(timeBox);
        box.getChildren().add(hut);
        dialog.getDialogPane().setContent(box);

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

        dialog.setResultConverter(new Callback<ButtonType, HousingUnit>() {
            @Override
            public HousingUnit call(ButtonType b) {
                if (b == buttonTypeOk) {
                    return hut.getSelectedUnit();
                }
                return null;
            }
        });
        Optional<HousingUnit> result = dialog.showAndWait();
        if (result.isPresent() && result.get() != current_hu) {
            LocalDate d = dp.getValue();
            Date currentDate = getDateTime(d, timeField.getText());

            HousingUnit new_hu = result.get();
            Housing current_housing = s.getCurrentHousing();
            if (currentDate.before(current_housing.getStart())) {
                showInfo("Error during relocation of subject. Relocation date before start date of current housing!");
                return;
            }

            Housing new_housing = new Housing();
            new_housing.setStart(currentDate);
            new_housing.setSubject(s);
            new_housing.setHousing(new_hu);
            current_housing.setEnd(currentDate);
            Session session = Main.sessionFactory.openSession();
            session.beginTransaction();
            session.saveOrUpdate(current_housing);
            session.saveOrUpdate(new_housing);
            session.getTransaction().commit();
        }
    }

    private void showTreatmentDialog(TreatmentForm tf) {
        if (tf == null) {
            return;
        }
        Dialog<Treatment> dialog = new Dialog<>();
        dialog.setTitle("Add treatment...");
        dialog.setHeight(200);
        dialog.setWidth(400);
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(tf);

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

        dialog.setResultConverter(new Callback<ButtonType, Treatment>() {
            @Override
            public Treatment call(ButtonType b) {
                if (b == buttonTypeOk) {
                    return tf.persistTreatment();
                }
                return null;
            }
        });
        dialog.showAndWait();
    }

    private void editTreatment(Treatment t) {
        if (t == null) {
            return;
        }
        TreatmentForm tf = new TreatmentForm(t);
        showTreatmentDialog(tf);
    }

    private void addTreatment(Subject s) {
        if (s == null) {
            return;
        }
        TreatmentForm tf = new TreatmentForm(s);
        showTreatmentDialog(tf);

    }

    private void showSubjectDialog(SubjectForm sf) {
        if (sf == null) {
            return;
        }
        Dialog<Subject> dialog = new Dialog<>();
        dialog.setTitle("Add/edit subject ...");
        dialog.setHeight(200);
        dialog.setWidth(400);
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(sf);

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

        dialog.setResultConverter(new Callback<ButtonType, Subject>() {
            @Override
            public Subject call(ButtonType b) {
                if (b == buttonTypeOk) {
                    return sf.persistSubject();
                }
                return null;
            }
        });
        dialog.showAndWait();
    }

    private void newSubject() {
        SubjectForm sf = new SubjectForm();
        showSubjectDialog(sf);
    }

    private void editSubject(Subject s) {
        SubjectForm sf = new SubjectForm(s);
        showSubjectDialog(sf);
    }

    private void newSubjectObservation(Subject s) {
        SubjectNotesForm snf = new SubjectNotesForm(s);
        showSubjectNotesDialog(snf);
    }

    private void editSubjectObservation(SubjectNote sn) {
        SubjectNotesForm snf = new SubjectNotesForm(sn, sn.getSubject());
        showSubjectNotesDialog(snf);
    }

    private void showSubjectNotesDialog(SubjectNotesForm snf) {
        if (snf == null) {
            return;
        }
        Dialog<SubjectNote> dialog = new Dialog<>();
        dialog.setTitle("Add/edit subject ...");
        dialog.setHeight(200);
        dialog.setWidth(400);
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(snf);

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

        dialog.setResultConverter(new Callback<ButtonType, SubjectNote>() {
            @Override
            public SubjectNote call(ButtonType b) {
                if (b == buttonTypeOk) {
                    return snf.persist();
                }
                return null;
            }
        });
        dialog.showAndWait();
    }

    @Override
    public void refresh() {
        //TODO refresh
    }
}