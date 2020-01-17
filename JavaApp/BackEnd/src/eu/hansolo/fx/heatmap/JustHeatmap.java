package eu.hansolo.fx.heatmap;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
	
	@Override public void init() {
		pane						= new StackPane();
		heatMap						= new HeatMap(400, 400, ColorMapping.BLUE_CYAN_GREEN_YELLOW_RED, 40);
		heatMap.setOpacityDistribution(OpacityDistribution.CUSTOM);
    }
    
	@Override public void start(Stage stage) {
    	VBox layout = new VBox();
    	pane.getChildren().setAll(layout, heatMap);
    	this.addDemoPoints();
    	
    	Scene scene = new Scene(pane, 400, 400, Color.GRAY);
    	
    	stage.setTitle("JavaFX HeatMap Demo");
        stage.setScene(scene);
        stage.show();

        //this.saveHeatmapImage(heatMap);
        testDB();
    }        
    
    public static void main(String[] args) {
    	launch(args);
    }
    
    public void addDemoPoints() {
    	heatMap.addEvents(new Point2D(70,70), new Point2D(70,70), new Point2D(70,70), new Point2D(80,80));
    	
    	ArrayList<Point2D> List = new ArrayList<>();
    	List.add(new Point2D(2,3));
    	List.add(new Point2D(12,13));
    	heatMap.addEvents(List);
    }

    public void testDB(){
	    DBHandler dbHan = new DBHandler();
	    dbHan.dbTest();
    }
    
    public void saveHeatmapImage(HeatMap map) {
        File outputFile = new File("D:/HeatmapPNG/Heatmap.png");
        BufferedImage bImage = SwingFXUtils.fromFXImage(map.getImage(), null);
        try {
          ImageIO.write(bImage, "png", outputFile);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
    }

    public void generateHeatmap(){
	    //still need to add method here
    }
}
