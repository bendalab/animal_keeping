package animalkeeping.ui.forms;

import animalkeeping.ui.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
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

public class SettingsForm extends VBox implements Initializable {
    private FilterSettings filterSettings;
    private GeneralSettings generalSettings;
    private StorageSettings storageSettings;

    private Preferences appSettings;
    @FXML private ScrollPane scrollPane;
    @FXML private SplitPane splitPane;
    @FXML private TitledPane generalPane;

    public SettingsForm(Preferences appSettings) {
        this.appSettings = appSettings;
        URL url = Main.class.getResource("/animalkeeping/ui/fxml/Settings.fxml");
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filterSettings = new FilterSettings(appSettings);
        filterSettings.applySettings();
        filterSettings.prefWidthProperty().bind(scrollPane.widthProperty());

        generalSettings = new GeneralSettings(appSettings);
        generalSettings.applySettings();
        generalSettings.prefWidthProperty().bind(scrollPane.widthProperty());

        storageSettings = new StorageSettings(appSettings);
        storageSettings.applySettings();
        storageSettings.prefWidthProperty().bind(scrollPane.widthProperty());

        splitPane.prefHeightProperty().bind(this.heightProperty());
        splitPane.prefWidthProperty().bind(this.widthProperty());
        showGeneralSettings();
    }

    @FXML
    private void showFilterSettings() {
        scrollPane.setContent(filterSettings);
    }

    @FXML
    private void showGeneralSettings() {
        scrollPane.setContent(generalSettings);
    }

    @FXML
    private void showStorageSettings() {
        scrollPane.setContent(storageSettings);
    }

    public void storeSettings() {
        generalSettings.storeSettings();
        storageSettings.storeSettings();
        filterSettings.storeSettings();
    }
}
