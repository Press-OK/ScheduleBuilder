package schedulebuilder;

import javafx.scene.shape.Rectangle;

/**
 * A custom Rectangle used in the TableView for each grid spot. This enables
 * each rectangle to store its own grid position locally.
 * 
 * @author Sean Berwick
 * @author Steve Markham
 * @author Alvin Alora
 */
public class TableRect extends Rectangle{
    
    // The grid position of the rectangle (set when created by the TableView)
    private int col, row;
    
    /**
     * Public method allows the instantiator to set the grid position.
     * @param c
     * @param r 
     */
    public void setID(int c, int r) {
        this.col = c;
        this.row = r;
    }
    
    /**
     * Get this instance's column.
     * @return 
     */
    public int getCol() {
        return this.col;
    }
    
    /**
     * Get this instance's row.
     * @return 
     */
    public int getRow() {
        return this.row;
    }
}
