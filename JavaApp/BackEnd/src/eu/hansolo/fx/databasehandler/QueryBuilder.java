package eu.hansolo.fx.databasehandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class QueryBuilder {
    final private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    /**
     * Builds the full query required for the data retrieval
     * @param date_from
     * @param date_till
     * @param devices
     * @return
     */
    public String buildQuery(String date_from, String date_till, ArrayList<String> devices){
        String dateClause = buildDateClause(date_from, date_till);
        String devicesClause = buildDevicesClause(devices);
        return String.format("SELECT x, y FROM positions WHERE %s AND %s ;", dateClause, devicesClause);
    }

    /**
     * Builds the full query required for the data retrieval
     * @param time
     * @param time_amount
     * @param devices
     * @return
     */
    public String buildQuery(String time, long time_amount, ArrayList<String> devices){
        String dateClause = buildDateClause(time, time_amount);
        String devicesClause = buildDevicesClause(devices);
        return String.format("SELECT x, y FROM positions WHERE %s AND %s ;", dateClause, devicesClause );
    }

    /**
     * Builds a part of a where clause
     * This is specifically to select data points from a certain datetime till a certain datetime
     * @param date_from Start date
     * @param date_till End date
     * @return Part of where clause query
     */
    private String buildDateClause(String date_from, String date_till){
        return String.format("( %s <= positions.datetime <= %s )", date_from, date_till);
    }

    /**
     * Builds a part of a where clause
     * This is specifically to select data points from the current datetime till an amount of time back
     * @param time param which defines the unit of time_amount
     * @param time_amount amount of a unit that you want to look back in time
     * @return Part of where clause query
     */
    private String buildDateClause(String time, long time_amount){
        LocalDateTime now = LocalDateTime.now();
        String date_till = this.dtf.format(now);
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

    /**
     * Builds a part of a where clause
     * Here we create a part of the query which selects data that is linked to the selected devices
     * @param devices an Arraylist of given selected devices
     * @return
     */
    private String buildDevicesClause(ArrayList<String> devices){
        String devicesClause = null;
        for(String device : devices){
            if (devicesClause == null){
                devicesClause = String.format("device_id = %s",device);
            }
            else{
                devicesClause = String.format("%s OR device_id = %s",devicesClause, device);
            }
        }
        return String.format("(%s)",devicesClause);
    }
}
