package eu.hansolo.fx.databasehandler;

import java.sql.*;
import java.util.HashMap;

public class DBHandler {
    private String url;
    private String username;
    private String password;
    private Connection con;
    private Statement stmt;

    public DBHandler(){
         this( "jdbc:mysql://172.21.1.69:3306/indoormovementheatmap", "collin", "henkdetank");
    }

    public DBHandler(String url, String username, String password){
        this.url = url;
        this.username = username;
        this.password = password;
        this.con = connectDb();

        try {
            this.stmt = this.con.createStatement();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Create database connection and return it
     * @return con
     */
    private Connection connectDb(){
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(url, username, password);
            return con;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void dbTest() {
        try {
            ResultSet rs = this.stmt.executeQuery("select * from device_types");
            while (rs.next())
                System.out.println(rs.getInt(1) + " " + rs.getString(2)); //should still change this aswell. dk what values it will return
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets data from database using the given query and returns resultset
     * @param query This can be any kind of query that fits the database that you are connected to
     */
    public ResultSet getData(String query){
        try{
            return this.stmt.executeQuery(query);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    // Im probably gonna move these two method
    /**
     * Returns hashmap of all registered types
     * @return typeMap  Key = type_id and Value = description
     * @throws SQLException
     */
    public HashMap<Integer, String> getAllTypes() throws SQLException {
        HashMap<Integer, String> typeMap = new HashMap<>();
        ResultSet types = this.stmt.executeQuery("SELECT * FROM device_types");
        while(types.next()){
            typeMap.put(types.getInt(1), types.getString(2));
        }
        return typeMap;
    }

    /**
     * Returns hashmap of all registered devices
     * @return devicesMap Key = device_id and Value = device_type
     * @throws SQLException
     */
    public HashMap<String, Integer> getAllDevices() throws SQLException{
        HashMap<String, Integer> devicesMap = new HashMap<>();
        ResultSet devices = this.stmt.executeQuery("SELECT * FROM devices");
        while(devices.next()){
            devicesMap.put(devices.getString(1), devices.getInt(2));
        }
        return devicesMap;
    }
}
