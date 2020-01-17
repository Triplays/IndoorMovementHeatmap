package eu.hansolo.fx.databasehandler;

import javax.sound.midi.Soundbank;
import java.sql.*;

public class DBHandler {
    private String url;
    private String username;
    private String password;

    public DBHandler(){
         this( "jdbc:mysql://172.21.1.69:3306/indoormovementheatmap", "collin", "henkdetank");
    }

    public DBHandler(String url, String username, String password){
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Create database connection and return it
     * @return con
     */
    public Connection connectDb(){
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection(url, username, password);
            return con;
        } catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    public void dbTest() {
        try {
            Connection con = connectDb();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from devicetypes");
            while (rs.next())
                System.out.println(rs.getInt(1) + " " + rs.getString(2)); //should still change this aswell. dk what values it will return
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
