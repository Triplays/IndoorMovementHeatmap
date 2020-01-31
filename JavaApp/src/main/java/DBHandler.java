import java.sql.*;
import java.util.HashMap;

public class DBHandler {
    private String url;
    private String username;
    private String password;
    private Connection con;
    public Statement stmt;

    public DBHandler(){
         this( "jdbc:mysql://localhost:3306/indoormovementheatmap?serverTimezone=CET", "root", "");
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
            con = DriverManager.getConnection(url, username, password);
            System.out.println("Database connection established");
            return con;
        } catch (Exception e){
            System.out.println("Database connection failed");
            e.printStackTrace();
        }
        return null;
    }

    public void dbTest() {
        try {
            ResultSet rs = this.stmt.executeQuery("SELECT * FROM devicetypes");
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


    /**
     * Returns hashmap of all registered types
     * @return typeMap  Key = type_id and Value = description
     */
    public HashMap<Integer, String> getAllTypes(){
        HashMap<Integer, String> typeMap = new HashMap<>();
        try {
            ResultSet types = this.stmt.executeQuery("SELECT * FROM devicetypes");
            while (types.next()) {
                typeMap.put(types.getInt(1), types.getString(2));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return typeMap;
    }

    /**
     * Returns hashmap of all registered devices
     * @return devicesMap Key = device_id and Value = device_type
     */
    public HashMap<String, Integer> getAllDevices(){
        HashMap<String, Integer> devicesMap = new HashMap<>();
        try {
            ResultSet devices = this.stmt.executeQuery("SELECT * FROM devices");
            while (devices.next()) {
                devicesMap.put(devices.getString(1), devices.getInt(2));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return devicesMap;
    }
}
