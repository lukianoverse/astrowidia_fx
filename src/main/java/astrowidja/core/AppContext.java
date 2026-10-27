package astrowidja.core;

import astrowidja.model.Chart;
import astrowidja.provider.AstroDataProvider;

public class AppContext {
    private static final AppContext instance = new AppContext();
    /**
     * Поставщик данных о положениях небесных тел.
     */
    private final AstroDataProvider dataProvider = new AstroDataProvider();
    /**
     * Реестр астросущностей, известных сейчас Приложению
     */
    private final AstroRegistry astroRegistry = new AstroRegistry();
    // TODO: акромя общего реестра астросущностей, нужна механика
    //  управления списками пользователя
    /**
     * Карта, активная в данный момент. То есть отображаемая и доступная для расчётов.
     */
    private Chart activeChart = null;

    public void setActiveChart(Chart activeChart) {
        this.activeChart = activeChart;
    }

    public Chart getActiveChart() {
        return activeChart;
    }

    /**
     * Синглетон для удержания глобальных компонентов.
     * @return сам себя одного и того же.
     */
    public static AppContext getInstance() { return instance; }

    public AstroRegistry getAstroRegistry() {
        return astroRegistry;
    }

    public AstroDataProvider getDataProvider() {
        return dataProvider;
    }

}
