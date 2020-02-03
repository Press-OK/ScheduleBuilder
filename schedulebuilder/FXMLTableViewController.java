package schedulebuilder;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 * The TableView (GridPane) FXML used to display and edit Weeks.
 * 
 * @author Sean Berwick
 * @author Steve Markham
 * @author Alvin Alora
 */
public class FXMLTableViewController implements Initializable{
    
    @FXML
    private GridPane tableGrid;
    
    // Whether the user can edit the active week by clicking the grid
    private boolean isEditable = false;
    // The active week being displayed
    private Week activeWeek;
    // The 2-dimensional array of TableRects which act as buttons on the grid
    private TableRect[][] tableRects = new TableRect[5][13];
    
    // A list of colors which will be used to display weeks on the grid
    // The first value is used for "empty" timeslots
    public final int NUM_COLORS = 6;
    public final Color[] RECT_COLORS = {
            Color.LIGHTGRAY,
            Color.rgb(110, 255, 110),
            Color.rgb(255, 110, 110),
            Color.rgb(110, 110, 255),
            Color.rgb(255, 255, 0),
            Color.rgb(0, 255, 255),
            Color.rgb(255, 0, 255),
            };
    
    /**
     * JavaFX initialization method.
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Creates the grid of TableRects at runtime
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 13; j++) {
                TableRect nr = new TableRect();
                tableRects[i][j] = nr;
                nr.setID(i, j);
                nr.setHeight(32);
                nr.setWidth(104);
                nr.setFill(Color.LIGHTGREY);
                nr.setStroke(Color.BLACK);
                nr.setStrokeWidth(0.1);
                GridPane.setHalignment(nr, HPos.CENTER);
                tableGrid.add(nr, i+1, j+1);
                nr.setOnMousePressed((e) -> {
                    this.rectClicked(e);
                });
            }
        }
    }
    
    /**
     * Receives a week and sets it as the current activeWeek.
     * @param w 
     */
    public void setActiveWeek(Week w) {
        this.activeWeek = w;
        updateRects();
    }
    
    /**
     * Public method allows the external source (TreeView) to decide if the
     * activeWeek should be editable, or if it is display-only.
     * @param b 
     */
    public void setEditable(boolean b) {
        this.isEditable = b;
    }
    
    /**
     * Called when a TableRect is clicked.
     * @param e 
     */
    private void rectClicked(MouseEvent e) {
        // Make sure the clicked target is a TableRect and check if the WeekView
        // is currently editable
        if (e.getTarget() instanceof TableRect && this.isEditable) {
            int c = ((TableRect)e.getTarget()).getCol();
            int r = ((TableRect)e.getTarget()).getRow();
            // If the active week at the specified slot is 0, set to 1, change rect color
            if (activeWeek.getOne(c, r) == 0) {
                activeWeek.setOne(c, r, 1);
                ((TableRect)e.getTarget()).setFill(RECT_COLORS[1]);
            // If the active week at the specified slot is 1, set to 0, change rect color
            } else {
                activeWeek.setOne(c, r, 0);
                ((TableRect)e.getTarget()).setFill(RECT_COLORS[0]);
            }
        }
    }
    
    /**
     * Updates TableRect colors FROM the week data.
     */
    private void updateRects() {
        resetRects();
        int c;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 13; j++) {
                if (activeWeek.getOne(i,j) > NUM_COLORS) {
                    c = activeWeek.getOne(i,j)%NUM_COLORS;
                } else {
                    c = activeWeek.getOne(i,j);
                }
                tableRects[i][j].setFill(RECT_COLORS[c]);
            }
        }
    }
    
    /**
     * Resets all of the tableRects to grey (used before displaying a new week
     * to start from scratch).
     */
    private void resetRects() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 13; j++) {
                tableRects[i][j].setFill(RECT_COLORS[0]);
            }
        }
    }
}
