package astrowidja.fx;

import astrowidja.core.AstroRegistry;
import astrowidja.core.Settings;
import astrowidja.model.charts.Chart;
import astrowidja.provider.AstroDataProvider;
import astrowidja.provider.TimeSpacePoint;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import astrowidja.core.AppContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import javafx.util.StringConverter;

public class WorkController {

    private final AppContext appContext = AppContext.getInstance();
    private final AstroDataProvider dataProvider = appContext.getDataProvider();
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



    // Инициализация контроллера
    @FXML
    public void initialize() {
        initDatePicker();
        initTimeSpinners();
        initTimezoneSelector();
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

        TimeSpacePoint moment = new TimeSpacePoint(selectedDate, localTime, zoneId);

        ZonedDateTime localMoment = ZonedDateTime.of(selectedDate, localTime, zoneId);
        ZonedDateTime utcMoment = localMoment.withZoneSameInstant(ZoneId.of("UTC"));

        LocalDate utcDate = utcMoment.toLocalDate();
        LocalTime utcTime = utcMoment.toLocalTime();

        Chart chart = new Chart(
                "Карта",
                moment,
                registry.getEntitiesToCalculate(currentSettings.getAstrasToCalculate()),
                dataProvider);
        appContext.setActiveChart(chart);

        String output =
                "Местное время:  %s %s (%s)\n"
                    .formatted(selectedDate, localTime, timezoneSelector.getValue())
                + "Расчётный момент: %s %s UTC\n"
                    .formatted(utcDate, utcTime)
                + "========================================\n"
                + appContext.getActiveChart();

        resultTextArea.setText(output);
    }

}
