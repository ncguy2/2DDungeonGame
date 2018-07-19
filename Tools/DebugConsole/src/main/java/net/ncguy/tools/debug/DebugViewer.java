package net.ncguy.tools.debug;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.ncguy.lwjgl3.Lwjgl3Launcher;

import java.io.IOException;

import static javafx.fxml.FXMLLoader.load;

public class DebugViewer extends Application {

    public static void main(String[] args) {
        new Thread(() -> Lwjgl3Launcher.main(args)).start();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Parent root;
        try {
            root = load(getClass().getResource("/fxml/root.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(root, 300, 275);

        primaryStage.setTitle("Debug Viewer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
