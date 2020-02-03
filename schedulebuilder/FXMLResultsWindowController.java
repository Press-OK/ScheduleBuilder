package schedulebuilder;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
/**
 * This is the "pop-up" window which displays the final timetables.
 * 
 * @author Sean Berwick
 * @author Steve Markham
 * @author Alvin Alora
 */
public class FXMLResultsWindowController implements Initializable {

    // Filter controls
    @FXML
    private CheckBox cbMonday, cbTuesday, cbWednesday, cbThursday, cbFriday;
    @FXML
    private Slider sliderHours, sliderDays;
    @FXML
    private RadioButton radioHours;
    @FXML
    private ListView resultList;
    // Reference to the main window so we can add the TableView dynamically
    @FXML
    private VBox parentVBox;
    @FXML
    private HBox legendHBox;
    
    // TableView controller
    private FXMLTableViewController table;
    // Single-instance UserCollection which is given from the main window
    private UserCollection ucol;
    // Holds all valid timetables (timetables with no schedule conflicts are
    // considered valid)
    private ArrayList<Week> validTimetables = new ArrayList<>();
    
    /**
     * JavaFX initialization.
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Add the TableView
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLTableView.fxml"));
            Parent tablepane = loader.load();
            table = loader.getController();
            parentVBox.getChildren().add(0,tablepane);
        } catch (IOException ex) {
        }
        // When the ListView "resultList" is selected, call this method
        resultList.getSelectionModel().selectedItemProperty().addListener((e) -> {
            setTableToSelection();
        });
        legendHBox.setAlignment(Pos.CENTER);
    }
    
    /**
     * Public method which is used to receive the deep-copy of the UserCollection.
     * @param u 
     */
    public void setCollection(UserCollection u) {
        this.ucol = u;
        // Build and display the legend under the table
        for (Course c : ucol.getCourses()) {
            
            // Create a new Text control for each course under the TableView
            Text newLegend = new Text(c.getName());
            newLegend.setFont(Font.font(null, FontWeight.BOLD, 18));
            int clr = 0;
            if (ucol.getCourses().indexOf(c)+1 > table.NUM_COLORS) {
                clr = (ucol.getCourses().indexOf(c)+1)%table.NUM_COLORS;
            } else {
                clr = ucol.getCourses().indexOf(c)+1;
            }
            newLegend.setStyle("-fx-text-alignment: center; -fx-stroke: black; -fx-stroke-width: 0.5px;");
            newLegend.applyCss();
            newLegend.setFill(table.RECT_COLORS[clr]);
            DropShadow ds = new DropShadow();
            ds.setOffsetY(3.0f);
            ds.setColor(Color.color(0, 0, 0));
            newLegend.setEffect(ds);
            legendHBox.getChildren().add(newLegend);
            
            // This is a hack solution because the Text control cannot have a
            // padding set (?) so we create an empty label to use as an Inset-
            // type of thing between each legend Text.
            Label spaceBuffer = new Label();
            spaceBuffer.setPrefWidth(20);
            spaceBuffer.setMinWidth(20);
            spaceBuffer.setPrefHeight(50);
            spaceBuffer.setMinHeight(50);
            legendHBox.getChildren().add(spaceBuffer);
        }
        // Process the new collection into an arrayList of weeks that do not
        // have schedule conflicts
        processUserCollection();
        // Update the display list
        filterAndUpdateList();
    }

    /**
     * When we receive the UserCollection, the Courses contain Weeks, but we do
     * not yet have a full weekly timetable. This method iterates through every
     * combination of the Courses and their Weeks, and creates an ArrayList of
     * NEW weeks that are built based on valid combinations only (so, it excludes
     * any schedule conflicts). This processing is done BEFORE any filtration.
     */
    private void processUserCollection() {
        validTimetables.add(new Week());
        for (int i = 0; i < ucol.getCourses().size(); i++) {
            // Empty the ArrayList of temp valid weeks from previous iteration
            ArrayList<Week> tempTimetables = new ArrayList<>();
            // Migrate finalArrayList to the tempArrayList
            for (Week finalWeek : validTimetables) {
                tempTimetables.add(finalWeek.getCopy());
            }
            // Empty ArrayList of final valid weeks
            validTimetables = new ArrayList<>();
            
            // this gives us a new empty ArrayList (final) to add new valid weeks to,
            // and gives us previous valid weeks (now in temp) to compare to.
            //----- For each weekOption in Course
            for (int potentialValid = 0; potentialValid < ucol.getCourses().get(i).getWeekOptions().size(); potentialValid++) {
                //----- For each previously valid week in the temp ArrayList
                for (int previousValid = 0; previousValid < tempTimetables.size(); previousValid++) {
                    boolean isValidWeek = true;
                    // For each day D
                    for (int d = 0; d < 5; d++) {
                        // For each hour in day
                        for (int h = 0; h < 13; h++) {
                            // For every value in each array that we are comparing, if both are != 0
                            // (if the two arrays have a conflicting timeslot) then set isValidWeek = false;
                            if (ucol.getCourses().get(i).getWeekOptions().get(potentialValid).getOne(d, h) != 0 &&
                                    tempTimetables.get(previousValid).getOne(d, h) != 0) {
                                isValidWeek = false;
                            }
                        }
                    }
                    // If there are no conflicts, build a new array which contains data from both
                    // Then add that week to the ArrayList of finalized weeks. In the next iteration,
                    // it will move into the temp valid list for further comparison
                    if (isValidWeek) {
                        Week tmpOutputWeek = new Week();
                        for (int d = 0; d < 5; d++) {
                            for (int h = 0; h < 13; h++) {
                                if (ucol.getCourses().get(i).getWeekOptions().get(potentialValid).getOne(d, h) != 0) {
                                    tmpOutputWeek.setOne(d, h, i+1);
                                }
                                if (tempTimetables.get(previousValid).getOne(d, h) != 0) {
                                    tmpOutputWeek.setOne(d, h, tempTimetables.get(previousValid).getOne(d, h));
                                }
                            }
                        }
                        tmpOutputWeek.setName("Week " + (validTimetables.size()+1));
                        validTimetables.add(tmpOutputWeek);
                    }
                }
            }
        }
    }
    
