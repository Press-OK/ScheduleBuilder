package schedulebuilder;

import java.io.Serializable;

/**
 * The Week object which holds a 2-Dimensional array of class times. Since the
 * data is stored as Integers, it can be used to store both: 1) an individual
 * course's class times (1's and 0's), and 2) an entire timetable for the week
 * with more than one course.
 * 
 * @author Sean Berwick
 * @author Steve Markham
 * @author Alvin Alora
 */
public class Week implements DataNode, Serializable{
    
    private String weekName = "Week";
    // Holds week data
    private Integer[][] weekData = new Integer[5][13];
    
    /**
     * Constructor initializes the array to all zeros
     */
    public Week() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 13; j++) {
                weekData[i][j] = 0;
            }
        }
    }
    // is this default behavior when we make an instance? Does it happen automatically?
    
    /**
     * Returns this week's name.
     * @return 
     */
    @Override
    public String toString() {
        return this.weekName;
    }

    /**
     * Same as toString();
     * @return 
     */
    String getName() {
        return this.weekName;
    }
    
    /**
     * Sets the name of the week.
     * @param n 
     */
    public void setName(String n) {
        this.weekName = n;
    }
    
    /**
     * Set one element of the array of Integers to the provided value.
     * @param day
     * @param hour
     * @param newValue 
     */
    public void setOne(int day, int hour, Integer newValue) {
        weekData[day][hour] = newValue;
    }
    
    /**
     * Returns one Integer from the array.
     * @param day
     * @param hour
     * @return 
     */
    public Integer getOne(int day, int hour) {
        return weekData[day][hour];
    }
    
    /**
     * Replace this week's weekData array with a provided one.
     * @param newdata 
     */
    public void setAll(Integer[][] newdata) {
        this.weekData = newdata;
    }
    
    /**
     * Returns the entire array of Integers.
     * @return 
     */
    public Integer[][] getAll() {
        return weekData;
    }
    
    /**
     * This method creates a new week which is a clone of this one (deep copy)
     * and returns it.
     * @return 
     */
    public Week getCopy() {
        Week w = new Week();
        for (int d = 0; d < 5; d++) {
            for (int h = 0; h < 13; h++) {
                w.setOne(d, h, weekData[d][h]);
                w.setName("+Copy of " + this.getName());
            }
        }
        return w;
    }
    
    /**
     * Returns the longest day of this week (hours), including space between
     * classes.
     * @return 
     */
    public int getLongestDay() {
        int longest = 0;
        // for each day of the week
        for (int d = 0; d < 5; d++) {
            int subtotal = 0;
            int sectionStart = 0;
            // find first hour that != 0
            for (int h = 0; h < 13; h++) {
                if (weekData[d][h] != 0) {
                    sectionStart = h;
                    break;
                }
            }
            // loop in reverse to find the last hour that != 0
            for (int h = 12; h >= 0; h--) {
                if (weekData[d][h] != 0) {
                    // add the difference to the total
                    subtotal += h-sectionStart+1;
                    break;
                }
            }
            // if new longest day is longer than old longest day, set it to the
            // new value
            if (subtotal > longest) {
                longest = subtotal;
            }
        }
        return longest;
    }
    
    /**
     * Returns the total number of hours for this week, including time between
     * classes.
     * @return 
     */
    public int getTotalHours() {
        int totalHours = 0;
        // For each day of the week
        for (int d = 0; d < 5; d++) {
            int sectionStart = 0;
            // Find first hour that != 0
            for (int h = 0; h < 13; h++) {
                if (weekData[d][h] != 0) {
                    sectionStart = h;
                    break;
                }
            }
            // Iterate in reverse to find the last hour that != 0
            for (int h = 12; h >= 0; h--) {
                if (weekData[d][h] != 0) {
                    // Add the difference to the total
                    totalHours += h-sectionStart+1;
                    break;
                }
            }
        }
        return totalHours;
    }
    
    /**
     * Returns the number of occupied days in this week. Only a day with no classes
     * is considered unoccupied.
     * @return 
     */
    public int getTotalDays() {
        int totalDays = 0;
        for (int d = 0; d < 5; d++) {
            for (int h = 0; h < 13; h++) {
                if (weekData[d][h] != 0) {
                    totalDays += 1;
                    break;
                }
            }
        }
        return totalDays;
    }
}