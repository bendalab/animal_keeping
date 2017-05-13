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

 *****************************************************************************/
package animalkeeping.ui;

import animalkeeping.ui.controller.MainViewController;
import javafx.application.Application;
import javafx.scene.Scene;
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
    private static ConnectionDetails connectionDetails = null;

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

    public static void connectToDatabase(ConnectionDetails credentials) throws Exception {
        StandardServiceRegistryBuilder registrybuilder = new StandardServiceRegistryBuilder();
        registrybuilder.configure();
        registrybuilder.applySettings(credentials.getCredentials());

        final StandardServiceRegistry registry = registrybuilder.build();
        sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        connected = true;
        connectionDetails = credentials;
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

    public static ConnectionDetails getCredentials(){
        return connectionDetails;
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
        private String databaseName;
        private String hostUrl;

		public ConnectionDetails(String userName, String password, String host, String dbName, String hostUrl) {
			user = userName;
			passwd = password;
			hostName = host;
			databaseName = dbName;
			this.hostUrl = hostUrl;
		}

        public String getUser() {
            return user;
        }

        public String getPasswd() {
            return passwd;
        }

        public String getHostName() { return hostName; }

        public String getDatabaseName() { return databaseName; }

        public String getHostUrl() { return hostUrl; }

        public HashMap<String, String> getCredentials() {
		    HashMap<String, String> cred = new HashMap<>();
            cred.put("hibernate.connection.username", getUser());
            cred.put("hibernate.connection.password", getPasswd());
            cred.put("hibernate.connection.url", getHostUrl());
            return  cred;
		}

        @Override
		public String toString() {
			return (user);
		}
	}
}
