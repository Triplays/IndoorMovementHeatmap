package eu.hansolo.fx.databasehandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * Gets data from database using the given query and returns resultset
     * @param query
     * @return Resultset
     */
    public ResultSet getData(String query){
        try{
            Connection con = connectDb();
            Statement stmt = con.createStatement();
            ResultSet data = stmt.executeQuery(query);
            return data;
        }
        catch (Exception e){
            System.out.println(e);
        }
        return null;
    }


    /**
     * Build a query with a given hashmap of selected parameters
     * @param eventmap
     * @return query
     */
    public String buildQuery(HashMap<String, String> eventmap){
        String query = null;
        for(Map.Entry<String, String> entry : eventmap.entrySet()){
            switch (entry.getKey()){
                case "Time":
                    if (query == null){

                    } else{query = query + "";}
                case "Device":
                    if (query == null){

                    } else{query = query + "";}
            }
        }
        return query;
    }
}
