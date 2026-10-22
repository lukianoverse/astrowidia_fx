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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.util.StringConverter;

import static swisseph.SweConst.*;

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
        // По умолчанию текущая дата
        datePicker.setValue(LocalDate.now());

        // Текущее время системы
        LocalTime now = LocalTime.now();

        // Часы (0-23), по умолчанию текущие
        SpinnerValueFactory.IntegerSpinnerValueFactory hoursFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, now.getHour());
        hoursFactory.setConverter(new StringConverter<>() {
            @Override
            public String toString(Integer value) {
                return String.format("%02d", value);
            }
            @Override
            public Integer fromString(String string) {
                return Integer.parseInt(string);
            }
        });
        hourSetter.setValueFactory(hoursFactory);

        // Минуты (0-59), по умолчанию текущие
        SpinnerValueFactory.IntegerSpinnerValueFactory minutesFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, now.getMinute());
        minutesFactory.setConverter(new StringConverter<>() {
            @Override
            public String toString(Integer value) {
                return String.format("%02d", value);
            }
            @Override
            public Integer fromString(String string) {
                return Integer.parseInt(string);
            }
        });
        minuteSetter.setValueFactory(minutesFactory);

        // Перечень часовых поясов
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
                appContext.getAstroRegistry(),
                dataProvider);
        appContext.setActiveChart(chart);

        String output = "Местное время:  %s %s (%s)\n".formatted(selectedDate, localTime, zoneIdString)
                + "Расчётный момент: %s %s UTC\n".formatted(utcDate, utcTime)
                + "========================================\n"
                + appContext.getActiveChart();

        resultTextArea.setText(output);
    }

}
