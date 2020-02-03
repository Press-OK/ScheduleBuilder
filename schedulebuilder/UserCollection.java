package schedulebuilder;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class contains all the data for the current user: Their courses, and the
 * weekly options for those courses.
 * 
 * @author Sean Berwick
 * @author Steve Markham
 * @author Alvin Alora
 */
public class UserCollection implements Serializable{
    
    // These two variables are used when generating dummy data
    private final int GEN_DAYSPERWEEK = 1;
    private final int GEN_HOURSPERCLASS = 3;
    
    // Array list holds all the user's courses
    private ArrayList<Course> courses = new ArrayList<>();
    
    /**
     * Returns all courses.
     * @return 
     */
    public ArrayList<Course> getCourses() {
        return courses;
    }
    
    /**
     * Adds a provided course to the ArrayList of Courses.
     * @param c 
     */
    public void addCourse(Course c) {
        this.courses.add(c);
    }
    
    /**
     * Creates a new course, and since we can assume the next thing the user
     * wants to do is add a weekly option to that course, go ahead and do that
     * too.
     */
    public void newCourse() {
        Course c = new Course();
        Week w = new Week();
        c.setName("Course " + (courses.size()+1));
        c.addOption(w);
        w.setName("Week 1");
        courses.add(c);
    }
    
    /**
     * Adds a new empty week to the provided Course.
     * @param c 
     */
    public void newWeek(Course c) {
        Week w = new Week();
        w.setName("Week " + (c.getWeekOptions().size() + 1));
        for (Week old : c.getWeekOptions()) {
            if (old.getName() == w.getName()) {
                w.setName("Week " + (c.getWeekOptions().size() + 2));
            }
        }
        c.addOption(w);
    }
    
    /**
     * Generates a new course with 3 weekly options and adds it to the ArrayList
     * of Courses.
     */
    public void generate() {
        Course c = new Course();
        int lastDay = 99;
        int ndays = GEN_DAYSPERWEEK+(int)(Math.random()*2);
        c.setName("Course " + (courses.size()+1));
        for (int i = 0; i < 3; i++) {
            Week w = new Week();
            for (int j = 0; j < ndays; j++) {
                int day = (int)(Math.random()*5);
                while (day == lastDay) {
                    day = (int)(Math.random()*5);
                }
                lastDay = day;
                int startTime = (int)(Math.random()*(13-GEN_HOURSPERCLASS));
                for (int k = startTime; k < startTime + GEN_HOURSPERCLASS; k++) {
                    w.setOne(day, k, 1);
                }
            }
            c.addOption(w);
            w.setName("Week " + (c.getWeekOptions().size()));
        }
        courses.add(c);
    }
}
