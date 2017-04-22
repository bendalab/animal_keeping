package animalkeeping.ui;

import animalkeeping.logging.Communicator;
import animalkeeping.model.*;
import animalkeeping.ui.controller.TimelineController;
import animalkeeping.util.Dialogs;
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

public class SubjectView extends VBox implements Initializable, View {
    @FXML private ScrollPane tableScrollPane;
    @FXML private Label idLabel;
    @FXML private Label nameLabel;
    @FXML private Label aliasLabel;
    @FXML private Label housingStartLabel;
    @FXML private Label housingEndLabel;
    @FXML private Label statusLabel;
    @FXML private Label originLabel;
    @FXML private Label speciesLabel;
    @FXML private Tab housingHistoryTab;
    @FXML private Tab observationsTab;
    @FXML private Tab treatmentsTab;
    @FXML private VBox timelineVBox;
    @FXML private RadioButton deadOrAliveRadioBtn;

    private SubjectsTable subjectsTable;
    private HousingTable housingTable;
    private NotesTable<SubjectNote> notesTable;
    private TimelineController timeline;
    private TreatmentsTable treatmentsTable;
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


    public SubjectView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/animalkeeping/ui/fxml/FishView.fxml"));
        loader.setController(this);
        try {
            this.getChildren().add(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        subjectsTable = new SubjectsTable();
        subjectsTable.getSelectionModel().getSelectedItems().addListener(new FishTableListChangeListener());
        subjectsTable.setAliveFilter(true);
        timeline = new TimelineController();

        this.tableScrollPane.setContent(subjectsTable);
        this.tableScrollPane.prefHeightProperty().bind(this.heightProperty());
        this.tableScrollPane.prefWidthProperty().bind(this.widthProperty());
        this.timelineVBox.getChildren().add(timeline);
        idLabel.setText("");
        nameLabel.setText("");
        speciesLabel.setText("");
        originLabel.setText("");
        statusLabel.setText("");
        housingEndLabel.setText("");
        housingStartLabel.setText("");

        treatmentsTable = new TreatmentsTable();
        treatmentsTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Treatment>) c -> treatmentSelected(c.getList().size() > 0 ? c.getList().get(0) : null));
        treatmentsTab.setContent(treatmentsTable);

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
                editSubject(subjectsTable.getSelectionModel().getSelectedItem());
            }
        });
        controls.getChildren().add(editSubjectLabel);
        deleteSubjectLabel = new ControlLabel("delete subject", true);
        deleteSubjectLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                deleteSubject();
                subjectsTable.refresh(); //TODO check refresh methods!
            }
        });
        controls.getChildren().add(deleteSubjectLabel);

        controls.getChildren().add(new Separator(Orientation.HORIZONTAL));

        addTreatmentLabel = new ControlLabel("new treatment", true);
        addTreatmentLabel.setTooltip(new Tooltip("add a treatment entry for the selected subject"));
        addTreatmentLabel.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                addTreatment(subjectsTable.getSelectionModel().getSelectedItem());
                treatmentsTable.refresh();
            }
        });
        controls.getChildren().add(addTreatmentLabel);
        editTreatmentLabel = new ControlLabel("edit treatment", true);
        editTreatmentLabel.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                editTreatment(treatmentsTable.getSelectionModel().getSelectedItem());
                treatmentsTable.refresh();
            }
        });
        controls.getChildren().add(editTreatmentLabel);
        deleteTreatmentLabel = new ControlLabel("remove treatment", true);
        deleteTreatmentLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                deleteTreatment();
                treatmentsTable.refresh();
            }
        });
        controls.getChildren().add(deleteTreatmentLabel);

        controls.getChildren().add(new Separator(Orientation.HORIZONTAL));
        newComment = new ControlLabel("add observation", true);
        newComment.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                newSubjectObservation(subjectsTable.getSelectionModel().getSelectedItem());
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
                moveSubject(subjectsTable.getSelectionModel().getSelectedItem());
            }
        });
        controls.getChildren().add(moveSubjectLabel);

        reportDead = new ControlLabel("report dead", true);
        reportDead.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                reportDead(subjectsTable.getSelectionModel().getSelectedItem());
            }
        });
        controls.getChildren().add(reportDead);
    }


    private void subjectSelected(Subject s) {
        if (s != null) {
            idLabel.setText(s.getId().toString());
            nameLabel.setText(s.getName());
            aliasLabel.setText(s.getAlias() != null ? s.getAlias() : "");
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
                statusLabel.setText("In treatment: " + t.getTreatmentType().getName());
            } else if (s.getCurrentHousing() != null) {
                statusLabel.setText("In housing unit: " + s.getCurrentHousing().getHousing().getName());
            } else {
                statusLabel.setText("none");
            }

            treatmentsTable.setTreatments(s.getTreatments());
            timeline.setTreatments(s.getTreatments());
            housingTable.setSubject(s);
            notesTable.setNotes(s.getNotes());
        } else {
            idLabel.setText("");
            nameLabel.setText("");
            aliasLabel.setText("");
            originLabel.setText("");
            speciesLabel.setText("");
            statusLabel.setText("");
            housingEndLabel.setText("");
            housingStartLabel.setText("");
            treatmentsTable.setTreatments(null);
            timeline.setTreatments(null);
            housingTable.clear();
            notesTable.setNotes(null);
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
        this.subjectsTable.setNameFilter(name);
    }


    public void idFilter(Long id) {
        this.subjectsTable.setIdFilter(id);
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
        subjectsTable.setAliveFilter(deadOrAliveRadioBtn.isSelected());
    }

    private void deleteSubject() {
        subjectsTable.deleteSubject(subjectsTable.getSelectionModel().getSelectedItem());
    }


    private void deleteTreatment() {
        treatmentsTable.deleteTreatment(treatmentsTable.getSelectionModel().getSelectedItem());
    }

    private void deleteObservation(SubjectNote n) {
        notesTable.getSelectionModel().select(null);
        Communicator.pushDelete(n);
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
        subjectsTable.reportSubjectDead(s);
    }

    private void moveSubject(Subject s) {
        subjectsTable.moveSubject(s);
    }

    private void editTreatment(Treatment t) {
        Dialogs.editTreatmentDialog(t);
    }

    private void addTreatment(Subject s) {
        Dialogs.editTreatmentDialog(s);
    }

    private void newSubject() {
        subjectsTable.editSubject(null);
    }

    private void editSubject(Subject s) {
        subjectsTable.editSubject(s);
    }

    private void newSubjectObservation(Subject s) {
        Dialogs.editSubjectNoteDialog(s);
    }

    private void editSubjectObservation(SubjectNote sn) {
        Dialogs.editSubjectNoteDialog(sn, sn.getSubject());
    }

    @Override
    public void refresh() {
        Subject s = subjectsTable.getSelectionModel().getSelectedItem();
        subjectsTable.getSelectionModel().select(null);
        subjectSelected(null);
        subjectsTable.refresh();
        if (subjectsTable.getItems().contains(s)) {
            subjectSelected(s);
        } else {
            subjectSelected(null);
        }
    }
}