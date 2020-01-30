import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


//This class should be just for testing
public class JustHeatmap extends Application{
	private HeatMap heatMap;
	private StackPane                      pane;
	final private static int               XCOLUMN = 1;
	final private static int               YCOLUMN = 2;
	
	@Override public void init() {
		pane						= new StackPane();
		heatMap						= new HeatMap(400, 400, ColorMapping.BLUE_CYAN_GREEN_YELLOW_RED, 40);
		heatMap.setOpacityDistribution(OpacityDistribution.CUSTOM);
    }
    
	@Override public void start(Stage stage) {
    	//VBox layout = new VBox();
    	//pane.getChildren().setAll(layout, heatMap);

    	//addDemoPoints();

    	//Scene scene = new Scene(pane, 400, 400, Color.GRAY);
    	
    	//stage.setTitle("JavaFX HeatMap Demo");
        //stage.setScene(scene);
        //stage.show();

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
	    HashMap map = dbHan.getAllTypes();
		HashMap map1 = dbHan.getAllDevices();
		System.out.println("BR532401 = "+ map.get(map1.get("BR532401")));
	    dbHan.dbTest();
    }

}
