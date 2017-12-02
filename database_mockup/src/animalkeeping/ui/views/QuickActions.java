package animalkeeping.ui.views;

import animalkeeping.ui.Main;
import animalkeeping.util.Dialogs;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/******************************************************************************
 animalBase
 animalkeeping.ui.views

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

 * Created by jan on 20.08.17.

 *****************************************************************************/

public class QuickActions extends AbstractView implements Initializable {


    public QuickActions() {
        URL url = Main.class.getResource("/animalkeeping/ui/fxml/QuickActions.fxml");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(url);
        loader.setController(this);
        try {
            this.getChildren().add(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void showSubjectView() {
        Main.getMainView().showView("subject", true);
    }

    @FXML
    protected void newSubject() {
        Dialogs.editSubjectDialog(null);
    }

    @FXML
    protected void importSubjects() {
        Dialogs.importSubjectsDialog(null);
    }

    @FXML
    protected void showHousingView() {
        Main.getMainView().showView("housing", true);
    }

    @FXML
    protected void showTreatmentView() {
        Main.getMainView().showView("treatment", true);
    }

    @FXML
    protected void newTreatment() {
        Dialogs.editTreatmentDialog();
    }

    @FXML
    protected void newTreatmentType() {
        Dialogs.editTreatmentTypeDialog(null);
    }

    @FXML
    protected void newBatchTreatment() {
        Dialogs.batchTreatmentDialog(null);
    }

    @FXML
    protected void reportDead() {
        Dialogs.reportSubjectDead(null);
    }


    @FXML
    protected void moveSubject() {
        Dialogs.relocateSubjectDialog(null);
    }

    @FXML
    protected void showLicenseView() {
        Main.getMainView().showView("license", true);
    }

    @FXML
    protected void showInventoryView() {
        Main.getMainView().showView("inventory", true);
    }

    @FXML
    protected void newLicense() {
        Dialogs.editLicenseDialog(null);
    }

    @FXML
    protected void newQuota() {
        Dialogs.editQuotaDialog(null, null);
    }

    @FXML
    protected void exportStockList() {Main.getMainView().exportStockList();}

    @Override
    public VBox getControls() {
        return null;
    }

    @Override
    public void refresh() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
