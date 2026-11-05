package astrowidja.fx;

import astrowidja.core.AppContext;
import astrowidja.core.AstroRegistry;
import astrowidja.core.Settings;
import astrowidja.model.AstroEntity;
import astrowidja.model.LilithType;
import astrowidja.model.NodeType;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import swisseph.SweConst;

import java.util.List;

import static astrowidja.provider.EpheChecker.isCalculatable;

public class AstrasController {

    private final AppContext appContext = AppContext.getInstance();
    private final AstroRegistry registry = appContext.getAstroRegistry();
    private final Settings currentSettings = appContext.getCurrentSettings();

    @FXML private VBox mainAstrasGroup;
    @FXML private VBox lunarAstrasGroup;
    @FXML private HBox columnsContainer;
    @FXML private VBox extraAsteroidsGroup;

    private final static int MAX_ITEMS_PER_USER_COLUMN = 14;

    @FXML
    public void initialize() {

        // Колонка 1 (Основные 12)
        for (AstroEntity entity : registry.getDefaultBodies())
            mainAstrasGroup.getChildren().add(createAstraRow(entity));

        // Колонка 2 (Лунные узлы/аспиды и их переключатели)
        AstroEntity[] lunarPoints = registry.getLunarPoints();
        lunarAstrasGroup.getChildren().add(createAstraRow(lunarPoints[0]));
        lunarAstrasGroup.getChildren().add(createNodeRadioGroup(currentSettings));
        lunarAstrasGroup.getChildren().add(createAstraRow(lunarPoints[1]));
        lunarAstrasGroup.getChildren().add(createLilithRadioGroup(currentSettings));

        // Надстройка ещё колонок для юзерских астр справа
        List<AstroEntity> userAstras = registry.getUserDefinedAstras();
        buildUserColumns(userAstras);
    }


    private void buildUserColumns(List<AstroEntity> userAstras) {
        VBox currentColumnVBox = null;
        int columnCounter = 1;

        for (int i = 0; i < userAstras.size(); i++) {
            // Если мы превысили лимит элементов на колонку или это самый первый запуск
            if (i % MAX_ITEMS_PER_USER_COLUMN == 0) {
                // Создаем новый внешний каркас шириной 300px
                VBox columnWrapper = new VBox();
                columnWrapper.setPrefWidth(300.0);
                columnWrapper.setMinWidth(300.0);
                columnWrapper.setMaxWidth(300.0);

                // Создаем рамку TitledPane
                TitledPane userPane = new TitledPane();
                userPane.setText("ПОЛЬЗОВАТЕЛЬСКИЕ ТОЧКИ " + columnCounter++);
                userPane.setCollapsible(false);
                userPane.setMaxHeight(Double.MAX_VALUE);

                // Внутренний контейнер для чекбоксов
                currentColumnVBox = new VBox(12.0);
                currentColumnVBox.setStyle("-fx-background-color: #2b2b2b;");
                userPane.setContent(currentColumnVBox);

                columnWrapper.getChildren().add(userPane);
                // Толкаем новую колонку вправо в наш главный HBox!
                columnsContainer.getChildren().add(columnWrapper);
            }

            if (currentColumnVBox != null) {
                currentColumnVBox.getChildren().add(createAstraRow(userAstras.get(i)));
            }
        }
    }

    private HBox createAstraRow(AstroEntity entity) {
        HBox row = new HBox(12.0);
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

        Label lblSymbol = new Label(entity.getSymbol());
        lblSymbol.setStyle("-fx-text-fill: #00ffcc; -fx-font-size: 18; -fx-min-width: 25; -fx-alignment: center;");

        Label lblName = new Label(entity.getName());
        lblName.setStyle("-fx-text-fill: white; -fx-font-size: 15;");

        // --- ПЕРСОНАЛЬНАЯ ПРОВЕРКА ЗАТЕНЕНИЯ ---
        // Каждая астра сама спрашивает checker, есть ли под неё файл!
        boolean calculatable = isCalculatable(entity.getSwissId());

        if (!calculatable) {
            // Если файла нет — гасим этот объект
            lblSymbol.setStyle("-fx-text-fill: #444444; -fx-font-size: 18; -fx-min-width: 25;");
            lblName.setStyle("-fx-text-fill: #555555; -fx-font-size: 15;");
            checkBox.setDisable(true);
            activeNames.remove(entity.getName()); // Убираем из расчёта
        }

        row.getChildren().addAll(checkBox, lblSymbol, lblName);
        return row;
    }

    private VBox createNodeRadioGroup(Settings settings) {
        VBox box = new VBox(5.0);
        box.setPadding(new Insets(0, 0, 10, 32)); // Сдвигаем вправо под имя чекбокса

        ToggleGroup group = new ToggleGroup();
        RadioButton rbMean = new RadioButton("Средний");
        RadioButton rbTrue = new RadioButton("Истинный");

        rbMean.setToggleGroup(group);
        rbTrue.setToggleGroup(group);
        rbMean.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 13;");
        rbTrue.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 13;");

        // Инициализация состояний
        rbMean.setSelected(settings.getNodeType() == NodeType.MEAN);
        rbTrue.setSelected(settings.getNodeType() == NodeType.TRUE);
        if (!isCalculatable(SweConst.SE_TRUE_NODE))
            rbTrue.setDisable(true);

        // Слушатели
        group.selectedToggleProperty().addListener((o, oldT, newT)
                        -> settings.setNodeType(newT == rbTrue ? NodeType.TRUE : NodeType.MEAN));

        box.getChildren().addAll(rbMean, rbTrue);
        return box;
    }


    private VBox createLilithRadioGroup(Settings settings) {
        VBox box = new VBox(5.0);
        box.setPadding(new Insets(0, 0, 0, 32)); // Сдвигаем вправо под имя чекбокса

        ToggleGroup group = new ToggleGroup();
        RadioButton rbMean = new RadioButton("Средняя");
        RadioButton rbTrue = new RadioButton("Истинная");
        RadioButton rbInt = new RadioButton("Интерполированная");

        for (RadioButton rb : List.of(rbMean, rbTrue, rbInt)) {
            rb.setToggleGroup(group);
            rb.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 13;");
        }

        // Инициализация состояний
        rbMean.setSelected(settings.getLilithType() == LilithType.MEAN);
        rbTrue.setSelected(settings.getLilithType() == LilithType.TRUE);
        rbInt.setSelected(settings.getLilithType() == LilithType.INTERPOLATED);

        if (!isCalculatable(SweConst.SE_OSCU_APOG))
            rbTrue.setDisable(true);
        if (!isCalculatable(SweConst.SE_INTP_APOG))
            rbInt.setDisable(true);

        // Слушатели
        group.selectedToggleProperty().addListener((o, oldT, newT) -> {
            if (newT == rbTrue) settings.setLilithType(LilithType.TRUE);
            else if (newT == rbInt) settings.setLilithType(LilithType.INTERPOLATED);
            else settings.setLilithType(LilithType.MEAN);
        });

        box.getChildren().addAll(rbMean, rbTrue, rbInt);
        return box;
    }


}
