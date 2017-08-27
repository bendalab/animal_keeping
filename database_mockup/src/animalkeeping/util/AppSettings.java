package animalkeeping.util;

import java.util.*;
import java.util.prefs.Preferences;

/******************************************************************************
 animalBase
 animalkeeping.util

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

 * Created by jan on 25.08.17.

 *****************************************************************************/

public class AppSettings {

    private java.util.prefs.Preferences prefs;
    private MutableBool activePersonsView = new MutableBool(true);
    private MutableBool activePersonsSelection = new MutableBool(true);
    private MutableBool validLicensesView = new MutableBool(true);
    private MutableBool validLicensesSelection = new MutableBool(true);
    private MutableBool validTreatmentsView = new MutableBool(true);
    private MutableBool validTreatmentsSelection = new MutableBool(true);
    private MutableBool availableSubjectsView = new MutableBool(true);
    private MutableBool availableSubjectsSelection = new MutableBool(true);
    private HashMap<String, MutableBool> boolValues = new HashMap<>();

    public AppSettings() {
        prefs = Preferences.userNodeForPackage(AppSettings.class);
        boolValues.put("app_settings_activePersonView", activePersonsView);
        boolValues.put("app_settings_activePersonSelection", activePersonsSelection);
        boolValues.put("app_settings_validLicenseView", validLicensesView);
        boolValues.put("app_settings_validLicenseSelection", validLicensesSelection);
        boolValues.put("app_settings_validTreatmentsView", validTreatmentsView);
        boolValues.put("app_settings_validTreatmentsSelection", validLicensesSelection);
        boolValues.put("app_settings_availableSubjectsView", availableSubjectsView);
        boolValues.put("app_settings_availableSubjectsSelection", availableSubjectsSelection);
        readPreferences();
    }

    private void readPreferences() {
        for (Map.Entry<String, MutableBool> s : boolValues.entrySet()) {
            if (!prefs.get(s.getKey(), "").isEmpty()) {
                s.getValue().setValue(prefs.getBoolean(s.getKey(), true));
            }
        }
    }

    private void storePreferences() {
        for (Map.Entry<String, MutableBool> s : boolValues.entrySet()) {
            prefs.putBoolean(s.getKey(), s.getValue().getValue());
        }
    }

    public Boolean isActivePersonsView() {
        return activePersonsView.getValue();
    }

    public void setActivePersonsView(Boolean activePersonsView) {
        this.activePersonsView.setValue(activePersonsView);
    }

    public Boolean isActivePersonsSelection() {
        return activePersonsSelection.getValue();
    }

    public void setActivePersonsSelection(Boolean activePersonsSelection) {
        this.activePersonsSelection.setValue(activePersonsSelection);
    }

    public Boolean isValidLicensesView() {
        return validLicensesView.getValue();
    }

    public void setValidLicensesView(Boolean validLicensesView) {
        this.validLicensesView.setValue(validLicensesView);
    }

    public Boolean isValidLicensesSelection() {
        return validLicensesSelection.getValue();
    }

    public void setValidLicensesSelection(Boolean validLicensesSelection) {
        this.validLicensesSelection.setValue(validLicensesSelection);
    }

    public Boolean isValidTreatmentsView() {
        return validTreatmentsView.getValue();
    }

    public void setValidTreatmentsView(Boolean validTreatmentsView) {
        this.validTreatmentsView.setValue(validTreatmentsView);
    }

    public Boolean isValidTreatmentsSelection() {
        return validTreatmentsSelection.getValue();
    }

    public void setValidTreatmentsSelection(Boolean validTreatmentsSelection) {
        this.validTreatmentsSelection.setValue(validTreatmentsSelection);
    }

    public Boolean isAvailableSubjectsView() {
        return availableSubjectsView.getValue();
    }

    public void setAvailableSubjectsView(Boolean availableSubjectsView) {
        this.availableSubjectsView.setValue(availableSubjectsView);
    }

    public Boolean isAvailableSubjectsSelection() {
        return availableSubjectsSelection.getValue();
    }

    public void setGetAvailableSubjectsSelection(Boolean getAvailableSubjectsSelection) {
        this.availableSubjectsSelection.setValue(getAvailableSubjectsSelection);
    }


    protected void finalize(){
        storePreferences();
    }

    private class MutableBool {
        private Boolean value;

        MutableBool(Boolean b) {
            this.value = b;
        }

        public Boolean getValue() {
            return value;
        }

        public void setValue(Boolean value) {
            this.value = value;
        }
    }
}
