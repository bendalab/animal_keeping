package animalkeeping.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.hibernate.Hibernate;
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
        dialog.setTitle("Enter connection details");
        dialog.setResizable(true);
        Label label1 = new Label("user: ");
        Label label2 = new Label("password: ");
        TextField text1 = new TextField("huben");
        PasswordField text2 = new PasswordField();
        text2.setText("test");

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(text1, 2, 1);
        grid.add(label2, 1, 2);
        grid.add(text2, 2, 2);
        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

        dialog.setResultConverter(new Callback<ButtonType, ConnectionDetails>() {
            @Override
            public ConnectionDetails call(ButtonType b) {
                if (b == buttonTypeOk) {
                return new ConnectionDetails(text1.getText(), text2.getText());
                }
            return null;
            }
        });
		Optional<ConnectionDetails> result = dialog.showAndWait();
		if (result.isPresent()) {
		    ConnectionDetails det = result.get();
		    credentials.put("hibernate.connection.username", det.getUser());
            credentials.put("hibernate.connection.password", det.getPasswd());
        }
        return credentials;
    }

    private static class ConnectionDetails {
		private String user;
		private String passwd;

		ConnectionDetails(String s1, String s2) {
			user = s1;
			passwd = s2;
		}

        public String getUser() {
            return user;
        }

        public String getPasswd() {
            return passwd;
        }

        @Override
		public String toString() {
			return (user);
		}
	}
}
