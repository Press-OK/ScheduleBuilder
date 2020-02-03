package schedulebuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * The left panel of the main editor window. It is used to create, manage, and
 * delete Course and Week options.
 * 
 * @author Sean Berwick
 * @author Steve Markham
 * @author Alvin Alora
 */
public class FXMLBuildTreeController implements Initializable {
    
    // The TreeView control which lists Courses and Weeks
    @FXML
    private TreeView tree;
    // The single instance UserCollection which will store Courses and their Weeks
    private UserCollection ucol = new UserCollection();
    // The FXMLTableView on the right side of the main window
    private FXMLTableViewController table;
    // The label which displays which task the user is currently doing (viewing/editing)
    private Label taskLabel;
    private File activeFile;
    public Stage parentWindow;
    
    /**
     * JavaFX initialization.
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        updateTree();
    }
    
    /**
     * This BuildTreeController needs a reference to the TableView on the right
     * side of the window. When this class is created, this method is called to
     * receive a reference to the TableView (and the label which displays the
     * current task)
     * @param t
     * @param taskDisplay 
     */
    public void setTable(FXMLTableViewController t, Label taskDisplay) {
        this.table = t;
        this.taskLabel = taskDisplay;
    }
    
    /**
     * Generate a new course with fake data.
     */
    @FXML
    public void generateCourse() {
        ucol.generate();
        updateTree();
    }
    
    /**
     * Create a new empty course.
     */
    @FXML
    public void newCourse() {
        ucol.newCourse();
        updateTree();
    }
    
    /**
     * Create a new empty week. It adds it to the selected course. If a week is
     * selected, the new week will be added to the same course as the selected week.
     */
    @FXML
    public void newWeek() {
        TreeItem s = (TreeItem)tree.getSelectionModel().getSelectedItem(); //getting which item is clicked
        if (s != null) {
            Course c;
            if (s.getValue() instanceof Course) {
                c = (Course)s.getValue();
            } else { //otherwise it is a week
                c = (Course)s.getParent().getValue();
            }
            ucol.newWeek(c);
            updateTree();
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("New Week Error");
            alert.setHeaderText("No Course Selected");
            alert.setContentText("To add a week option, first select a course to add it to.");
            alert.showAndWait();
        }
    }
    
    /**
     * Duplicates the currently selected item from the build tree (deep copy,
     * not new reference).
     */
    @FXML
    public void duplicateSelectedItem() {
        TreeItem s = (TreeItem)tree.getSelectionModel().getSelectedItem();
        if (s != null) {
            if (s.getValue() instanceof Course) {
                Course newCourse = ((Course)s.getValue()).getCopy();
                newCourse.setName("+Copy of " + ((Course)s.getValue()).getName());
                ucol.addCourse(newCourse);
            } else {
                Week newWeek = ((Week)s.getValue()).getCopy();
                Course parentCourse = (Course)s.getParent().getValue();
                newWeek.setName("+Copy of " + ((Week)s.getValue()).getName());
                parentCourse.addOption(newWeek);
            }
            updateTree();
        }
    }
    
    /**
     * Remove the selected item, either a Course or a Week. Deletes the instance
     * from the UserCollection and updates the display. TODO: Fix the increment
     * in the naming convention?
     */
    @FXML
    public void removeSelectedItem() {
        TreeItem s = (TreeItem)tree.getSelectionModel().getSelectedItem();
        if (s != null) {
            if (s.getValue() instanceof Course) {
                ucol.getCourses().remove((Course)s.getValue());
            } else {
                ((Course)s.getParent().getValue()).getWeekOptions().remove((Week)s.getValue());
            }
            updateTree();
        }
    }
    
