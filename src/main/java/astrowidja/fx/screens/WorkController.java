package astrowidja.fx.screens;

import astrowidja.core.AstroRegistry;
import astrowidja.core.NotificationService;
import astrowidja.core.Settings;
import astrowidja.model.storage.Album;
import astrowidja.model.storage.AstroWork;
import astrowidja.model.storage.AstroWorkFactory;
import astrowidja.model.storage.WorkCreationOutcome;
import astrowidja.provider.AstroDataProvider;
import astrowidja.model.TimeSpacePoint;
import astrowidja.model.TimeSpacePointFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import astrowidja.core.AppContext;

import java.time.*;
import java.util.List;

import javafx.util.StringConverter;

public class WorkController {

    private final AppContext appContext = AppContext.getInstance();
    private final AstroDataProvider astroEngine = appContext.getDataProvider();
    private final Settings currentSettings = appContext.getCurrentSettings();
    private final AstroRegistry registry = appContext.getAstroRegistry();


    @FXML
    private Label datePrompt;
    @FXML
    private Label timePrompt;
    @FXML
    public DatePicker datePicker;
    @FXML
    public TextArea resultTextArea;
    @FXML
    public Button calculateButton;
    @FXML
    private Spinner<Integer> hourSetter;
    @FXML
    private Spinner<Integer> minuteSetter;
    @FXML
    private ComboBox<String> timezoneSelector;
    @FXML
    private SplitMenuButton smartSaveButton;

    // Тултип для нулевого состояния
    private final Tooltip emptyStateTooltip = new Tooltip("Создайте альбом, чтобы сохранить карту");



    // Инициализация контроллера
    @FXML
    public void initialize() {
        initDatePicker();
        initTimeSpinners();
        initTimezoneSelector();

        setupSmartSaveButton();

        createChartForNow();
        NotificationService.showInfo("Привет! Так выглядят уведомления");
    }

    private void setupSmartSaveButton() {
        // 1. Подписываемся на изменение активного альбома в AppContext.
        // Как только activeAlbum изменится где угодно в программе — кнопка сама перерисуется!
        appContext.activeAlbumProperty().addListener((obs, oldAlbum, newAlbum) -> {
            updateSaveButtonState(newAlbum);
        });

        // Устанавливаем начальное состояние (если null -> "Сохранить в…")
        updateSaveButtonState(appContext.getActiveAlbum());

        // 2. Обработка клика по основной части кнопки
        smartSaveButton.setOnAction(event -> {
            Album currentAlbum = appContext.getActiveAlbum();
            if (currentAlbum == null) {
                // Если альбома нет, левая часть "деактивирована" — ничего не делаем
                // (или можно сразу открывать диалог создания)
                return;
            }
            executeSave(currentAlbum);
        });

        // 3. Динамическая сборка выпадающего меню под стрелочкой
        smartSaveButton.setOnShowing(event -> rebuildAlbumMenu());

    }

    /**
     * Обновляет внешний вид кнопки в зависимости от наличия активного альбома.
     */
    private void updateSaveButtonState(Album album) {
        if (album == null) {
            smartSaveButton.setText("Сохранить в…");
            smartSaveButton.setTooltip(emptyStateTooltip);
            // приглушаем текст, но НЕ деактивируем, чтобы стрелочка оставалась!
            smartSaveButton.setStyle("-fx-opacity: 0.75;");
        } else {
            smartSaveButton.setText("Сохранить в " + album.getTitle());
            smartSaveButton.setTooltip(null); // Убираем подсказку, всё и так понятно
            smartSaveButton.setStyle("-fx-opacity: 1.0;");
        }
    }

    /**
     * Наполняет выпадающий список актуальными альбомами + пунктом "Новый список".
     */
    private void rebuildAlbumMenu() {
        smartSaveButton.getItems().clear();

        var allAlbums = appContext.getUserLibrary().getAllAlbums();

        // Если альбомы уже есть в природе — добавляем их в меню
        if (!allAlbums.isEmpty()) {
            for (Album album : allAlbums) {
                MenuItem item = new MenuItem(album.getTitle());

                // Выделяем текущий активный альбом жирным шрифтом
                if (album.equals(appContext.getActiveAlbum()))
                    item.setStyle("-fx-font-weight: bold;");

                // При выборе альбома из списка делаем активным (текст обновится сам)
                item.setOnAction(e -> appContext.setActiveAlbum(album));

                smartSaveButton.getItems().add(item);
            }
            smartSaveButton.getItems().add(new SeparatorMenuItem());
        }
        // Пункт "Новый список" присутствует всегда
        MenuItem newAlbumItem = new MenuItem("Новый список...");
        newAlbumItem.setOnAction(e -> showCreateAlbumDialog());
        smartSaveButton.getItems().add(newAlbumItem);

    }

    private void executeSave(Album targetAlbum) {
        AstroWork work = appContext.getActiveWork();
        if (work == null) return;


    }

    private void showCreateAlbumDialog() {

    }

    private void createChartForNow() {
        try {
            WorkCreationOutcome outcome = AstroWorkFactory.createForNow();
            AstroWork work = outcome.work();
            appContext.setActiveChart(work);
            // Если замечания — мягко всплываем снизу
            if (outcome.hasWarnings())
                NotificationService.showInfo(outcome.warnings());

            resultTextArea.setText(appContext.getActiveChart().info());
        } catch (Exception e) {
            // Если создание прервалось из-за ошибки — показываем алёрт
            NotificationService.showError("Не удалось построить карту", e.getMessage());
        }
    }

    private void initDatePicker() {
        datePicker.setValue(LocalDate.now());
    }

    private void initTimeSpinners() {
        LocalTime now = LocalTime.now();
        hourSetter.setValueFactory(createTimeSpinnerFactory(23, now.getHour()));
        minuteSetter.setValueFactory(createTimeSpinnerFactory(59, now.getMinute()));
    }

    private static SpinnerValueFactory.IntegerSpinnerValueFactory createTimeSpinnerFactory(int max, int initial) {
        SpinnerValueFactory.IntegerSpinnerValueFactory factory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, max, initial);
        factory.setConverter(new StringConverter<>() {
            @Override
            public String toString(Integer value) {
                return String.format("%02d", value);
            }
            @Override
            public Integer fromString(String string) {
                return Integer.parseInt(string);
            }
        });
        return factory;
    }

    private void initTimezoneSelector() {
        List<String> zones = ZoneId.getAvailableZoneIds().stream()
                .sorted()
                .toList();
        timezoneSelector.getItems().addAll(zones);
        timezoneSelector.setValue(ZoneId.systemDefault().getId());
    }

    // On Action кнопки "Рассчитать"
    @FXML
    void onCalculateClick(ActionEvent event) {
        LocalDate selectedDate = datePicker.getValue();
        LocalTime localTime = LocalTime.of(hourSetter.getValue(), minuteSetter.getValue());
        ZoneId zoneId = ZoneId.of(timezoneSelector.getValue());

        TimeSpacePoint moment = TimeSpacePointFactory.createGeocentric(
                LocalDateTime.of(selectedDate, localTime), zoneId.getId()
        );

        AstroWork work = AstroWorkFactory.createForDate(moment);

        appContext.setActiveChart(work);

        String output =
                "Местное время:  %s %s (%s)\n"
                    .formatted(selectedDate, localTime, timezoneSelector.getValue())
//                + "Расчётный момент: %s %s UTC\n"
//                    .formatted(utcDate, utcTime)
                + "========================================\n"
                + appContext.getActiveChart().info();

        resultTextArea.setText(output);
    }

}
