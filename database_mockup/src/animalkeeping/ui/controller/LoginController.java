package animalkeeping.ui.controller;

import animalkeeping.ui.Main;
import animalkeeping.util.Dialogs;
import animalkeeping.util.Version;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
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

import javax.swing.event.DocumentEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/**
 * Created by jan on 25.01.17.
 */

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
        prefs = Preferences.userRoot().node(Main.class.getName());
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
        worker.setOnFailed(event -> fireEvent(new DatabaseEvent(DatabaseEvent.FAILED,
                worker.exceptionProperty().getValue().getCause().getCause().getMessage())));
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
            if (Main.connectToDatabase(credentials)) {
                prefs.put("db_name", databaseField.getText());
                prefs.put("db_user", userField.getText());
                prefs.put("db_host", hostField.getText());
            }
            return null;
        }
    }

    static class DatabaseEvent extends Event {
        private static final long serialVersionUID = 20121107L;
        private String message = "";
        static final EventType<DatabaseEvent> DATABASE_ALL = new EventType<>("DATABASE all");
        static final EventType<DatabaseEvent> CONNECTING = new EventType<>(DATABASE_ALL, "Connecting");
        static final EventType<DatabaseEvent> CONNECTED = new EventType<>(DATABASE_ALL, "Connected");
        static final EventType<DatabaseEvent> FAILED = new EventType<>(DATABASE_ALL, "failed");

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
