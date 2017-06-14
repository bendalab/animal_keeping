/******************************************************************************
 Copyright (c) 2017 Neuroethology Lab, University of Tuebingen,
 Jan Grewe <jan.grewe@g-node.org>,
 Dennis Huben <dennis.huben@rwth-aachen.de>

 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this list
 of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice, this
 list of conditions and the following disclaimer in the documentation and/or other
 materials provided with the distribution.

 3. Neither the name of the copyright holder nor the names of its contributors may
 be used to endorse or promote products derived from this software without specific
 prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 DAMAGE.

 * Created by jan on 01.05.17.

 *****************************************************************************/
package animalkeeping.ui.views;

import animalkeeping.model.*;
import animalkeeping.ui.widgets.ControlLabel;
import animalkeeping.ui.Main;
import animalkeeping.ui.widgets.TimelineController;
import animalkeeping.ui.tables.LicenseTable;
import animalkeeping.ui.tables.TreatmentTypeTable;
import animalkeeping.ui.tables.TreatmentsTable;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import org.hibernate.Session;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;

public class LicenseView extends AbstractView implements Initializable {
    @FXML private ScrollPane tableScrollPane;
    @FXML private Label idLabel;
    @FXML private Label nameLabel;
    @FXML private Label permitNoLabel;
    @FXML private Label agencyLabel;
    @FXML private Label respPersonLabel;
    @FXML private Label deputyPersonLabel;
    @FXML private Label startDateLabel;
    @FXML private Label endDateLabel;
    @FXML private VBox timelineVBox;
    @FXML private VBox quotaBox;
    @FXML private VBox typesBox;
    @FXML private VBox treatmentsBox;
    @FXML private TabPane tabPane;

    private LicenseTable licenseTable;
    private TreatmentTypeTable typeTable;
    private TreatmentsTable treatmentsTable;
    private QuotaView qv;
    private TimelineController timeline;
    private ControlLabel editLicenseLabel;
    private ControlLabel deleteLicenseLabel;
    private ControlLabel addQuota;
    private ControlLabel editQuotaLabel;
    private ControlLabel deleteQuotaLabel;
    private VBox controls;


    public LicenseView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/animalkeeping/ui/fxml/LicenseView.fxml"));
        loader.setController(this);
        try {
            this.getChildren().add(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabPane.prefWidthProperty().bind(prefWidthProperty());
        licenseTable = new LicenseTable();
        licenseTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<License>) c -> {
            if (c.getList().size() > 0) {
                licenseSelected(c.getList().get(0));
            }
        });
        tableScrollPane.prefHeightProperty().bind(heightProperty());
        tableScrollPane.prefWidthProperty().bind(widthProperty());
        typeTable = new TreatmentTypeTable();
        typeTable.prefWidthProperty().bind(tableScrollPane.widthProperty());
        typeTable.prefHeightProperty().bind(tableScrollPane.heightProperty());
        typesBox.getChildren().add(typeTable);

        treatmentsTable = new TreatmentsTable();
        treatmentsTable.prefWidthProperty().bind(prefWidthProperty());
        treatmentsBox.getChildren().add(treatmentsTable);

        timeline = new TimelineController();
        this.tableScrollPane.setContent(licenseTable);
        this.timelineVBox.getChildren().add(timeline);

