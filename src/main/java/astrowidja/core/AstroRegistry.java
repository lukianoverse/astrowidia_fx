package astrowidja.core;

import astrowidja.model.AstroEntity;
import astrowidja.model.LilithType;
import astrowidja.model.NodeType;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static swisseph.SweConst.*;

/**
 * Управление реестром астросущностей, известных Приложению.
 */
public class AstroRegistry {

    private final List<AstroEntity> registry = new ArrayList<>();

    // Массив объектов, доступных для расчета без файлов эфемерид (по Мошьеру)
    public static final int[] MOSHIERABLE = {
            SE_SUN,
            SE_MOON,
            SE_MERCURY,
            SE_VENUS,
            SE_MARS,
            SE_JUPITER,
            SE_SATURN,
            SE_URANUS,
            SE_NEPTUNE,
            SE_PLUTO,
            SE_MEAN_NODE,
            SE_MEAN_APOG
    };

    public List<AstroEntity> getEntitiesToCalculate(ObservableList<String> astrasToCalculate) {
        return astrasToCalculate.stream()
                .map(this::findByName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    /**
     * Возвращает список всех зарегистрированных астросущностей.
     */
    public List<AstroEntity> getAll() {
        return List.copyOf(registry);
    }

    /**
     * Находит сущность по имени, псевдониму или символу.
     */
    public Optional<AstroEntity> findByName(String name) {
        if (name == null) return Optional.empty();
        return registry.stream()
                .filter(entity -> entity.matches(name.trim()))
                .findFirst();
    }

    /**
     * Возвращает символ сущности по имени, или "*" если не найдена.
     */
    public String findSymbolFor(String name) {
        return findByName(name)
                .map(AstroEntity::getSymbol)
                .orElse("*");
    }

    /**
     * Регистрирует новую сущность с проверкой уникальности.
     * @throws IllegalArgumentException если имя, символ или псевдоним уже используются
     */
    public void register(AstroEntity entity) {
        validateUniqueness(entity);
        registry.add(entity);
    }

    /**
     * Обновляет реестр: удаляет конфликтующие сущности и добавляет новую.
     * <p>
     * Если новая сущность конфликтует с существующей, конфликтующая удаляется
     * и на её место добавляется новая. Если конфликтов нет — просто добавляет.
     *
     * @param entity обновляемая сущность
     */
    public void update(AstroEntity entity) {
        // Сначала удаляем конфликтующие сущности
        registry
                .removeIf(existing -> !entity.interferes(existing).isBlank());
        // Затем добавляем новую
        registry.add(entity);
    }

    /**
     * Проверяет уникальность астросущности в Реестре.
     * <p>
     * Метод удостоверяет, что сущность не конфликтует с другими сущностями
     * (имена, символы, псевдонимы, SwissID).
     * При нарушении уникальности выбрасывается {@link IllegalArgumentException}
     * с перечислением найденных интерференций.
     *
     * @param entity проверяемая астросущность
     * @throws IllegalArgumentException если обнаружен дубликат
     */
    private void validateUniqueness(AstroEntity entity) {
        for (AstroEntity existing : registry) {
            String conflict = entity.interferes(existing);
            if (!conflict.isBlank()) throw new IllegalArgumentException(
                    "Конфликт определения сущности \"%s\" с сущностью \"%s\":\n%s"
                            .formatted(entity.getName(), existing.getName(), conflict));
        }
    }

    /**
     * Основные планеты (без эфемеридных файлов — расчёт по Мошьеру).
     */
    private static final AstroEntity[] MAIN_ASTRAS = {
        new AstroEntity("Солнце", "☉", SE_SUN, "Sun", "Sol", "Сурья", "Рави"),
        new AstroEntity("Луна", "☽", SE_MOON, "Moon", "Чандра"),
        new AstroEntity("Меркурий", "☿", SE_MERCURY, "Mercury", "Буддха"),
        new AstroEntity("Венера", "♀", SE_VENUS, "Venus", "Шукра"),
        new AstroEntity("Марс", "♂", SE_MARS, "Mars", "Мангала"),
        new AstroEntity("Церера", "⚳", SE_AST_OFFSET + 1, "Ceres"),
        new AstroEntity("Юпитер", "♃", SE_JUPITER, "Jupiter", "Гуру", "Брихаспати"),
        new AstroEntity("Сатурн", "♄", SE_SATURN, "Saturn", "Шани"),
        new AstroEntity("Хирон", "⚷", SE_AST_OFFSET + 2060, "Chiron"),
        new AstroEntity("Уран", "♅", SE_URANUS, "Uranus"),
        new AstroEntity("Нептун", "♆", SE_NEPTUNE, "Neptune"),
        new AstroEntity("Плутон", "⯓", SE_PLUTO, "Pluto")
    };

    /**
     * Лунные точки: узлы и аспиды (Лилит).
     * SwissID разрешается динамически на основе настроек AppContext.
     */
    private static final AstroEntity[] LUNAR_POINTS = {
        new AstroEntity("Раху", "☊", () -> {
            NodeType type = AppContext.getInstance().getCurrentSettings().getNodeType();
            return (type == NodeType.TRUE) ? SE_TRUE_NODE : SE_MEAN_NODE;
        }, "Rahu", "Северный Узел", "Голова Дракона", "North Node"),
        new AstroEntity("Лилит", "⚸", () -> {
            LilithType type = AppContext.getInstance().getCurrentSettings().getLilithType();
            return type == LilithType.TRUE ? SE_OSCU_APOG :
                    type == LilithType.INTERPOLATED ? SE_INTP_APOG : SE_MEAN_APOG;
        }, "Lilith", "Чёрная Луна", "Black Moon")
    };

    /**
     * Кентавры: малые тела с орбитами между Юпитером и Нептуном.
     */
    private static final AstroEntity[] CENTAURS = {
        // TODO: добавить Фол, Асбол, Несс и др.
    };

    /**
     * Астероиды: основные астероиды 1-й группы.
     */
    private static final AstroEntity[] ASTEROIDS = {
        // TODO: добавить Палладу, Юнону, Весту и др.
    };

    /**
     * Пользовательские сущности — добавляются/удаляются в runtime.
     * Состояние сохраняется в файл и загружается при старте.
     */
    private final List<AstroEntity> userDefinedAstras = new ArrayList<>();

    /**
     * Загрузка пользовательских сущностей из хранилища.
     */
    public void loadUserDefinedAstras() {
        // TODO: загрузить из файла/хранилища и populate userDefinedAstras
    }

    /**
     * Сохранение пользовательских сущностей в хранилище.
     */
    public void saveUserDefinedAstras() {
        // TODO: сохранить userDefinedAstras в файл
    }

    /**
     * Регистрация пользовательской сущности.
     */
    public void registerUserDefined(AstroEntity entity) {
        register(entity);
        userDefinedAstras.add(entity);
    }

    /**
     * Инициализация реестра: поочерёдная регистрация всех категорий.
     */
    public void init() {
        Arrays.stream(MAIN_ASTRAS).forEach(this::register);
        Arrays.stream(LUNAR_POINTS).forEach(this::register);
        Arrays.stream(CENTAURS).forEach(this::register);
        Arrays.stream(ASTEROIDS).forEach(this::register);
        loadUserDefinedAstras();
        userDefinedAstras.forEach(this::register);
    }
}
