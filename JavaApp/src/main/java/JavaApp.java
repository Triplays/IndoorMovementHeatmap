import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

// JavaScript interface object
public class JavaApp {

    public int                      time_amount = 1, time_spent = 1, radius = 40;
    public String                   point_opacity, color_mapping, object_type, parameter = "normal", time = "hours", date_from = "2019/01/1 00:00:00", date_till = "2020/02/02 00:00:00";
    public ArrayList<String>        color_mappings, point_opacity_s, object_types, devices, devices_s;
    public HeatMap                  heatMap;
    public DBHandler                dbHan;
    public HashMap<String, Integer> devicesMap;
    public HashMap<Integer, String> typesMap;
    public String                   image_url = new File("Heatmap.png").getAbsolutePath().replace('\\', '/');

    public void test() {
        System.out.printf("%d %d %d %s %s %s %s %s %s %n", time_amount, time_spent, radius, point_opacity, color_mapping, time, parameter, object_type, devices_s);
//        System.out.println(String.join(" ", time_amount, time_spent, radius, point_opacity, color_mapping, time, parameter, object_type, devices));
    }



    public JavaApp() {
        point_opacity_s = new ArrayList<>(Arrays.asList("EXPONENTIAL", "TAN_HYP", "CUSTOM", "LINEAR"));
        color_mappings = new ArrayList<>(Arrays.asList("LIME_YELLOW_RED", "BLUE_CYAN_GREEN_YELLOW_RED", "INFRARED_1", "INFRARED_2", "INFRARED_3", "INFRARED_4", "BLUE_GREEN_RED", "BLUE_BLACK_RED", "BLUE_YELLOW_RED","GREEN_BLACK_RED","GREEN_YELLOW_RED", "RAINBOW","BLACK_WHITE", "WHITE_BLACK"));
        dbHan = new DBHandler();
        typesMap = dbHan.getAllTypes();
        devicesMap = dbHan.getAllDevices();
        object_types = hashValuesToList(typesMap);
        devices = hashKeysToList(devicesMap);
        devices_s = new ArrayList<>();
        devices_s.addAll(devices);
        point_opacity = point_opacity_s.get(0).toLowerCase();
        color_mapping = color_mappings.get(0).toLowerCase();
        object_type = object_types.get(0).toLowerCase();
        heatMap = new HeatMap(400, 400); //needs to be height and width of image pane of application
        System.out.println(image_url);
    }

    public void submitParams(){
        String query = "";
        if (time.equals("dates")) {
            query = QueryBuilder.buildQuery(date_from, date_till, devices_s); // 1 and 2 need to be date_from and date_till
        } else {
            query = QueryBuilder.buildQuery(time, time_amount, devices_s);
        }
        System.out.println(query);
        ResultSet data = dbHan.getData(query);
        setHeatmapParams();
        heatMap.addDataHeatmap(data);
        heatMap.saveHeatmapImage();
    }

    private void setHeatmapParams(){
        heatMap.setOpacityDistribution(OpacityDistribution.valueOf(point_opacity.toUpperCase()));
        heatMap.setColorMapping(ColorMapping.valueOf(color_mapping.toUpperCase()));
        heatMap.setEventRadius(radius);
        heatMap.clearHeatMap();
    }

    public void print(String text) {
        System.out.println(text);
    }

    public void append(String par_name, String value) {
        switch (par_name) {
            case "devices_s":
            case "devices":
                if (!devices_s.contains(value)) {
                    devices_s.add(value);
//                    System.out.println("" + devices_s + " " + value);
                }
                break;
        }
    }

    public void remove(String par_name, String value) {
        switch (par_name) {
            case "devices_s":
            case "devices":
                devices_s.remove(value);
//                System.out.println(devices_s);
                break;
        }
    }

    public void set(String par_name, String value) {
//        System.out.println(par_name + value);
        switch (par_name) {
            case "time_amount":
                time_amount = Integer.parseInt(value);
                break;
            case "time_spent":
                time_spent = Integer.parseInt(value);
                break;
            case "radius":
                radius = Integer.parseInt(value);
                break;
            case "point_opacity":
                point_opacity = value;
                break;
            case "color_mapping":
                color_mapping = value;
                break;
            case "object_type":
                object_type = value;
                break;
            case "parameter":
                parameter = value;
                break;
            case "time":
                time = value;
                break;

        }
    }

    public String get(String par_name) {
        switch (par_name) {
            case "time_amount":
                return "" + time_amount;
            case "time_spent":
                return "" + time_spent;
            case "radius":
                return "" + radius;
            case "color_mapping":
                return get_json(color_mappings, color_mapping);
            case "point_opacity":
                return get_json(point_opacity_s, point_opacity);
            case "object_type":
                return get_json(object_types, object_type);
            case "parameter":
                return parameter;
            case "time":
                return time;
            case "devices_s":
                return get_json(devices_s);
            case "devices":
                return get_json(devices);
            case "image":
                return image_url;
        }
        return null;
    }

    /**
     * Puts all the values of a hashmap into an Arraylist
     * @param map can be any hashmap
     * @return list a list of values in the given hashmap
     */
    public ArrayList<String> hashValuesToList(HashMap map){
       ArrayList<String> list = new ArrayList<>();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            list.add(pair.getValue().toString());
            it.remove(); // avoids a ConcurrentModificationException
        }
       return list;
    }

    /**
     * Puts all the keys of a hashmap into an Arraylist
     * @param map can be any hashmap
     * @return list a list of keys in the given hashmap
     */
    public ArrayList<String> hashKeysToList(HashMap map) {
        ArrayList<String> list = new ArrayList<>();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it.next();
            list.add(pair.getKey().toString());
            it.remove(); // avoids a ConcurrentModificationException
        }
        return list;
    }

    private String get_json(ArrayList<String> list, String value) {
        StringBuilder text = new StringBuilder("[\"");
        text.append(value);
        text.append("\",");
        return createString(list, text);
    }

    private String get_json(ArrayList<String> list) {
        StringBuilder text = new StringBuilder("[");
        return createString(list, text);
    }

    private String createString(ArrayList<String> list, StringBuilder text) {
        String prefix = "";
        for (String s : list) {
            text.append(prefix);
            prefix = ",";
            text.append("\"");
            text.append(s);
            text.append("\"");
        }
        text.append("]");
        return text.toString();
    }



}
