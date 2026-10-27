package astrowidja.fx;

import astrowidja.core.AppContext;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SettingsController {

    private final AppContext appContext = AppContext.getInstance();

    @FXML private ToggleGroup calcMethodGroup;
    @FXML private ToggleGroup nodeTypeGroup;
    @FXML private ToggleGroup lilithTypeGroup;

    @FXML private RadioButton rbMoshier;
    @FXML private RadioButton rbSwissEph;

    @FXML private RadioButton rbMeanNode;
    @FXML private RadioButton rbTrueNode;

    @FXML private RadioButton rbMeanLilith;
    @FXML private RadioButton rbTrueLilith;
    @FXML private RadioButton rbInterpolatedLilith;

    @FXML
    public void initialize() {
        boolean ephemerisAvailable = checkEphemerisAvailability();

        if (!ephemerisAvailable) {
            // Если файлов нет, ограничиваемся Мошьером
            rbMoshier.setSelected(true);
            rbMeanNode.setSelected(true);
            rbMeanLilith.setSelected(true);

            // Блокируем опции, требующие присутствия файлов
            rbSwissEph.setDisable(true);
            rbTrueNode.setDisable(true);
            rbTrueLilith.setDisable(true);
            rbInterpolatedLilith.setDisable(true);
        } else {
            // Файлы на месте! Загружаем дефолтные или сохранённые настройки
            rbSwissEph.setSelected(true);
            rbTrueNode.setSelected(true);
            rbMeanLilith.setSelected(true);
        }

        // Тут можно повесить слушатели изменений (Listeners) на ToggleGroup,
        // чтобы при переключении кнопок обновлять конфиг в AppContext
        setupListeners();
    }

    private boolean checkEphemerisAvailability() {
        // Логика проверки путей точно такая же, как в конфигураторе эфемерид
        String os = System.getProperty("os.name").toLowerCase();
        Path epheFolder;

        if (os.contains("win")) {
            epheFolder = Paths.get(System.getenv("LOCALAPPDATA"), "AstroWidja", "ephe");
        } else {
            epheFolder = Paths.get(System.getProperty("user.home"), ".local", "share", "astrowidja", "ephe");
        }

        // Проверяем физическое существование хотя бы базовых планетарных файлов на диске
        Path basePlanetsFile = epheFolder.resolve("sepl_18.se1");
        Path baseMoonFile = epheFolder.resolve("semo_18.se1");

        return Files.exists(basePlanetsFile) && Files.exists(baseMoonFile);
    }

    private void setupListeners() {
        calcMethodGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (newToggle == rbMoshier) {
                // Записать в AppContext глобальный флаг использования Moshier
            } else if (newToggle == rbSwissEph) {
                // Записать флаг использования SwissEph
            }
        });

        // Аналогичные слушатели для nodeTypeGroup и lilithTypeGroup...
    }
}
