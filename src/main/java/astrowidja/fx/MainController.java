package astrowidja.fx;

import astrowidja.model.Chart;
import astrowidja.provider.AstroDataProvider;
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

public class MainController {

    private final AppContext appContext = AppContext.getInstance();
    private final AstroDataProvider dataProvider = appContext.getDataProvider();

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
        if (selectedDate == null) {
            resultTextArea.setText("Пожалуйста, выберите дату.");
            return;
        }

        int hours = hourSetter.getValue();
        int minutes = minuteSetter.getValue();
        String zoneIdString = timezoneSelector.getValue();

        LocalTime localTime = LocalTime.of(hours, minutes);
        ZoneId zoneId = ZoneId.of(zoneIdString);
        ZonedDateTime localMoment = ZonedDateTime.of(selectedDate, localTime, zoneId);
        ZonedDateTime utcMoment = localMoment.withZoneSameInstant(ZoneId.of("UTC"));

        LocalDate utcDate = utcMoment.toLocalDate();
        LocalTime utcTime = utcMoment.toLocalTime();

        Chart chart = new Chart(
                "Карта",
                utcDate,
                utcTime,
                appContext.getAstroRegistry().getAll(),
                dataProvider);
        appContext.setActiveChart(chart);

        String output = "Местное время:  %s %s (%s)\n".formatted(selectedDate, localTime, zoneIdString)
                + "Расчётный момент: %s %s UTC\n".formatted(utcDate, utcTime)
                + "========================================\n"
                + appContext.getActiveChart();

        resultTextArea.setText(output);
    }

}