        qv = new QuotaView();
        quotaBox.prefWidthProperty().bind(this.prefWidthProperty());
        qv.prefWidthProperty().bind(quotaBox.prefWidthProperty());
        qv.quotaTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Quota>) c -> {
            if (c.getList().size() > 0) {
                quotaSelected(c.getList().get(0));
            } else {
                quotaSelected(null);
            }
        });
        quotaBox.getChildren().add(qv);

        controls = new VBox();
        ControlLabel addLicenseLabel = new ControlLabel("new license", false);
        addLicenseLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                editLicense(null);
            }
        });
        controls.getChildren().add(addLicenseLabel);

        editLicenseLabel = new ControlLabel("edit license", true);
        editLicenseLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                editLicense(licenseTable.getSelectionModel().getSelectedItem());
            }
        });
        controls.getChildren().add(editLicenseLabel);

        deleteLicenseLabel = new ControlLabel("delete license", true);
        deleteLicenseLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
              deleteLicense(licenseTable.getSelectionModel().getSelectedItem());
            }
        });
        controls.getChildren().add(deleteLicenseLabel);
        controls.getChildren().add(new Separator(Orientation.HORIZONTAL));

        addQuota = new ControlLabel("add quota", true);
        addQuota.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                Dialogs.editQuotaDialog(licenseTable.getSelectionModel().getSelectedItem());
                if (!licenseTable.getSelectionModel().isEmpty()) {
                    refreshLicense(licenseTable.getSelectionModel().getSelectedItem());
                    qv.setQuota(licenseTable.getSelectionModel().getSelectedItem().getQuotas());
                }
            }
        });
        controls.getChildren().add(addQuota);

        editQuotaLabel = new ControlLabel("edit quota", true);
        editQuotaLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                Dialogs.editQuotaDialog(qv.getSelectedItem());
                if (!licenseTable.getSelectionModel().isEmpty()) {
                    refreshLicense(licenseTable.getSelectionModel().getSelectedItem());
                    qv.setQuota(licenseTable.getSelectionModel().getSelectedItem().getQuotas());
                }
            }
        });
        controls.getChildren().add(editQuotaLabel);

        deleteQuotaLabel = new ControlLabel("delete quota", true);
        deleteQuotaLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                deleteQuota(qv.getSelectedItem());
            }
        });
        controls.getChildren().add(deleteQuotaLabel);
    }


    private void setInfo(License l) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        idLabel.setText(l != null ? l.getId().toString() : "");
        nameLabel.setText(l != null ? l.getName() : "");
        permitNoLabel.setText(l !=  null ? l.getNumber() : "");
        startDateLabel.setText(l !=null ? l.getStartDate() != null ? sdf.format(l.getStartDate()) : "" : "");
        endDateLabel.setText(l != null ? l.getEndDate() != null ? sdf.format(l.getEndDate()) : "" : "");
        respPersonLabel.setText((l != null && l.getResponsiblePerson() != null) ? l.getResponsiblePerson().getFirstName() + " " + l.getResponsiblePerson().getLastName() : "");
        deputyPersonLabel.setText((l != null && l.getDeputy() != null) ? l.getDeputy().getFirstName() + " " + l.getDeputy().getLastName() : "");
        agencyLabel.setText((l != null && l.getAgency() != null) ? l.getAgency() : "");
    }


    private void licenseSelected(License l) {
        HashSet<Treatment> ts = new HashSet<>(0);
        setInfo(l);
        editLicenseLabel.setDisable(l == null);
        deleteLicenseLabel.setDisable(l == null);
        addQuota.setDisable(l == null);

        if (l != null) {
            qv.setLicense(l);
            typeTable.setTreatmentTypes(l.getTreatmentTypes());
            for (TreatmentType t : l.getTreatmentTypes()) {
                ts.addAll(t.getTreatments());
            }
        } else {
            qv.setLicense(null);
            typeTable.setTreatmentTypes(new ArrayList<>());
        }
        treatmentsTable.setTreatments(ts);
        timeline.setTreatments(ts);
    }

    private void quotaSelected(Quota q) {
        editQuotaLabel.setDisable(q == null);
        deleteQuotaLabel.setDisable(q == null);
    }

    private void editLicense(License l) {
        Dialogs.editLicenseDialog(l);
        licenseTable.refresh();
    }


    private void deleteLicense(License l) {
        if (l.getTreatmentTypes().size() != 0) {
            Dialogs.showInfo("Cannot delete License " + l.getName().substring(0, 20) + " since it is referenced by treatment types!");
            return;
        }
        Session session = Main.sessionFactory.openSession();
        session.beginTransaction();
        session.delete(l);
        session.getTransaction().commit();
        session.close();
        licenseSelected(null);
        licenseTable.remove(l);
    }


    private void deleteQuota(Quota q) {
        Session session = Main.sessionFactory.openSession();
        session.beginTransaction();
        session.delete(q);
        session.refresh(licenseTable.getSelectionModel().getSelectedItem());
        session.getTransaction().commit();
        session.close();
        if (!qv.removeItem(q)) {
            Dialogs.showInfo("Could not delete Quota entry (insufficient rights?): " + q.getSpeciesType().getName());
        }
    }

    @Override
    public VBox getControls() {
        return controls;
    }


    @Override
    public void refresh() {
        licenseTable.refresh();
    }

    public static Tooltip getToolTip() {
        return new Tooltip("Manage licenses, permits and respective animal quota.");
    }

    private void refreshLicense(License l) {
        EntityHelper.refreshEntity(l);
    }
}
