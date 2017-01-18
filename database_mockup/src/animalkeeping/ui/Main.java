package animalkeeping.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.HashMap;
import java.util.Optional;


public class Main extends Application {
    public static SessionFactory sessionFactory;
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Main.primaryStage = primaryStage;
        Main.primaryStage.setTitle("AnimalKeepingDB");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("fxml/MainView.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        //connectToDatabase();
        primaryStage.show();
    }

    public static void connectToDatabase() {
        HashMap<String, String> properties = getCredentials();
        /*
        HashMap<String, String> properties = new HashMap<>();
        properties.put("hibernate.connection.username", "huben");
        properties.put("hibernate.connection.password", "test");
        */
        /*
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        */
        StandardServiceRegistryBuilder registrybuilder = new StandardServiceRegistryBuilder();
        registrybuilder.configure();
        registrybuilder.applySettings(properties);
        final StandardServiceRegistry registry = registrybuilder.build();

        try {
            sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        }
        catch (Exception e) {
            StandardServiceRegistryBuilder.destroy( registry );
            Alert alert = new Alert(Alert.AlertType.ERROR);
        	alert.setTitle("Connection error!");
            alert.setHeaderText("Cannot establish connection to database!");
        	String s = e.getLocalizedMessage();
        	alert.setContentText(s);
        	alert.show();
            e.printStackTrace();
        }
    }

    public void stop() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    public static Stage getPrimaryStage(){return primaryStage;}

    public SessionFactory getSessionFactory() {
        return  sessionFactory;
    }


    public static void main(String[] args) {
        launch(args);
    }

    public static HashMap<String, String> getCredentials() {
        HashMap<String, String> credentials = new HashMap<>();
        Dialog<ConnectionDetails> dialog = new Dialog<>();
        dialog.setWidth(600);
        dialog.setTitle("Enter connection details");
        dialog.setResizable(true);

        Label label1 = new Label("user: ");
        Label label2 = new Label("password: ");
        Label label3 = new Label( "hostName:");

        TextField nameField = new TextField("huben");
        PasswordField passwordField = new PasswordField();
        passwordField.setText("test");
        TextField hostField = new TextField("jdbc:mysql://localhost/animal_keeping");

        GridPane grid = new GridPane();
        grid.add(label3, 1, 1);
        grid.add(hostField, 2, 1);

        grid.add(label1, 1, 3);
        grid.add(nameField, 2, 3);

        grid.add(label2, 1, 4);
        grid.add(passwordField, 2, 4);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

        dialog.setResultConverter(new Callback<ButtonType, ConnectionDetails>() {
            @Override
            public ConnectionDetails call(ButtonType b) {
                if (b == buttonTypeOk) {
                return new ConnectionDetails(nameField.getText(), passwordField.getText(),
                                             hostField.getText());
                }
            return null;
            }
        });
		Optional<ConnectionDetails> result = dialog.showAndWait();
		if (result.isPresent()) {
		    ConnectionDetails det = result.get();
		    credentials.put("hibernate.connection.username", det.getUser());
            credentials.put("hibernate.connection.password", det.getPasswd());
            credentials.put("hibernate.connection.url", det.getHostName());
        }
        return credentials;
    }

    private static class ConnectionDetails {
		private String user;
		private String passwd;
		private String hostName;

		ConnectionDetails(String userName, String password, String host) {
			user = userName;
			passwd = password;
			hostName = host;
		}

        public String getUser() {
            return user;
        }

        public String getPasswd() {
            return passwd;
        }

        public String getHostName() { return hostName; }

        @Override
		public String toString() {
			return (user);
		}
	}
}
