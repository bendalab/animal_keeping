package animalkeeping.ui.forms;

import animalkeeping.ui.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/******************************************************************************
 animalBase
 animalkeeping.ui.forms

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

 * Created by jan on 27.08.17.

 *****************************************************************************/

public class FilterSettings extends VBox {
    private Preferences settings;
    private HashMap<String, CheckBox> boolValues = new HashMap<>();

    @FXML private CheckBox activePersonsView;
    @FXML private CheckBox activePersonsSelection;
    @FXML private CheckBox availableSubjectsView;
    @FXML private CheckBox availableSubjectsSelection;
    @FXML private CheckBox validLicensesView;
    @FXML private CheckBox validLicensesSelection;
    @FXML private CheckBox validTreatmentsView;
    @FXML private CheckBox validTreatmentsSelection;


    public FilterSettings(Preferences settings) {
        this.settings = settings;
        URL url = Main.class.getResource("/animalkeeping/ui/fxml/FilterSettings.fxml");
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

    public void applySettings() {
        boolValues.put("app_settings_activePersonView", activePersonsView);
        boolValues.put("app_settings_activePersonSelection", activePersonsSelection);
        boolValues.put("app_settings_validLicenseView", validLicensesView);
        boolValues.put("app_settings_validLicenseSelection", validLicensesSelection);
        boolValues.put("app_settings_validTreatmentsView", validTreatmentsView);
        boolValues.put("app_settings_validTreatmentsSelection", validTreatmentsSelection);
        boolValues.put("app_settings_availableSubjectsView", availableSubjectsView);
        boolValues.put("app_settings_availableSubjectsSelection", availableSubjectsSelection);

        for (Map.Entry<String, CheckBox> s : boolValues.entrySet()) {
            if (!settings.get(s.getKey(), "").isEmpty()) {
                s.getValue().setSelected(settings.getBoolean(s.getKey(), true));
            }
        }
    }

    void storeSettings() {
        for (Map.Entry<String, CheckBox> s : boolValues.entrySet()) {
            settings.putBoolean(s.getKey(), s.getValue().isSelected());
        }
    }

}
