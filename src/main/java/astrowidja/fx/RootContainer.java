package astrowidja.fx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class RootContainer {

    @FXML
    private StackPane mainStackPane;

    private Parent mainScreen;
    private Parent libraryScreen;
    private Parent astrasScreen;
    private Parent settingsScreen;

    @FXML
    public void initialize() {
        try {
            // Загружаем все 4 экрана в память единожды
            mainScreen = loadScreen("main_screen.fxml");
            libraryScreen = loadScreen("library_screen.fxml");
            astrasScreen = loadScreen("astras_screen.fxml");
            settingsScreen = loadScreen("settings_screen.fxml");

            // Складываем их друг на друга в StackPane
            mainStackPane.getChildren().addAll(mainScreen, libraryScreen, astrasScreen, settingsScreen);

            // По умолчанию кажем главный экран расчёта
            showScreen(mainScreen);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Parent loadScreen(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        return loader.load();
    }

    // делает выбранный экран видимым
    public void showScreen(Parent targetScreen) {
        mainScreen.setVisible(targetScreen == mainScreen);
        libraryScreen.setVisible(targetScreen == libraryScreen);
        astrasScreen.setVisible(targetScreen == astrasScreen);
        settingsScreen.setVisible(targetScreen == settingsScreen);

        targetScreen.toFront();
    }

    // горячие клавиши вешаются на главный класс AWApplication
    public void handleGlobalKeys(KeyEvent event) {
        if (event.getCode() == KeyCode.F5) showScreen(mainScreen);
        if (event.getCode() == KeyCode.F6) showScreen(libraryScreen);
        if (event.getCode() == KeyCode.F7) showScreen(astrasScreen);
        if (event.getCode() == KeyCode.F8) showScreen(settingsScreen);
    }
}
