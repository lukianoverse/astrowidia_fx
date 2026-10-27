package astrowidja.core;

import astrowidja.model.AstroEntity;

import java.util.ArrayList;
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
        name = name.trim();
        for (AstroEntity entity : registry) {
            if (entity.name.equalsIgnoreCase(name))
                return Optional.of(entity);
            if (entity.symbol.equals(name))
                return Optional.of(entity);
            for (String alias : entity.also)
                if (alias.equalsIgnoreCase(name))
                    return Optional.of(entity);
        }
        return Optional.empty();
    }

    /**
     * Возвращает символ сущности по имени, или "*" если не найдена.
     */
    public String findSymbolFor(String name) {
        return findByName(name).map(entity -> entity.symbol).orElse("*");
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
                            .formatted(entity.name, existing.name, conflict));
        }
    }

    /**
     * Базовый набор планет для инициализации реестра.
     */
    private static final AstroEntity[] DEFAULT_ASTRAS = {
        new AstroEntity("Солнце", "☉", SE_SUN, "Sun", "Sol", "Сурья", "Рави"),
        new AstroEntity("Луна", "☽", SE_MOON, "Moon", "Чандра"),
        new AstroEntity("Меркурий", "☿", SE_MERCURY, "Mercury", "Буддха"),
        new AstroEntity("Венера", "♀", SE_VENUS, "Venus", "Шукра"),
        new AstroEntity("Марс", "♂", SE_MARS, "Mars", "Мангала"),
//        new AstroEntity("Церера", "⚳", SE_AST_OFFSET + 1, "Ceres"),
        new AstroEntity("Юпитер", "♃", SE_JUPITER, "Jupiter", "Гуру", "Брихаспати"),
        new AstroEntity("Сатурн", "♄", SE_SATURN, "Saturn", "Шани"),
//        new AstroEntity("Хирон", "⚷", SE_AST_OFFSET + 2060, "Chiron"),
        new AstroEntity("Уран", "♅", SE_URANUS, "Uranus"),
        new AstroEntity("Нептун", "♆", SE_NEPTUNE, "Neptune"),
//        new AstroEntity("Плутон", "⯓", SE_PLUTO, "Pluto"),
//        new AstroEntity("Раху", "☊",
//                () -> useTrueNode ? SE_TRUE_NODE : SE_MEAN_NODE, "Rahu", "North Node"),
//        new AstroEntity("Лилит", "⚸",
//                () -> useTrueLilith ? SE_OSCU_APOGEE : SE_MEAN_APOGEE, "Lilith", "Black Moon")
    };

    public AstroRegistry() {
        for (AstroEntity entity : DEFAULT_ASTRAS)
            register(entity);
    }
}
