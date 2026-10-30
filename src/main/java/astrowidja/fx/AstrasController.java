package astrowidja.fx;

import astrowidja.core.AppContext;
import astrowidja.core.AstroRegistry;
import astrowidja.core.Settings;
import astrowidja.model.AstroEntity;
import astrowidja.model.LilithType;
import astrowidja.model.NodeType;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class AstrasController {

    private final AppContext appContext = AppContext.getInstance();
    private final AstroRegistry registry = appContext.getAstroRegistry();
    private final Settings currentSettings = appContext.getCurrentSettings();

    @FXML private VBox mainAstrasGroup;
    @FXML private VBox lunarAstrasGroup;

    @FXML
    public void initialize() {
        boolean hasEphemeris = checkEphemerisFiles();

        List<AstroEntity> allAstras = registry.getAll();

        // Распределяем 14 объектов по двум колонкам согласно спецификации
        for (int i = 0; i < allAstras.size(); i++) {
            AstroEntity entity = allAstras.get(i);

            // Строим один аккуратный ряд: Чекбокс + Символ + Название
            HBox row = new HBox(12.0); // Отступ между элементами ряда
            row.setAlignment(Pos.CENTER_LEFT);

            CheckBox checkBox = new CheckBox();

            // отмечаем астру согласно текущим Настройкам
            ObservableList<String> activeNames = currentSettings.getAstrasToCalculate();
            checkBox.setSelected(activeNames.contains(entity.getName()));

            // связываем с актуальным состоянием галочки
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    // галочка прилетела → добавляем имя в настройки, если его там нет
                    if (!activeNames.contains(entity.getName()))
                        activeNames.add(entity.getName());
                } else {
                    // галочка улетела -> убираем имя из настроек
                    activeNames.remove(entity.getName());
                }
            });

            // 2. Яркий символ объекта
            Label lblSymbol = new Label(entity.getSymbol());
            lblSymbol.setStyle("-fx-text-fill: #00ffcc; -fx-font-size: 18; -fx-min-width: 25; -fx-alignment: center;");

            // 3. Текстовое название
            Label lblName = new Label(entity.getName());
            lblName.setStyle("-fx-text-fill: white; -fx-font-size: 15;");

            // --- Логика затенения, если файлов эфемерид нет в папке ephe ---
            if (!hasEphemeris)
                if (!isObjectMoshierable(entity.getSwissId())) {
                    // Если объект требует файлы, а их нет — затеняем и блокируем
                    lblSymbol.setStyle("-fx-text-fill: #444444; -fx-font-size: 18; -fx-min-width: 25;");
                    lblName.setStyle("-fx-text-fill: #555555; -fx-font-size: 15;");
                    checkBox.setDisable(true);
                }

            row.getChildren().addAll(checkBox, lblSymbol, lblName);

            // Первые 12 тел отправляем в левый столбик, остальные 2 — в правый
            if (i < 12) mainAstrasGroup.getChildren().add(row);
            else lunarAstrasGroup.getChildren().add(row);
        }
    }







    private boolean isObjectMoshierable(int swissId) {
        return Arrays.stream(AstroRegistry.MOSHIERABLE)
                .anyMatch(id -> id == swissId);
    }

    private boolean checkEphemerisFiles() {
        Path localEpheFolder = Paths.get(".").resolve("ephe");
        return Files.exists(localEpheFolder.resolve("sepl_18.se1")) &&
                Files.exists(localEpheFolder.resolve("semo_18.se1"));
    }
}
