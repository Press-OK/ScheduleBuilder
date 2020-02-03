package schedulebuilder;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * This is the first FXML scene which is created, where the user can create and
 * manage their courses/weeks.
 * 
 * @author Sean Berwick
 * @author Steve Markham
 * @author Alvin Alora
 */
public class FXMLEditorWindowController implements Initializable {

    // HBox for the BuildTree
    @FXML
    private HBox treebox;
    //   VBox for the TableView
    @FXML
    private VBox tablebox;
    //  Label which displays the current active task (under the table)
    @FXML
    private Label taskLabel;
    
    public Stage wnd;
    
    FXMLBuildTreeController treectrl;
    
    /**
     * JavaFX initialization method.
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        FXMLTableViewController tcontroller = null;
        // Create and add the TableView to the main window
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLTableView.fxml"));
            Parent tablepane = loader.load();
            tcontroller = loader.getController();
            tablebox.getChildren().add(0, tablepane);
        } catch (IOException ex) {
        }
        // Create and add the BuildTree to the main window
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLBuildTree.fxml"));
            Parent treepane = loader.load(); //when this is called it returns a pane
            // This method gives the BuildTree a reference to the TableView
            treectrl = loader.getController();
            treectrl.setTable(tcontroller, taskLabel);
            treebox.getChildren().add(0, treepane);
        } catch (IOException ex) {
        }
    }
    
    public void setParentWindow(Stage s) {
        wnd = s;
        treectrl.parentWindow = wnd;
    }
    
    // FILE MENU
    
    @FXML
    private void fileNew() {
        treectrl.newCollection();
    }
    
    @FXML
    private void fileSave() {
        treectrl.saveCollection(true);
    }
    
    @FXML
    private void fileSaveAs() {
        treectrl.saveCollection(false);
    }
    
    @FXML
    private void fileOpen() {
        treectrl.loadCollection();
    }
    
    @FXML
    private void fileExit() {
        Platform.exit();
    }
    
    
    
    
    // EDIT MENU
    
    @FXML
    private void newCourse() {
        treectrl.newCourse();
    }
    
    @FXML
    private void newWeek() {
        treectrl.newWeek();
    }
    
    @FXML
    private void generateCourse() {
        treectrl.generateCourse();
    }
    
    @FXML
    private void duplicateSelectedItem() {
        treectrl.duplicateSelectedItem();
    }
    
    @FXML
    private void removeSelectedItem() {
        treectrl.removeSelectedItem();
    }
    
    
    // HELP MENU
    
    @FXML
    private void helpAbout() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Schedule Builder 1.1.0f");
        alert.setContentText("Schedule Builder\nBy Sean Berwick, Steve Markham,"
                + " and Alvin Alora\n\nPrepared for Walid Belal's Java 2\n"
                + "Final Project - April, 2018");
        alert.showAndWait();
    }
}
