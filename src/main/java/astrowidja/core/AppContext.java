package astrowidja.core;

import astrowidja.model.AstroEntity;
import astrowidja.model.Chart;
import astrowidja.provider.AstroDataProvider;

import java.util.ArrayList;
import java.util.List;
import static swisseph.SweConst.*;

public class AppContext {
    private static final AppContext instance = new AppContext();
    /**
     * Поставщик данных о положениях небесных тел.
     */
    private final AstroDataProvider dataProvider = new AstroDataProvider();
    /**
     * Реестр астросущностей, известных сейчас Приложению
     */
    private final List<AstroEntity> astroRegistry = new ArrayList<>();
    /**
     * Карта, активная в данный момент. То есть отображаемая и доступная для расчётов.
     */
    private Chart activeChart = null;

    /**
     * Идентифицирует и выдаёт астросущность по имени.
     * В качестве имени может быть использовано основное
     * или любое из альтернативных имён, а также символ сущности.
     *
     * @param name обозначение астры, по которому идентифицируется сущность.
     * @return идентифицированная астросущность или {@code null},
     *         если астросущность не идентифицирована.
     */
    public static AstroEntity getEntityByName(String name) {
        if (name == null) return null;
        name = name.trim();
        for (AstroEntity s : instance.getAstroRegistry()) {
            if (s.name.equalsIgnoreCase(name))
                return s;
            if (s.symbol.equals(name))
                return s;
            for (String alt : s.also)
                if (alt.equalsIgnoreCase(name))
                    return s;
        }
        return null;
    }

    /**
     * Находит и отдаёт символ, связанный с астросущностью, какое-то
     * имя которой передано в аргументе и распознано {@link #getEntityByName(String)}.
     *
     * @param name имя, псевдоним или символ астры.
     * @return символ астросущности, распознанной в астре, или "*", если не распознано.
     */
    public static String findSymbolFor(String name) {
        AstroEntity astra = getEntityByName(name);
        return astra != null ? astra.symbol : "*";
    }


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
    private boolean isInitialized = false;

    public List<AstroEntity> getAstroRegistry() {
        ensureInitialized();
        return astroRegistry;
    }
    // Флаг ленивой инициализации

    private AppContext() {}

    /**
     * Ленивая инициализация базового астрологического набора.
     * Вызывается автоматически при первом запросе.
     */
    private void ensureInitialized() {
        if (isInitialized) return;

        astroRegistry.add(new AstroEntity("Солнце", "☉", SE_SUN, "Sun", "Sol"));
        astroRegistry.add(new AstroEntity("Луна", "☽", SE_MOON, "Moon"));
        astroRegistry.add(new AstroEntity("Меркурий", "☿", SE_MERCURY, "Mercury"));
        astroRegistry.add(new AstroEntity("Венера", "♀", SE_VENUS, "Venus"));
        astroRegistry.add(new AstroEntity("Марс", "♂", SE_MARS, "Mars"));
//        masterRegistry.add(new AstroEntity("Церера", "⚳", SE_AST_OFFSET + 1, "Ceres"));
        astroRegistry.add(new AstroEntity("Юпитер", "♃", SE_JUPITER, "Jupiter"));
        astroRegistry.add(new AstroEntity("Сатурн", "♄", SE_SATURN, "Saturn"));
//        masterRegistry.add(new AstroEntity("Хирон", "⚷", SE_AST_OFFSET + 2060, "Chiron"));
        astroRegistry.add(new AstroEntity("Уран", "♅", SE_URANUS, "Uranus"));
        astroRegistry.add(new AstroEntity("Нептун", "♆", SE_NEPTUNE, "Neptune"));
//        masterRegistry.add(new AstroEntity("Плутон", "⯓", SE_PLUTO, "Pluto"));

//        masterRegistry.add(new AstroEntity("Раху", "☊",
//                () -> useTrueNode ? SE_TRUE_NODE : SE_MEAN_NODE, "Rahu", "North Node"));
//
//        masterRegistry.add(new AstroEntity("Лилит", "⚸",
//                () -> useTrueLilith ? SE_OSCU_APOGEE : SE_MEAN_APOGEE, "Lilith", "Black Moon"));

        isInitialized = true;
    }

    public AstroDataProvider getDataProvider() {
        return dataProvider;
    }

}
