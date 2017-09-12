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

 * Created by jan on 25.01.17.

 *****************************************************************************/
package animalkeeping.ui.forms;

import animalkeeping.ui.Main;
import animalkeeping.util.Dialogs;
import animalkeeping.util.Version;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class LoginController extends FlowPane implements Initializable{
    @FXML private TextField databaseField;
    @FXML private TextField userField;
    @FXML private TextField hostField;
    @FXML private PasswordField passwordField;
    @FXML private Button connectBtn;
    private Preferences prefs;


    public LoginController() {
        this.setAlignment(Pos.CENTER);
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/animalkeeping/ui/fxml/Login.fxml"));
        loader.setController(this);
        try {
            this.getChildren().add(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prefs = Preferences.userNodeForPackage(this.getClass());

        String ID1 = "db_user";
        String ID2 = "db_host";
        String ID3 = "db_name";
        String db_name = prefs.get(ID3, "animal_keeping");
        String user = prefs.get(ID1, "");
        String host = prefs.get(ID2, "localhost");
        this.databaseField.setText(db_name);
        this.hostField.setText(host);
        this.userField.setText(user);
        this.connectBtn.setDisable(true);
    }

    @FXML
    private void connect() {
        ConnectionWorker worker = new ConnectionWorker(prefs);
        worker.addEventHandler(DatabaseEvent.ANY, this::handleEvents);
        worker.setOnFailed(event -> {fireEvent(new DatabaseEvent(DatabaseEvent.FAILED,
                worker.exceptionProperty().getValue().getCause().getCause().getMessage()));
                worker.exceptionProperty().getValue().printStackTrace();
        });
        new Thread(worker).start();

    }

    private void handleEvents(Event event) {
        if (event.getEventType() == WorkerStateEvent.WORKER_STATE_SCHEDULED) {
            fireEvent(new DatabaseEvent(DatabaseEvent.CONNECTING));
        } else if (event.getEventType() == WorkerStateEvent.WORKER_STATE_SUCCEEDED) {
            fireEvent(new DatabaseEvent(DatabaseEvent.CONNECTED));
            checkVersion();
        }
    }

    @FXML
    private void passwordEntered() {
        connectBtn.setDisable(passwordField.getText().isEmpty());
    }

    private void checkVersion() {
        Version version = new Version();
        if (!version.getAvailable()) {
            Dialogs.showInfo("Version information could not be found. Some things may not work properly...");
        }
        int migrationState = version.checkMigrationState();
        if (migrationState != 0) {
            String message = migrationState < 0 ? "App migration state behind database state." : "Database migration behind app migration state!";
            Dialogs.showInfo("Migration mismatch! " + message + "\nSome things may not work properly.\n" +
            "\n\tApp migration state: " + version.getMigrationState() + "\n\tDatabase migration state: " + version.getDatabaseMigrationState() );
        }
    }

    class ConnectionWorker extends Task<Void> {
        private Preferences prefs;

        ConnectionWorker(Preferences preferences) {
            super();
            this.prefs = preferences;
        }

        @Override
        protected Void call() throws Exception {
            String host = "jdbc:mysql://" + hostField.getText() + "/" + databaseField.getText() + "?serverTimezone=UTC";
            Main.ConnectionDetails credentials = new Main.ConnectionDetails(userField.getText(), passwordField.getText(),
                    hostField.getText(), databaseField.getText(), host);
            Main.connectToDatabase(credentials);
            prefs.put("db_name", databaseField.getText());
            prefs.put("db_user", userField.getText());
            prefs.put("db_host", hostField.getText());
            return null;
        }
    }

    public static class DatabaseEvent extends Event {
        private static final long serialVersionUID = 20121107L;
        private String message = "";
        static final EventType<DatabaseEvent> DATABASE_ALL = new EventType<>("DATABASE all");
        public static final EventType<DatabaseEvent> CONNECTING = new EventType<>(DATABASE_ALL, "Connecting");
        public static final EventType<DatabaseEvent> CONNECTED = new EventType<>(DATABASE_ALL, "Connected");
        public static final EventType<DatabaseEvent> FAILED = new EventType<>(DATABASE_ALL, "failed");

        DatabaseEvent(EventType<DatabaseEvent> type) {
            super(type);
        }

        DatabaseEvent(EventType<DatabaseEvent> type, String message) {
            this(type);
            this.message = message;
        }
        @Override
        public DatabaseEvent copyFor(Object newSource, EventTarget newTarget) {
            return (DatabaseEvent) super.copyFor(newSource, newTarget);
        }

        @Override
        public EventType<? extends DatabaseEvent> getEventType() {
            return (EventType<? extends DatabaseEvent>) super.getEventType();
        }

        public String getMessage() {
            return message;
        }

    }

}
