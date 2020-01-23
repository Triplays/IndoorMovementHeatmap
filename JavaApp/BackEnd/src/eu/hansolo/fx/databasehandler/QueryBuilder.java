package eu.hansolo.fx.databasehandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class QueryBuilder {
    // Probably not yet finished, but better than my last attempt
    public String buildQuery(String time, String date_from, String date_till, long time_amount, ArrayList<String> devices){
        String query = "SELECT x, y FROM positions WHERE";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        if (time.equals("dates")){ //might still change this, dont want to force the date_from and date_till
            query = String.format("%s ( %s <= positions.datetime <= %s )", query, date_from, date_till);
        }
        else{
            LocalDateTime now = LocalDateTime.now();
            date_till = dtf.format(now);
            date_from = null;
            if (time.equals("minutes")){
                date_from = dtf.format(now.minusMinutes(time_amount));
            }
            else if(time.equals("hours")){
                date_from = dtf.format(now.minusHours(time_amount));
            }
            else if (time.equals("days")){
                date_from = dtf.format(now.minusDays(time_amount));
            }
            else if (time.equals("weeks")){
                date_from = dtf.format(now.minusWeeks(time_amount));
            }
            else if (time.equals("months")){
                date_from = dtf.format(now.minusMonths(time_amount));
            }
            else{
                date_from = dtf.format(now.minusYears(time_amount));
            }
            query = String.format("%s ( %s <= positions.datetime <= %s )", query, date_from, date_till);
        }

        // Iterates over every device in devices and adds them to the where clause
        String devicesClause = null;
        for(String device : devices){
            if (devicesClause == null){
                devicesClause = String.format("device_id = %s",device);
            }
            else{
                devicesClause = String.format("%s OR device_id = %s",devicesClause, device);
            }
        }

        query = String.format("%s AND ( %s );", query, devicesClause);

        return query;
    }

    private String buildDateQuery(String date_till, String date_from){
        return String.format("%s ( %s <= positions.datetime <= %s )", query, date_from, date_till);
    }

    private String buildDateClause(String time, long time_amount){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String date_till = dtf.format(now);
        String date_from = null;

        if (time.equals("minutes")){
            date_from = dtf.format(now.minusMinutes(time_amount));
        }
        else if(time.equals("hours")){
            date_from = dtf.format(now.minusHours(time_amount));
        }
        else if (time.equals("days")){
            date_from = dtf.format(now.minusDays(time_amount));
        }
        else if (time.equals("weeks")){
            date_from = dtf.format(now.minusWeeks(time_amount));
        }
        else if (time.equals("months")){
            date_from = dtf.format(now.minusMonths(time_amount));
        }
        else{
            date_from = dtf.format(now.minusYears(time_amount));
        }
        return String.format(" ( %s <= positions.datetime <= %s )", date_from, date_till);
    }

    private String buildDevicesClause(ArrayList<String> devices){
        //still need to write this
        return "";

    }

}
