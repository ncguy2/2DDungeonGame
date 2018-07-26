package net.ncguy.tools.debug;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.ncguy.lwjgl3.Lwjgl3Launcher;
import net.ncguy.profile.CPUProfiler;
import net.ncguy.profile.ProfilerHost;
import org.scenicview.ScenicView;

import java.io.IOException;
import java.lang.management.RuntimeMXBean;
import java.util.Scanner;

import static javafx.fxml.FXMLLoader.load;

public class DebugViewer extends Application {

    public static void main(String[] args) {

//        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
//        System.out.println(runtimeMXBean.getName());
        System.out.println("Renderdoc injection point");
        new Scanner(System.in).next();


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
        scene.getStylesheets().add("css/global.css");

        ScenicView.show(scene);

        primaryStage.setTitle("Debug Viewer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
