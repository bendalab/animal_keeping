package animalkeeping.ui;

import animalkeeping.ui.controller.MainViewController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import java.util.HashMap;

public class Main extends Application {
    public static SessionFactory sessionFactory;
    private static Boolean connected = false;
    private static Stage primaryStage;


    @Override
    public void start(Stage primaryStage) throws Exception{
        Main.primaryStage = primaryStage;
        Main.primaryStage.setTitle("AnimalKeepingDB");
        MainViewController mainView = new MainViewController();
        mainView.prefHeightProperty().bind(primaryStage.heightProperty());
        mainView.prefWidthProperty().bind(primaryStage.widthProperty());
        Scene scene = new Scene(mainView);
        primaryStage.setScene(scene);
        primaryStage.setWidth(1024);
        primaryStage.setHeight(768);
        primaryStage.show();
    }

    public static  boolean connectToDatabase(ConnectionDetails credentials) throws Exception {
        StandardServiceRegistryBuilder registrybuilder = new StandardServiceRegistryBuilder();
        registrybuilder.configure();
        registrybuilder.applySettings(credentials.getCredentials());

        final StandardServiceRegistry registry = registrybuilder.build();
        sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        connected = true;
        return true;
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


    public static Boolean isConnected() {
        return connected;
    }

    public static class ConnectionDetails {
		private String user;
		private String passwd;
		private String hostName;

		public ConnectionDetails(String userName, String password, String host) {
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

        public HashMap<String, String> getCredentials() {
		    HashMap<String, String> cred = new HashMap<>();
            cred.put("hibernate.connection.username", getUser());
            cred.put("hibernate.connection.password", getPasswd());
            cred.put("hibernate.connection.url", getHostName());
            return  cred;
		}

        @Override
		public String toString() {
			return (user);
		}
	}
}
