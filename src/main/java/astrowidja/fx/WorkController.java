package astrowidja.fx;

import astrowidja.core.AstroRegistry;
import astrowidja.core.Settings;
import astrowidja.model.charts.AstroSituation;
import astrowidja.model.charts.AstroWork;
import astrowidja.model.charts.NaturalSituation;
import astrowidja.provider.AstroDataProvider;
import astrowidja.model.TimeSpacePoint;
import astrowidja.model.TimeSpacePointFactory;
import astrowidja.provider.SituationCalcResult;
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

        TimeSpacePoint moment = TimeSpacePointFactory.createGeocentric(
                LocalDateTime.of(selectedDate, localTime), zoneId.getId()
        );

        AstroWork work = new AstroWork("Карта");
        AstroSituation situation = new NaturalSituation();
        SituationCalcResult calculated = astroEngine.calculateSituation(
                moment,
                currentSettings.getCurrentModus(),
                registry.getEntitiesToCalculate(),
                situation
        );
        situation.addAstras(calculated.astras());
        if (calculated.hasWarning()) {
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Примечания к расчету");
            infoAlert.setHeaderText("Некоторые астросущности рассчитаны в резервном режиме:");
            infoAlert.setContentText(calculated.report());
            infoAlert.show();
        }
        work.addSituation(situation);

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
