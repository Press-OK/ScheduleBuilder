package schedulebuilder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * The main .java file of the project.
 * 
 * @author Sean Berwick
 * @author Steve Markham
 * @author Alvin Alora
 */
public class ScheduleBuilder extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLEditorWindow.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        ((FXMLEditorWindowController)loader.getController()).setParentWindow(stage);
        stage.getIcons().add(new Image("file:src/schedulebuilder/img/sbico.png"));
        stage.setTitle("Schedule Builder: New Schedule");
        stage.setResizable(false);
        stage.sizeToScene();
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
