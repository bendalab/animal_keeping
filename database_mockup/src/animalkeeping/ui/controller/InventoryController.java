package animalkeeping.ui.controller;

import animalkeeping.model.*;
import animalkeeping.ui.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by jan on 14.01.17.
 */
public class InventoryController extends VBox implements Initializable, View {
    @FXML private PieChart populationChart;
    @FXML private VBox unitsBox;
    @FXML private VBox chartVbox;
    @FXML private Label allLabel;
    @FXML private ScrollPane tableScrollPane;
    private VBox controls;
    private HashMap<String, HousingUnit> unitsHashMap;
    private ControlLabel animalUseLabel;
    private ControlLabel exportLabel;


    public InventoryController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/Inventory.fxml"));
        loader.setController(this);
        try {
            this.getChildren().add(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        unitsHashMap = new HashMap<>();
        controls = new VBox();
        animalUseLabel = new ControlLabel("animal use", true);
        exportLabel = new ControlLabel("export overview", true);
        controls.getChildren().add(animalUseLabel);
        controls.getChildren().add(exportLabel);
        this.fillList();
        listAllPopulation();
    }

    private void fillList() {
        List<HousingUnit> result = null;
        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            result = session.createQuery("from HousingUnit where parent_unit_id is NULL").list();
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }

        if (result != null) {
            for (HousingUnit h : result) {
                unitsHashMap.put(h.getName(), h);
                Label label = new Label(h.getName());
                label.setUnderline(true);
                label.setTextFill(allLabel.getTextFill());
                label.setOnMouseClicked(event -> listPopulation(unitsHashMap.get(h.getName())));
                unitsBox.getChildren().add(label);
                unitsBox.setMargin(label, new Insets(0., 0., 5., 5.0 ));
              }
        } else {
            System.out.println("mist");
        }
    }

    @FXML
    private void listAllPopulation() {
        List<SpeciesType> result = null;

        List<Housing> housings = null;
        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            result = session.createQuery("from SpeciesType").list();
            housings = session.createQuery("from Housing where end_datetime is null").list();
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }
        Integer count = 0;
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        if (result != null) {
            for (SpeciesType st : result) {

                count += st.getCount();
                pieChartData.add(new PieChart.Data(st.getName() + " (" + st.getCount() + ")", st.getCount()));
            }
        }
        populationChart.setTitle("Total population: " + count.toString());
        populationChart.setData(pieChartData);
        tableScrollPane.setContent(null);
        HousingTable table = new HousingTable(housings);
        tableScrollPane.setContent(table);
    }


    private void listPopulation(HousingUnit housingUnit) {
        Set<Housing> housings = housingUnit.getAllHousings(true);

        Set<Subject> subjects = new HashSet<>();
        collectSubjects(subjects, housingUnit, true);

        HashMap<String, Integer> counts = new HashMap<>();
        for (Subject s : subjects) {
            if (counts.containsKey(s.getSpeciesType().getName())) {
                Integer c = counts.get(s.getSpeciesType().getName());
                c += 1;
                counts.put(s.getSpeciesType().getName(), c);
            } else {
                counts.put(s.getSpeciesType().getName(), 1);
            }
        }

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (String st : counts.keySet()) {
            pieChartData.add(new PieChart.Data(st + " (" + counts.get(st) + ")", counts.get(st)));
        }

        populationChart.setTitle(housingUnit.getName() + ": " + subjects.size());
        populationChart.setData(pieChartData);
        tableScrollPane.setContent(null);
        HousingTable table = new HousingTable(housings);
        tableScrollPane.setContent(table);
    }

    @Override
    public  VBox getControls() {
        return controls;
    }

    @Override
    public void refresh() {
        //TODO refresh
    }

    private void collectSubjects(Set<Subject> subjects, HousingUnit h) {
        collectSubjects(subjects, h, true);
    }

    private void collectSubjects(Set<Subject> subjects, HousingUnit h, Boolean currentOnly) {
        for (Housing housing : h.getAllHousings(true)) {
            subjects.add(housing.getSubject());

        }
    }
}
