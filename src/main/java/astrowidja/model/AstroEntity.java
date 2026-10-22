package astrowidja.model;

import java.util.*;
import java.util.function.Supplier;

/**
 * Используемые в АстроВидье астросущности, имеющие набор псевдонимов
 * и символ для отображения.
 * <p>
 * {@code Астросущность} соответствует какому-то небесному телу или точке,
 * рассматриваемым в астрологии.
 * В то время как {@code Астра} соответствует конкретному телу
 * с реальной координатой в определённый момент времени.
 * Каждый объект Астра в программе обычно связан с определённой Астросущностью.
 * <p>
 * Данная реализация также содержит расширимый Реестр Астросущностей,
 * используемых Программой.
 */
public class AstroEntity {

    /**
     * Глобальный динамический реестр известных Программе астросущностей.
     */
    private static final List<AstroEntity> registry = new ArrayList<>();

    /**
     * Основное отображаемое имя астросущности в АстроВидье.
     */
    public final String name;

    /**
     * Строковый астрологический символ (глиф) для отображения.
     * Использование String вместо char введено для поддержки современных
     * суррогатных пар Юникода (глифы Плутона '⯓' или Седны '🝼').
     */
    public final String symbol;

    /**
     * Набор альтернативных названий и псевдонимов сущности.
     * Открыт к изменениям в процессе работы программы.
     */
    public final Set<String> also;

    /**
     * Динамический поставщик идентификатора для Swiss Ephemeris.
     * Позволяет на лету разрешать коллизии Узлов и Аспид
     * в зависимости от глобальных настроек приложения.
     */
    private final Supplier<Integer> idProvider;

    /**
     * Полный конструктор для создания астросущности с динамическим поставщиком ID.
     * Автоматически регистрирует созданный объект в глобальном реестре системы.
     */
    public AstroEntity(String name, String symbol, Supplier<Integer> idProvider, String... also) {
        this.name = name;
        this.symbol = symbol;
        this.idProvider = idProvider;
        this.also = new HashSet<>();
        Collections.addAll(this.also, also);
        registry.add(this);
    }

    /**
     * Удобный облегченный конструктор для обычных планет. В качестве ID
     * берётся фиксированный статический идентификатор по Swiss Ephemerides.
     */
    public AstroEntity(String name, String symbol, int staticSwissId, String... also) {
        this(name, symbol, () -> staticSwissId, also);
    }

    /**
     * Возвращает актуальный на момент вызова идентификатор тела для Швейцарских Эфемерид.
     */
    public int getSwissId() {
        return idProvider.get();
    }

    /**
     * Возвращает список всех зарегистрированных в системе астросущностей.
     */
    public static List<AstroEntity> getRegistry() {
        return registry;
    }

}
