package astrowidja.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainFrame {

    @FXML
    private StackPane mainStackPane;

    // кнопки верхнего меню
    @FXML private Button btnWork;
    @FXML private Button btnLibrary;
    @FXML private Button btnAstras;
    @FXML private Button btnSettings;

    // экраны
    private Parent workScreen;
    private Parent libraryScreen;
    private Parent astrasScreen;
    private Parent settingsScreen;

    @FXML
    public void initialize() {
        try {
            // Загружаем все 4 экрана в память единожды
            workScreen = loadScreen("work_screen.fxml");
            libraryScreen = loadScreen("library_screen.fxml");
            astrasScreen = loadScreen("astras_screen.fxml");
            settingsScreen = loadScreen("settings_screen.fxml");

            // Складываем их друг на друга в StackPane
            mainStackPane.getChildren().addAll(workScreen, libraryScreen, astrasScreen, settingsScreen);

            // По умолчанию кажем главный экран расчёта
            showScreen(workScreen, btnWork);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Parent loadScreen(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        return loader.load();
    }

    // Обработчик мыши: кликнули на кнопку -> переключили экран
    @FXML
    private void onMenuButtonClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();

        if (clickedButton == btnWork) showScreen(workScreen, btnWork);
        else if (clickedButton == btnLibrary) showScreen(libraryScreen, btnLibrary);
        else if (clickedButton == btnAstras) showScreen(astrasScreen, btnAstras);
        else if (clickedButton == btnSettings) showScreen(settingsScreen, btnSettings);
    }

    // Метод переключения экранов с подсветкой кнопок
    public void showScreen(Parent targetScreen, Button activeButton) {
        workScreen.setVisible(targetScreen == workScreen);
        libraryScreen.setVisible(targetScreen == libraryScreen);
        astrasScreen.setVisible(targetScreen == astrasScreen);
        settingsScreen.setVisible(targetScreen == settingsScreen);

        targetScreen.toFront();

        // Меняем стили кнопок в зависимости от активной
        updateButtonStyles(activeButton);
    }

    private void updateButtonStyles(Button activeButton) {
        Button[] allButtons = {btnWork, btnLibrary, btnAstras, btnSettings};
        for (Button btn : allButtons) {
            // Активная кнопка: выделяется фоном и жирным белым текстом
            // Неактивные кнопки: сливаются с темным фоном, текст серый
            if (btn == activeButton)
                btn.setStyle("-fx-background-radius: 0; -fx-background-color: #2b2b2b;" +
                        " -fx-text-fill: white; -fx-font-weight: bold;");
            else
                btn.setStyle("-fx-background-radius: 0; -fx-background-color: #1a1a1a;" +
                        " -fx-text-fill: #888888;");
        }
    }

    // горячие клавиши вешаются на главный класс AWApplication
    public void handleGlobalKeys(KeyEvent event) {
        if (event.getCode() == KeyCode.F5) showScreen(workScreen, btnWork);
        if (event.getCode() == KeyCode.F6) showScreen(libraryScreen, btnLibrary);
        if (event.getCode() == KeyCode.F7) showScreen(astrasScreen, btnAstras);
        if (event.getCode() == KeyCode.F8) showScreen(settingsScreen, btnSettings);
    }
}
