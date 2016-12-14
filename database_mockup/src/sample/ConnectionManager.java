package sample;

import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by huben on 14.12.16.
 */
public class ConnectionManager {
    String url = "jdbc:mysql://localhost:3306/animal_keeping";
    String userid = "huben";
    String password = "test";


    public void tableFromDatabase(GridPane grid, String select_what) {
        ArrayList columnNames = new ArrayList();
        ArrayList data = new ArrayList();

        //  Connect to an MySQL Database, run query, get result set
        String sql = "SELECT " + select_what;

        try (Connection connection = DriverManager.getConnection(url, userid, password);
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();

            //  Get column names
            for (int i = 1; i <= columns; i++) {
                columnNames.add(md.getColumnName(i));
            }
            while (rs.next()) {
                ArrayList row = new ArrayList(columns);

                for (int i = 1; i <= columns; i++) {
                    row.add(rs.getObject(i));
                }

                data.add(row);
            }
            Text scenetitle = new Text("Welcome");
            grid.add(scenetitle, 1, 0, columns, 1);

            for (int i = 0; i < columns; i++) {
                Text heading = new Text((String) columnNames.get(i));
                grid.add(heading, i+2, 2);

            }

            for (int i = 0; i < data.size(); i++){
                for (int j = 0; j < columns; j++){
                    ArrayList currentRow = (ArrayList) data.get(i);
                    Text buff = new Text((String) currentRow.get(j).toString());
                    grid.add(buff, j+2, i+3);
                }

            }



            Text userName = new Text("Hallo, " + userid);
            grid.add(userName, 1, 1);


        }
        catch (SQLException e)
        {
            System.out.println( e.getMessage() );
        }
    }

    public int getCount(String count_what, String count_where){
        String sql = "SELECT COUNT(DISTINCT " + count_what +") FROM " + count_where;

        try (Connection connection = DriverManager.getConnection(url, userid, password);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();
        }
        catch (SQLException e)
        {
            System.out.println( e.getMessage() );
        }
    }

}
