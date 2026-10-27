package astrowidja;

import astrowidja.fx.MainFrame;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class AWApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AWApplication.class.getResource("fx/main_frame.fxml"));
        Parent root = fxmlLoader.load();
        MainFrame mainFrame = fxmlLoader.getController();
        Scene scene = new Scene(root);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, mainFrame::handleGlobalKeys);
        stage.setTitle("АстроВидья");
        stage.setScene(scene);
        stage.setMinWidth(500);
        stage.setMinHeight(400);
        stage.show();
    }
}