    /**
     * Renames the selected item.
     */
    @FXML
    public void renameSelectedItem() {
        TreeItem s = (TreeItem)tree.getSelectionModel().getSelectedItem();
        if (s != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Rename Selected Item");
            dialog.setHeaderText("Rename a course or week");
            dialog.setContentText("Please enter the new name:");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                if (s.getValue() instanceof Course) {
                    ((Course)s.getValue()).setName(result.get());
                } else {
                    ((Week)s.getValue()).setName(result.get());
                }
            }
            updateTree();
        }
    }
    
    /**
     * Updates the TreeView control with data from the UserCollection whenever
     * it is called.
     */
    private void updateTree() {
        TreeItem<DataNode> rootItem = new TreeItem<>();
        tree.setRoot(rootItem);
        for(Course c : ucol.getCourses()) {
            TreeItem<DataNode> courseItem = new TreeItem<>(c);
            rootItem.getChildren().add(courseItem);
            courseItem.setExpanded(true);
            for(Week w : c.getWeekOptions()) {
                TreeItem<DataNode> weekItem = new TreeItem<>(w);
                courseItem.getChildren().add(weekItem);
            }
        }
    }
    
    /**
     * Called when the user clicks on an item in the TreeView. If it is a Week,
     * this function passes it to the TableView and sets the table to be editable.
     * If it is a Course, this function builds a new empty week and populates it
     * with every week option for the course, and passes it to the TableView for
     * viewing (not-editable).
     */
    @FXML
    public void changeActiveWeek() {
        // Get the Tree item that was clicked
        TreeItem s = (TreeItem)tree.getSelectionModel().getSelectedItem();
        // Make sure that an item was clicked, could have also used an event
        // handler listening for "change", but this works
        if (s != null) {
            // If a week was selected, pass it along to the TableView
            if (s.getValue() instanceof Week) {
                table.setActiveWeek((Week)s.getValue());
                table.setEditable(true);
                taskLabel.setText("Editing: " + s.getParent().getValue() + ", " + s.getValue());
            // If a course was selected, build a temporary week which displays
            // all of it's options at once, and pass it along to the TableView
            } else if (s.getValue() instanceof Course) {
                Course c = ((Course)s.getValue());
                Week w = new Week();
                int count = 0;
                for (Week n : c.getWeekOptions()) {
                    for (int i = 0; i < 5; i++) {
                        for (int j = 0; j < 13; j++) {
                            if (n.getOne(i, j) != 0) {
                                w.setOne(i, j, n.getOne(i, j)+count);
                            }
                        }
                    }
                    count += 1;
                }
                table.setActiveWeek(w);
                table.setEditable(false);
                taskLabel.setText("Viewing: " + s.getValue());
            }
        }
    }
    
    /**
     * Checks if there is enough course data to be processed. If so, it creates
     * a deep copy of the UserCollection: This way, the results window has
     * independent instances of the Courses, Weeks, etc. Finally, it opens the
     * new window and passes it the new UserCollection.
     */
    @FXML
    private void viewResults() {
        // If # courses <= 1, throw warning
        if (ucol.getCourses().size() <= 1) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Timetable Error");
            alert.setHeaderText("Insufficient Courses");
            alert.setContentText("You must have at least two courses to build a valid timetable.");
            alert.showAndWait();
        } else {
            // If any weeks are empty, emptyWeek will resolve to TRUE
            boolean emptyWeek = true;
            for (Course c : ucol.getCourses()) {
                for (Week w : c.getWeekOptions()) {
                    emptyWeek = true;
                    for (int d = 0; d < 5; d++) {
                        for (int h = 0; h < 13; h++) {
                            if (w.getOne(d, h) != 0) {
                                emptyWeek = false;
                                break;
                            }
                        }
                    }
                }
            }
            // If weeks are empty, throw warning
            if (emptyWeek) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Timetable Error");
                alert.setHeaderText("Week Contains No Data");
                alert.setContentText("One or more of the week options for a course contains no class times.");
                alert.showAndWait();
            } else {
                try {
                    // Build and show the ResultsWindow
                    Parent resultsWindow;
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLResultsWindow.fxml"));
                    resultsWindow = loader.load();
                    FXMLResultsWindowController ctrl = loader.getController();
                    Stage stage = new Stage();
                    stage.setTitle("Timetable Results");
                    stage.setScene(new Scene(resultsWindow));
                    stage.setResizable(false);
                    stage.getIcons().add(new Image("file:src/schedulebuilder/img/sbico.png"));
                    stage.show();
                    // Create deep copy of the UserCollection
                    UserCollection ucolDeepCopy = new UserCollection();
                    int validWeekNumber = 0;
                    for (int i = 0; i < ucol.getCourses().size(); i++) {
                        Course c = new Course();
                        c.setName(ucol.getCourses().get(i).getName());
                        for (int j = 0; j < ucol.getCourses().get(i).getWeekOptions().size(); j++) {
                            Week wcopy = new Week();
                            wcopy.setName("Week " + ++validWeekNumber);
                            Week wold = ucol.getCourses().get(i).getWeekOptions().get(j);
                            for (int d = 0; d < 5; d++) {
                                for (int h = 0; h < 13; h++) {
                                    wcopy.setOne(d, h, wold.getOne(d, h));
                                }
                            }
                            c.addOption(wcopy);
                        }
                        ucolDeepCopy.addCourse(c);
                    }
                    // Once the copy is made, give it to the new window's CONTROLLER
                    ctrl.setCollection(ucolDeepCopy);
                }
                catch (IOException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Generating Results");
                    alert.setHeaderText("Error Generating Results");
                    alert.setContentText("Unfortunately, Schedule Builder was unable to generate timetables.");
                    alert.showAndWait();
                }
            }
        }
    }
    
    /**
     * Start a new UserCollection from scratch.
     */
    @FXML
    public void newCollection() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Create New File");
        alert.setHeaderText("Erase all data and start a new file");
        alert.setContentText("Are you sure you want to erase all current courses and weeks?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            this.ucol = new UserCollection();
            parentWindow.setTitle("Schedule Builder: New Schedule");
            table.setActiveWeek(new Week());
            table.setEditable(false);
            activeFile = null;
            taskLabel.setText("Add courses to get started. Use the grid to edit weekly options.");
            updateTree();
        }
    }
    
    /**
     * Load UserCollection from a file
     */
    @FXML
    public void loadCollection() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Courses");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Schedule", "*.sch"));
        Stage fcStage = new Stage();
        File f = fileChooser.showOpenDialog(fcStage);
        if (f != null) {
            try {
                FileInputStream fos = new FileInputStream(f);
                ObjectInputStream ois = new ObjectInputStream(fos);
                try {
                    ucol = (UserCollection)(ois.readObject()); // Returned as Object, ucol expects UserCollection
                } catch (ClassNotFoundException x) {
                    System.out.println("Unable to load UserCollection from file!");
                }
                parentWindow.setTitle("Schedule Builder: " + f.getName());
                table.setActiveWeek(new Week());
                table.setEditable(false);
                taskLabel.setText("Loaded file " + f.getPath());
                activeFile = f;
                ois.close();
            } catch (IOException e) {
                System.out.println("Error loading file!");
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Loading File");
                alert.setHeaderText("Error Loading File");
                alert.setContentText("Unfortunately, Schedule Builder was unable to load the file.");
                alert.showAndWait();
            }
        }
        updateTree();
    }

    /**
     * Save UserCollection to a file
     * @param justSave
     */
    @FXML
    public void saveCollection(boolean justSave) {
        // Save as
        if (!justSave || activeFile == null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Courses");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Schedule", "*.sch"));
            Stage fcStage = new Stage();
            File f = fileChooser.showSaveDialog(fcStage);
            if (f != null) {
                try {
                    FileOutputStream fos = new FileOutputStream(f);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(this.ucol);
                    parentWindow.setTitle("Schedule Builder: " + f.getName());
                    taskLabel.setText("Saved file to " + f.getPath());
                    activeFile = f;
                    oos.close();
                } catch (IOException e) {
                    System.out.println("Error saving file!");
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Saving File");
                    alert.setHeaderText("Error Saving File");
                    alert.setContentText("Unfortunately, Schedule Builder was unable to save the file.");
                    alert.showAndWait();
                }
            }
            // Just save
        } else {
            try {
                FileOutputStream fos = new FileOutputStream(activeFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(this.ucol);
                parentWindow.setTitle("Schedule Builder: " + activeFile.getName());
                taskLabel.setText("Saved file to " + activeFile.getPath());
                oos.close();
            } catch (IOException e) {
                System.out.println("Error saving file!");
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Saving File");
                alert.setHeaderText("Error Saving File");
                alert.setContentText("Unfortunately, Schedule Builder was unable to save the file.");
                alert.showAndWait();
            }
        }
    }     
}