    /**
     * A rather large and messy method which generally works like this:
     * [All possible timetables] -> -Filters- -> [Filtered timetables] -> {List}
     * Basically, filters the timetables based on the settings and updates the
     * list which the user can then click to view their timetables.
     */
    @FXML
    private void filterAndUpdateList() {
        resultList.getItems().clear();
        boolean[] dayFilters = {
            cbMonday.isSelected(),
            cbTuesday.isSelected(),
            cbWednesday.isSelected(),
            cbThursday.isSelected(),
            cbFriday.isSelected()};
        ArrayList<Week> tempList = new ArrayList<>();
        ArrayList<Week> sortedList = new ArrayList<>();
        for (Week w : validTimetables) {
            // Initially assume the week meets the filter requirements, then check:
            boolean includeInResults = true;
            // Check if the week has classes on a day that the user does not want
            for (int d = 0; d < 5; d++) {
                if (!dayFilters[d]) {
                    for (int h = 0; h < 13; h++) {
                        if (w.getOne(d, h) != 0) {
                            includeInResults = false;
                        }
                    }
                }
            }
            // Check if the week has more hours than the user wants (including time between classes)
            if (w.getLongestDay() > sliderHours.getValue()) {
                includeInResults = false;
            }
            // Check if the week has more total days than the user wants
            if (w.getTotalDays() > sliderDays.getValue()) {
                includeInResults = false;
            }
            // If the week survived all the filters, add it to the sorting list
            if (includeInResults) {
                tempList.add(w);
            }
        }
        // Sort the tempList into the sortedList
        // By fewest hours if radiobutton Hours is selected:
        int c = 0;
        if (radioHours.isSelected()) {
            for (Week w : tempList) {
                c+=1;
                w.setName("Timetable " + c + "            Hours: " + w.getTotalHours() + "            Days: " + w.getTotalDays());
                if (sortedList.isEmpty()) {
                    sortedList.add(w);
                } else {
                    int higherIndex = sortedList.size();
                    for (Week o : sortedList) {
                        if (w.getTotalHours() < o.getTotalHours()) {
                            higherIndex = sortedList.indexOf(o);
                            break;
                        }
                    }
                    sortedList.add(higherIndex,w);
                }
            }
        // By fewest days otherwise:
        } else {
            for (Week w : tempList) {
                if (sortedList.isEmpty()) {
                    sortedList.add(w);
                } else {
                    int higherIndex = sortedList.size();
                    for (Week o : sortedList) {
                        if (w.getTotalDays() < o.getTotalDays()) {
                            higherIndex = sortedList.indexOf(o);
                            break;
                        }
                    }
                    sortedList.add(higherIndex,w);
                }
            }
        }
        // Finally, if the sorted list is empty (no results to display), put some
        // text in there notifying the user. Otherwise, populate the list with
        // the generated results.
        if (sortedList.isEmpty()) {
            Week w = new Week();
            w.setName("No valid timetables!");
            resultList.getItems().add(w);
            Week w2 = new Week();
            w2.setName("Please double check the filters.");
            resultList.getItems().add(w2);
        } else {
            for (Week w : sortedList) {
                resultList.getItems().add(w);
            }
        }
    }
    
    /**
     * When the ListView "resultList" is selected, call this method. Sends the
     * selected timetable to the TableView to be displayed.
     */
    @FXML
    private void setTableToSelection() {
        if (resultList.getSelectionModel().getSelectedItem() == null) {
            table.setActiveWeek(new Week());
        } else {
            table.setActiveWeek((Week)resultList.getSelectionModel().getSelectedItem());
        }
    }
}
