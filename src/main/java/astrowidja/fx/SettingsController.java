package astrowidja.fx;

import astrowidja.core.AppContext;
import astrowidja.core.Settings;
import astrowidja.provider.CalcMethod;
import astrowidja.model.LilithType;
import astrowidja.model.NodeType;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SettingsController {

    private final AppContext appContext = AppContext.getInstance();
    private final Settings currentSettings = appContext.getCurrentSettings();

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

        if (ephemerisAvailable) {
            // Эфемериды присутствуют! Загружаем сохранённые (или дефолтные) настройки
            rbMoshier.setSelected(currentSettings.getCalcMethod() == CalcMethod.MOSHIER);
            rbSwissEph.setSelected(currentSettings.getCalcMethod() == CalcMethod.SWISSEPH);

            rbMeanNode.setSelected(currentSettings.getNodeType() == NodeType.MEAN);
            rbTrueNode.setSelected(currentSettings.getNodeType() == NodeType.TRUE);

            rbMeanLilith.setSelected(currentSettings.getLilithType() == LilithType.MEAN);
            rbTrueLilith.setSelected(currentSettings.getLilithType() == LilithType.TRUE);
            rbInterpolatedLilith.setSelected(currentSettings.getLilithType() == LilithType.INTERPOLATED);
        } else {
            // если эфемерид нет:
            // настройки откатываются на Мошьера
            currentSettings.setCalcMethod(CalcMethod.MOSHIER);
            currentSettings.setNodeType(NodeType.MEAN);
            currentSettings.setLilithType(LilithType.MEAN);

            // выставляем соответствующие опции
            rbMoshier.setSelected(true);
            rbMeanNode.setSelected(true);
            rbMeanLilith.setSelected(true);

            // отключаем опции, недоступные без эфемерид
            rbSwissEph.setDisable(true);
            rbTrueNode.setDisable(true);
            rbTrueLilith.setDisable(true);
            rbInterpolatedLilith.setDisable(true);
        }

        // Вешаем слушатели изменений на группы кнопок,
        // чтобы их переключение влияло на Настройки
        setupListeners();
    }

    private boolean checkEphemerisAvailability() {
        // 1. Проверяем ресурсы проекта (Classpath) — для отладки прямо в IDE
        var planetsResource = getClass().getResource("/ephemeris/sepl_18.se1");
        var moonResource = getClass().getResource("/ephemeris/semo_18.se1");

        if (planetsResource != null && moonResource != null) {
            return true;
        }

        // 2. Проверяем локальную папку "ephe" в директории запущенной программы
        // (Paths.get(".") смотрит в корень, где лежит JAR или запускается процесс)
        Path localEpheFolder = Paths.get(".").resolve("ephe");

        Path basePlanetsFile = localEpheFolder.resolve("sepl_18.se1");
        Path baseMoonFile = localEpheFolder.resolve("semo_18.se1");

        return Files.exists(basePlanetsFile) && Files.exists(baseMoonFile);
    }


    // Чтобы при выборе опции значение тут же актуализировалось
    private void setupListeners() {

        calcMethodGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == rbMoshier)
                currentSettings.setCalcMethod(CalcMethod.MOSHIER);
            if (newT == rbSwissEph)
                currentSettings.setCalcMethod(CalcMethod.SWISSEPH);
        });

        nodeTypeGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == rbMeanNode)
                currentSettings.setNodeType(NodeType.MEAN);
            if (newT == rbTrueNode)
                currentSettings.setNodeType(NodeType.TRUE);
        });

        lilithTypeGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == rbMeanLilith)
                currentSettings.setLilithType(LilithType.MEAN);
            if (newT == rbTrueLilith)
                currentSettings.setLilithType(LilithType.TRUE);
            if (newT == rbInterpolatedLilith)
                currentSettings.setLilithType(LilithType.INTERPOLATED);
        });
    }

}
