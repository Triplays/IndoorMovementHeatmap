package eu.hansolo.fx.heatmap;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import eu.hansolo.fx.databasehandler.DBHandler;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class JustHeatmap extends Application{
	private HeatMap                        heatMap;
	private StackPane                      pane;
	private int                            xColumn;
	private int                            yColumn;
	
	@Override public void init() {
		pane						= new StackPane();
		heatMap						= new HeatMap(400, 400, ColorMapping.BLUE_CYAN_GREEN_YELLOW_RED, 40);
		heatMap.setOpacityDistribution(OpacityDistribution.CUSTOM);
		xColumn                     = 1;
		yColumn                     = 2;
    }
    
	@Override public void start(Stage stage) {
    	VBox layout = new VBox();
    	pane.getChildren().setAll(layout, heatMap);

    	Scene scene = new Scene(pane, 400, 400, Color.GRAY);
    	
    	stage.setTitle("JavaFX HeatMap Demo");
        stage.setScene(scene);
        stage.show();

        HashMap events = new HashMap(); // This is where Zi-Wen his hashmap should go
        DBHandler dbHan = new DBHandler();

        // Once everything is ready this code should add data to the heatmap from the given hashmap
        //String query = dbHan.buildQuery(events);
        //ResultSet data = dbHan.getData(query);
        //addDataHeatmap(data);

        testDB();
    }        
    
    public static void main(String[] args) {
    	launch(args);
    }
    
    // Just a test function that adds points to the heatmap
    public void addDemoPoints() {
    	heatMap.addEvents(new Point2D(70,70), new Point2D(70,70), new Point2D(70,70), new Point2D(80,80));
    	
    	ArrayList<Point2D> List = new ArrayList<>();
    	List.add(new Point2D(2,3));
    	List.add(new Point2D(12,13));
    	heatMap.addEvents(List);
    }

    // Just a database test
    public void testDB(){
	    DBHandler dbHan = new DBHandler();
	    dbHan.dbTest();
    }

    /**
     * Save a given heatmap as a png file in the output file
     * @param map
     */
    public void saveHeatmapImage(HeatMap map) {
        File outputFile = new File("D:/HeatmapPNG/Heatmap.png");
        BufferedImage bImage = SwingFXUtils.fromFXImage(map.getImage(), null);
        try {
          ImageIO.write(bImage, "png", outputFile);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
    }

    /**
     * Adds data from a sql result set into the heatmap
     * @param data
     */
    public void addDataHeatmap(ResultSet data){
	    try{
	        while(data.next()){
	            heatMap.addEvent(data.getInt(xColumn), data.getInt(yColumn));
            }
        } catch(Exception e){
            System.out.println(e);
        }
    }
}
