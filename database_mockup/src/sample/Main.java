package sample;

import javafx.application.Application;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.Scene;

public class Main extends Application {
    ButtonService buttonControl;
    public static SessionFactory sessionFactory;
    private Stage primaryStage;
    private BorderPane rootLayout;


    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("AnimalKeepingDB");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("MainView.fxml"));
        System.out.println(sample.Main.class.getResource("PersonsView.fxml"));
        //rootLayout = loader.load();

        // Show the scene containing the root layout.
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);


        //primaryStage.setTitle("Database contents");
        //ArrayList<Button> buttonList = new ArrayList<>();

        //GridPaneBuilder builder = GridPaneBuilder.create();
        //builder.hgap(10);
        //builder.vgap(5);
        //GridPane root = builder.build();
        //buttonControl = new ButtonService(root, builder, primaryStage);
        //buttonControl.initialize();
        //buttonControl.addButtons();

        connectToDataBase();
  /*


        Button fish_btn = new Button("Show Fish Information");
        buttonControl.add(fish_btn);

        fish_btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {

                GridPane root = builder.build();
                buttonControl.addButtons(root, buttonControl.buttonList);
                tableFromDatabase(root,"name, species_id, source_id FROM census_subject");
                primaryStage.setScene(new Scene(root, 640, 480));

            }
        });
        buttonControl.addButtons(root, buttonControl.buttonList);
*/
        //primaryStage.setScene(new Scene(root, 640, 480));
        //tableFromDatabase(root);
        primaryStage.show();
    }

    private void connectToDataBase() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {

            sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        }
        catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy( registry );
            System.out.println("\n Exception!!!!");
            System.out.println(e.getCause());
            e.printStackTrace();
        }
    }

    public void stop() {
        if (sessionFactory != null) {
            System.out.println("close session factory");
            sessionFactory.close();
        }
        //super.stop();
        // Save file
    }


    public SessionFactory getSessionFactory() {
        return  sessionFactory;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
