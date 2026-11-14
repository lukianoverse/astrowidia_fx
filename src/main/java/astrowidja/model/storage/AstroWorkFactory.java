package astrowidja.model.storage;

import astrowidja.core.AppContext;
import astrowidja.core.AstroRegistry;
import astrowidja.core.Settings;
import astrowidja.exception.AstroCalculationException;
import astrowidja.fx.BottomToastBanner;
import astrowidja.model.TimeSpacePoint;
import astrowidja.model.TimeSpacePointFactory;
import astrowidja.provider.AstroDataProvider;
import astrowidja.provider.SituationCalcResult;
import javafx.scene.control.Alert;

public class AstroWorkFactory {
    private static final Settings currentSettings = AppContext.getInstance().getCurrentSettings();
    private static final AstroRegistry registry = AppContext.getInstance().getAstroRegistry();
    private static final AstroDataProvider astroEngine = AppContext.getInstance().getDataProvider();

    public static WorkCreationOutcome createForNow() {
        AstroWork work = new AstroWork("Небо Сейчас");
        TimeSpacePoint moment = TimeSpacePointFactory.createForNow();
        AstroSituation situation = new NaturalSituation(moment, currentSettings.getCurrentModus());
        SituationCalcResult calculated = astroEngine.calculateSituation(
                moment,
                currentSettings.getCurrentModus(),
                registry.getEntitiesToCalculate(),
                situation
        );
        var report = calculated.report();

        // Если критическая ошибка — прерываем сборку!
        if (calculated.hasError())
            throw new AstroCalculationException(report.criticalErrors(), report.warnings());

//        // 1. Критические ошибки — модально по центру экрана
//        if (calculated.hasError()) {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Ошибка расчёта эфемерид");
//            alert.setHeaderText("Не удалось рассчитать координаты");
//            alert.setContentText(calculated.report().criticalErrors());
//            alert.showAndWait();
//        }

        situation.addAstras(calculated.astras());

//        // 2. Предупреждения — мягко выезжают снизу
//        BottomToastBanner bottomToastBanner = new BottomToastBanner();
//        if (calculated.hasWarning())
//            bottomToastBanner.showToast(calculated.report().warnings());
//        else
//            bottomToastBanner.hideToast();

        work.addSituation(situation);

        // Возвращаем результат сборки с запиской
        return new WorkCreationOutcome(work, calculated.report().warnings());
    }

    public static AstroWork createForDate(TimeSpacePoint moment) {
        AstroWork work = new AstroWork("Небо на дату");
        AstroSituation situation = new NaturalSituation(moment, currentSettings.getCurrentModus());
        SituationCalcResult calculated = astroEngine.calculateSituation(
                moment,
                currentSettings.getCurrentModus(),
                registry.getEntitiesToCalculate(),
                situation
        );
        situation.addAstras(calculated.astras());
//        if (calculated.hasWarning()) {
//            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
//            infoAlert.setTitle("Примечания к расчёту");
//            infoAlert.setHeaderText("Некоторые астросущности рассчитаны в резервном режиме:");
//            infoAlert.setContentText(calculated.report());
//            infoAlert.show();
//        }

        work.addSituation(situation);
        return work;
    }
}
