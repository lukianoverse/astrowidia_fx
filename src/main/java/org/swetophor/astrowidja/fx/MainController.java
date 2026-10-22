package org.swetophor.astrowidja.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.swetophor.astrowidja.provider.AstroDataProvider;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static swisseph.SweConst.*;

public class MainController {

    private final AstroDataProvider dataProvider = new AstroDataProvider();
    // минимальный набор планет для MVP
    private final Map<String, Integer> targetPlanets = new LinkedHashMap<>();

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
        hourSetter.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, now.getHour()));

        // Минуты (0-59), по умолчанию текущие
        minuteSetter.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, now.getMinute()));

        // Перечень часовых поясов
        List<String> zones = ZoneId.getAvailableZoneIds().stream()
                .sorted()
                .toList();
        timezoneSelector.getItems().addAll(zones);
        timezoneSelector.setValue(ZoneId.systemDefault().getId());

        // Заполняем карту планет (название и ID по Swiss Ephemeris)
        targetPlanets.put("Солнце", SE_SUN);
        targetPlanets.put("Луна", SE_MOON);
        targetPlanets.put("Меркурий", SE_MERCURY);
        targetPlanets.put("Венера", SE_VENUS);
        targetPlanets.put("Марс", SE_MARS);
        targetPlanets.put("Юпитер", SE_JUPITER);
        targetPlanets.put("Сатурн", SE_SATURN);
        targetPlanets.put("Уран", SE_URANUS);
        targetPlanets.put("Нептун", SE_NEPTUNE);
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

        StringBuilder output = new StringBuilder();
        output.append("Местное время:  %s %s (%s)\n".formatted(selectedDate, localTime, zoneIdString));
        output.append("Расчётный момент: %s %s UTC\n".formatted(utcDate, utcTime));
        output.append("========================================\n");

        // Проходим по астрам
        for (Map.Entry<String, Integer> entry : targetPlanets.entrySet()) {
            String planetName = entry.getKey();
            int planetId = entry.getValue();
            var result = dataProvider.getPlanetEphemeris(selectedDate, localTime, planetId);

            if (result.isCompleted()) { // Если расчёт выполнен (с замечаниями или без)
                String retrogradeness = result.isRetrograde() ? " (R)" : "    ";
                output.append("%-10s%s = %11.5f°\n"
                        .formatted(planetName, retrogradeness, result.longitude()));
                // TODO: если status != 0, нужно будет вздымать окошко уведомления
                if (result.status() > 0 && !result.serr().isEmpty())
                    output.append("Предупреждение для %s: %s"
                        .formatted(planetName, result.serr()));
            } else {
                output.append("%-10s → Ошибка: %s\n"
                        .formatted(planetName, result.serr()));
            }
        }

        // Выводим собранный текст в окно
        resultTextArea.setText(output.toString());
    }

}
