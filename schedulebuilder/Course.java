package schedulebuilder;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This object represents one Course that a student might have. The course
 * might offer a number of different options for weekly classes.
 * 
 * @author Sean Berwick
 * @author Steve Markham
 * @author Alvin Alora
 */
public class Course implements DataNode, Serializable {
    
    //course name
    private String courseName;
    //arrayList of Week objects representing potential options (composition relationship)
    private ArrayList<Week> weekOptions = new ArrayList<>();
    
    /**
     * sets the name of this course
     * @param n 
     */
    public void setName(String n) {
        this.courseName = n;
    }
    
    /**
     * Returns the name of the course. This is required in addition to getName() because
     * some JavaFX controls will internally assume their contents have this method.
     * ie. The ListView control uses the toString() method to name each entry.
     * @return 
     */
    @Override
    public String toString() {
        return this.courseName;
    }
    
    /**
     * returns the name of this course
     * @return 
     */
    public String getName() {
        return this.courseName;
    }
    
    /**
     * returns a deep copy of this course
     * @return 
     */
    public Course getCopy() {
        Course newCourse = new Course();
        for (Week w : this.weekOptions) {
            Week weekCopy = w.getCopy();
            newCourse.addOption(weekCopy);
        }
        return newCourse;
    }
    
    /**
     * returns all of the weekly options for this course
     * @return 
     */
    public ArrayList<Week> getWeekOptions() {
        return weekOptions;
    }
    
    /**
     * adds a week option 'w' to this course's ArrayList of options
     * @param w 
     */
    public void addOption(Week w) {
        weekOptions.add(w);
    }
    
    /**
     * replaces a week option 'w' in the ArrayList at the specified index
     * @param index
     * @param w 
     */
    public void replaceOption(int index, Week w) {
        weekOptions.set(index, w);
    }
}
