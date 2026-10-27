package astrowidja.model;

import java.util.*;
import java.util.function.Supplier;

/**
 * Используемая в АстроВидье астросущность, имеющая набор псевдонимов
 * и символ для отображения.
 * <p>
 * {@code Астросущность} соответствует какому-то небесному телу или точке,
 * рассматриваемым в астрологии.
 * В то время как {@code Астра} соответствует конкретному телу
 * с реальной координатой в определённый момент времени.
 * Каждый объект Астра в программе обычно связан с определённой Астросущностью.
 */
public class AstroEntity {

    public static final int NO_SWISS_ID = -1;
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
    }

    /**
     * Удобный облегчённый конструктор для обычных планет. В качестве ID
     * берётся фиксированный статический идентификатор по Swiss Ephemerides.
     */
    public AstroEntity(String name, String symbol, int staticSwissId, String... also) {
        this(name, symbol, () -> staticSwissId, also);
    }

    /**
     * Возвращает актуальный на момент вызова идентификатор тела для Швейцарских Эфемерид.
     * Если поставщик выдал {@code null}, возвращает {@code NO_SWISS_ID}.
     */
    public int getSwissId() {
        Integer id = idProvider.get();
        return id != null ? id : NO_SWISS_ID;
    }


    /**
     * Добавляет псевдоним с проверкой уникальности внутри сущности.
     * <p>
     * Проверяет, что псевдоним не дублирует существующие псевдонимы
     * этой же сущности (без учёта регистра).
     *
     * @param alias псевдоним для добавления
     * @throws IllegalArgumentException если псевдоним уже существует
     */
    public void addAlias(String alias) {
        if (alias == null || alias.trim().isEmpty())
            throw new IllegalArgumentException("Псевдоним не может быть пустым");

        if (this.also.stream().anyMatch(a -> a.equalsIgnoreCase(alias)))
            throw new IllegalArgumentException(
                "Псевдоним уже существует: " + alias);

        this.also.add(alias);
    }

    /**
     * Удаляет псевдоним из сущности.
     *
     * @param alias псевдоним для удаления
     * @return {@code true} если псевдоним был удалён
     */
    public boolean removeAlias(String alias) {
        return this.also.remove(alias);
    }

    /**
     * Заменяет один псевдоним на другой.
     *
     * @param oldAlias старый псевдоним
     * @param newAlias новый псевдоним
     * @throws IllegalArgumentException если старый псевдоним не найден или новый конфликтует
     */
    public void setAlias(String oldAlias, String newAlias) {
        if (!this.also.remove(oldAlias))
            throw new IllegalArgumentException("Псевдоним не найден: " + oldAlias);

        try {
            addAlias(newAlias);
        } catch (IllegalArgumentException e) {
            this.also.add(oldAlias);
            throw e;
        }
    }

    /**
     * Проверяет эту сущность на конфликт с другой сущностью.
     * <p>
     * Конфликт возникает, если у сущностей есть хотя бы один общий дескриптор
     * (имя, символ или псевдоним), либо если заданный SwissID совпадает (за
     * исключением значения NO_SWISS_IS, равного -1).
     *
     * @param other проверяемая сущность
     * @return строка с общими дескрипторами и/или SwissID, разделёнными пробелами;
     *         пустая строка, если конфликтов нет
     */

    public String interferes(AstroEntity other) {
        Set<String> descriptors = gatherDescriptors(this);
        Set<String> otherDescriptors = gatherDescriptors(other);
        descriptors.retainAll(otherDescriptors);

        if (getSwissId() != NO_SWISS_ID && getSwissId() == other.getSwissId())
            descriptors.add("SwissID" + getSwissId());

        return String.join(" ", descriptors);
    }

    private static Set<String> gatherDescriptors(AstroEntity entity) {
        Set<String> set = new HashSet<>(entity.also);
        set.add(entity.name);
        set.add(entity.symbol);
        set.remove(null);
        return set;
    }

}
