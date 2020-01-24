package main.java;

import java.util.ArrayList;
import java.util.Arrays;

// JavaScript interface object
public class JavaApp {

    public int time_amount = 1, time_spent = 1, radius = 1;
    public String point_opacity, color_mapping, object_type, parameter = "normal", time = "hours";
    public ArrayList<String> color_mappings, point_opacity_s, object_types, devices, devices_s;

    public void test() {
        System.out.printf("%d %d %d %s %s %s %s %s %s %n", time_amount, time_spent, radius, point_opacity, color_mapping, time, parameter, object_type, devices_s);
//        System.out.println(String.join(" ", time_amount, time_spent, radius, point_opacity, color_mapping, time, parameter, object_type, devices));
    }

    public JavaApp() {
        color_mappings = new ArrayList<>(Arrays.asList("Normal", "Gray"));
        point_opacity_s = new ArrayList<>(Arrays.asList("Normal", "Something"));
        object_types = new ArrayList<>(Arrays.asList("Carts", "Baskets"));
        devices = new ArrayList<>(Arrays.asList("123", "456", "789", "1234", "2345"));
        devices_s = new ArrayList<>();
        devices_s.addAll(devices);
        point_opacity = point_opacity_s.get(0).toLowerCase();
        color_mapping = color_mappings.get(0).toLowerCase();
        object_type = object_types.get(0).toLowerCase();
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
        }
        return null;
    }

    private String get_json(ArrayList<String> list, String value) {
        StringBuilder text = new StringBuilder("[\"");
        text.append(value);
        text.append("\",");
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

    private String get_json(ArrayList<String> list) {
        StringBuilder text = new StringBuilder("[");
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
