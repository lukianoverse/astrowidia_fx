package org.swetophor.astrowidja.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.swetophor.astrowidja.provider.AstroDataProvider;
import swisseph.SweConst;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static swisseph.SweConst.*;

public class MainController {
    @FXML
    public Label prompt;
    @FXML
    public DatePicker datePicker;
    @FXML
    public TextArea resultTextArea;
    @FXML
    public Button calculateButton;

    private final AstroDataProvider dataProvider = new AstroDataProvider();

    // минимальный набор планет для MVP
    private final Map<String, Integer> targetPlanets = new LinkedHashMap<>();

    // Инициализация контроллера
    @FXML
    public void initialize() {
        // Устанавливаем текущую дату по умолчанию
        datePicker.setValue(LocalDate.now());

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

        StringBuilder output = new StringBuilder();
        output.append("Расчет на дату: %s (12:00 UTC)\n".formatted(selectedDate));
        output.append("========================================\n");

        // Проходим по карте объектов
        for (Map.Entry<String, Integer> entry : targetPlanets.entrySet()) {
            String planetName = entry.getKey();
            int planetId = entry.getValue();

            // Получаем double координату от нашего сервиса
            double longitude = dataProvider.getPlanetLongitude(selectedDate, planetId);

            // Добавляем строку вида "Юпитер = 123.01528…°"
            output.append(String.format("%-10s = %11.5f°\n", planetName, longitude));
        }

        // Выводим собранный текст в окно
        resultTextArea.setText(output.toString());
    }

}
