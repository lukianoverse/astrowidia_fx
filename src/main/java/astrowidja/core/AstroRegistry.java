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

    private void validateUniqueness(AstroEntity entity) {
        // Собираем все имена, символы и псевдонимы, уже занятые другими сущностями
        for (AstroEntity existing : registry) {
            // Проверка имени
            if (existing.name.equalsIgnoreCase(entity.name)) {
                throw new IllegalArgumentException(
                    "Дубликат имени: " + entity.name);
            }
            // Проверка псевдонимов на совпадение с чужим именем
            for (String alias : entity.also) {
                if (existing.name.equalsIgnoreCase(alias)) {
                    throw new IllegalArgumentException(
                        "Псевдоним " + alias + " совпадает с именем " + existing.name);
                }
            }
            // Проверка символов
            if (entity.symbol != null && existing.symbol != null
                    && entity.symbol.equals(existing.symbol)) {
                throw new IllegalArgumentException(
                    "Дубликат символа: " + entity.symbol);
            }
        }
        // Проверка уникальности псевдонимов внутри одной сущности
        for (int i = 0; i < entity.also.size(); i++) {
            for (int j = i + 1; j < entity.also.size(); j++) {
                if (entity.also.toArray()[i].equals(entity.also.toArray()[j])) {
                    throw new IllegalArgumentException(
                        "Дубликат псевдонима: " + entity.also.toArray()[i]);
                }
            }
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
        initDefaults();
    }

    /**
     * Инициализирует базовый набор планет.
     */
    private void initDefaults() {
        for (AstroEntity entity : DEFAULT_ASTRAS) {
            register(entity);
        }
    }
}
