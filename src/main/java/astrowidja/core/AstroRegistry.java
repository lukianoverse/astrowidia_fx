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

    public static final int NO_SWISS_ID = -1;

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
            if (entity.name.equalsIgnoreCase(name)) {
                return Optional.of(entity);
            }
            for (String alias : entity.also) {
                if (alias.equalsIgnoreCase(name)) {
                    return Optional.of(entity);
                }
            }
            if (entity.symbol.equals(name)) {
                return Optional.of(entity);
            }
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
     * Проверяет уникальность астросущности в Реестре.
     * <p>
     * Метод удостоверяет, что:
     * <ul>
     *   <li>внутри сущности нет дубликатов псевдонимов (без учёта регистра);</li>
     *   <li>имя сущности не совпадает с именем, псевдонимом или символом другой сущности;</li>
     *   <li>символ сущности не совпадает с символом, именем или псевдонимом другой сущности;</li>
     *   <li>псевдонимы сущности не совпадают с именами, символами или псевдонимами других сущностей;</li>
     *   <li>SwissID сущности уникален.</li>
     * </ul>
     * При нарушении уникальности выбрасывается {@link IllegalArgumentException}.
     *
     * @param entity проверяемая астросущность
     * @throws IllegalArgumentException если обнаружен дубликат
     */
    private void validateUniqueness(AstroEntity entity) {
        // Проверка уникальности псевдонимов внутри одной сущности
        if (entity.also.size() != entity.also.stream()
                .map(String::toLowerCase)
                .distinct()
                .count()) throw new IllegalArgumentException(
                "Дубликат псевдонима внутри сущности: " + entity.name);

        for (AstroEntity existing : registry) {
            // Проверка имени на совпадение с именем другой сущности
            if (existing.name.equalsIgnoreCase(entity.name))
                throw new IllegalArgumentException(
                    "Дубликат имени: " + entity.name);
            // Проверка имени на совпадение с чужим псевдонимом
            for (String alias : existing.also)
                if (alias.equalsIgnoreCase(entity.name))
                    throw new IllegalArgumentException(
                            "Имя " + entity.name + " совпадает с псевдонимом " + existing.name);
            // Проверка имени на совпадение с чужим символом
            if (existing.symbol != null && existing.symbol.equalsIgnoreCase(entity.name))
                throw new IllegalArgumentException(
                        "Имя " + entity.name + " совпадает с символом " + existing.name);
            // Проверка символов
            if (entity.symbol != null && existing.symbol != null
                    && entity.symbol.equalsIgnoreCase(existing.symbol))
                throw new IllegalArgumentException(
                    "Дубликат символа: " + entity.symbol);
            // Проверка символов на совпадение с чужим псевдонимом
            for (String alias : existing.also)
                if (entity.symbol != null && entity.symbol.equalsIgnoreCase(alias))
                    throw new IllegalArgumentException(
                            "Символ " + entity.symbol + " совпадает с псевдонимом " + existing.name);
            // Проверка псевдонимов на совпадение с чужим именем
            for (String alias : entity.also)
                if (alias.equalsIgnoreCase(existing.name))
                    throw new IllegalArgumentException(
                        "Псевдоним " + alias + " совпадает с именем " + existing.name);
            // Проверка псевдонимов на совпадение с чужим символом
            if (existing.symbol != null)
                for (String alias : entity.also)
                    if (alias.equalsIgnoreCase(existing.symbol))
                        throw new IllegalArgumentException(
                            "Псевдоним " + alias + " совпадает с символом " + existing.name);
            // Проверка псевдонимов на совпадение с чужими псевдонимами
            for (String alias : entity.also)
                for (String existingAlias : existing.also)
                    if (alias.equalsIgnoreCase(existingAlias))
                        throw new IllegalArgumentException(
                            "Псевдоним " + alias + " совпадает с псевдонимом " + existing.name);
            // Проверка SwissID
            if (entity.getSwissId() != NO_SWISS_ID && entity.getSwissId() == existing.getSwissId())
                throw new IllegalArgumentException(
                    "Дубликат SwissID: " + entity.getSwissId() +
                            " (" + entity.name + " и " + existing.name + ")");
        }
    }

    /**
     * Базовый набор планет для инициализации реестра.
     */
    private static final AstroEntity[] DEFAULT_ASTRAS = {
        new AstroEntity("Солнце", "☉", SE_SUN, "Sun", "Sol"),
        new AstroEntity("Луна", "☽", SE_MOON, "Moon"),
        new AstroEntity("Меркурий", "☿", SE_MERCURY, "Mercury"),
        new AstroEntity("Венера", "♀", SE_VENUS, "Venus"),
        new AstroEntity("Марс", "♂", SE_MARS, "Mars"),
        new AstroEntity("Юпитер", "♃", SE_JUPITER, "Jupiter"),
        new AstroEntity("Сатурн", "♄", SE_SATURN, "Saturn"),
        new AstroEntity("Уран", "♅", SE_URANUS, "Uranus"),
        new AstroEntity("Нептун", "♆", SE_NEPTUNE, "Neptune"),
//        new AstroEntity("Церера", "⚳", SE_AST_OFFSET + 1, "Ceres"),
//        new AstroEntity("Хирон", "⚷", SE_AST_OFFSET + 2060, "Chiron"),
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
