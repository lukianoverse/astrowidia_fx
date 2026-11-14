package astrowidja.provider;

import astrowidja.model.Astra;

import java.util.*;

/**
 * Вспомогательный класс для построения отчёта о пакетном расчёте объектов {@link Astra}.
 * <p>
 * Отчётник анализирует результаты вычисленных эфемерид ({@link SweCalcResult}) каждой астры
 * и группирует их согласно исходу:
 * <ul>
 *   <li><b>Ошибки</b> — астры, вычисление которых не получилось (статус -1), группируются
 *       по сообщению об ошибке.</li>
 *   <li><b>Предупреждения</b> — астры, флаговая маска результата вычисления которых
 *       отличается от входной, группируются по итоговой маске с человекочитаемым
 *       пояснением, какие флаги были сняты или добавлены.</li>
 * </ul>
 * <p>
 * Значения флагов соответствуют конвенции Swiss Ephemeris:
 * 1 — JPL, 2 — SwissEph, 4 — Moshier, 8 — гелиоцентр, 16 — истинное положение,
 * 64 — без нутации, 256 — скорость, 32768 — топоцентр, 65536 — сидерически,
 * 268435456 — математические орбиты (ERRDFT).
 * <p>
 * Результат оформляется в {@link CalculationReport}, содержащий форматированные тексты
 * ошибок и предупреждений, или пустой отчёт, если нет ни ошибок, ни предупреждений.
 */
public class AstraBatchReporter {

    private static final Map<Integer, String> FLAGS = Map.of(
        1, "JPL",
        2, "SwissEph",
        4, "Moshier",
        8, "Гелиоцентр",
        16, "Истинное положение",
        64, "Без нутации",
        256, "Скорость",
        32768, "Топоцентр",
        65536, "Сидерически",
        268435456, "Математические орбиты (ERRDFT)"
    );

    /**
     * Создаёт отчёт о вычислениях для данной коллекции астр.
     * <p>
     * Результат вычисления эфемерид каждой астры проверяется по запрошенным флагам:
     * неуспешные вычисления (отрицательный статус) группируются по сообщению об ошибке,
     * а несовпадающие файловые маски группируются как предупреждения.
     *
     * @param requestFlags битовая маска флагов вычисления, переданная с запросом.
     * @param astras       коллекция астр для анализа; может быть {@code null} или пустой,
     *                     тогда возвращается пустой отчёт.
     * @return {@link CalculationReport} с оформленными текстами ошибок и предупреждений.
     */
    public static CalculationReport build(int requestFlags, Collection<Astra> astras) {
        if (astras == null || astras.isEmpty())

            return new CalculationReport("", "");

        int astrasCount = astras.size();

        // Группировка: Текст ошибки -> Список имён астр
        Map<String, List<String>> errorsByMessage = new LinkedHashMap<>();
        // Группировка: Несоответствие маски -> Список имён астр
        Map<Integer, List<String>> warningsByMask = new LinkedHashMap<>();

        for (Astra astra : astras) {
            SweCalcResult res = astra.getEphemeris();
            if (res == null) continue;

            if (res.status() < 0) {
                String errMsg = (res.serr() == null || res.serr().isBlank()) 
                        ? "Неизвестная ошибка расчёта"
                        : res.serr().trim();
                errorsByMessage
                        .computeIfAbsent(errMsg, k -> new ArrayList<>())
                        .add(astra.getName());
            } else if (res.status() != requestFlags) {
                warningsByMask
                        .computeIfAbsent(res.status(), k -> new ArrayList<>())
                        .add(astra.getName());
            }
        }

        String errorText = formatGroupedErrors(errorsByMessage, astrasCount);
        String warningText = formatGroupedWarnings(warningsByMask, requestFlags, astrasCount);

        return new CalculationReport(errorText, warningText);
    }

    /**
     * Форматирует сгруппированные ошибки вычислений в многострочный текст.
     * <p>
     * Каждая строка имеет вид {@code "• <затронутые>: <сообщениеОбОшибке>"}, где затронутые это
     * или "Для всех астр" (если все астры попали в одну ошибку), или разделённый запятыми
     * список имён затронутых астр.
     *
     * @param grouped     карта сообщений об ошибках, которым сопоставлены списки вызвавших их астр
     * @param astrasCount общее количество астр в пакете, используемое для определения случая "все"
     * @return форматированный текст ошибки, или пустую строку, если ошибок нет
     */
    private static String formatGroupedErrors(Map<String, List<String>> grouped, int astrasCount) {
        if (grouped.isEmpty()) return "";
        
        List<String> lines = new ArrayList<>();
        for (var entry : grouped.entrySet()) {
            String errorMessage = entry.getKey();
            List<String> names = entry.getValue();

            String target = (names.size() == astrasCount)
                    ? "Для всех астр"
                    : "Для " + String.join(", ", names);

            lines.add("• " + target + ": " + errorMessage);
        }
        return String.join("\n", lines);
    }

    /**
     * Форматирует сгруппированные предупреждения об изменении флаговой маски в многострочный текст.
     * <p>
     * Каждая строка имеет форму {@code "• <затронутые>: <пояснение>"}, где пояснение генерируется
     * методом {@link #explainDiff(int, int)}, описывающим разницу между исходными и итоговыми флагами.
     *
     * @param grouped     карта полученных итоговых масок к списку астр, вернувших их
     * @param requested   битовая маска флагов, которые передавались изначально
     * @param astrasCount общее количество астр в пакете, используемое для определения случая "все"
     * @return форматированный текст предупреждения, или пустую строку, если предупреждений не было
     */
    private static String formatGroupedWarnings(Map<Integer, List<String>> grouped, int requested, int astrasCount) {
        if (grouped.isEmpty()) return "";

        List<String> lines = new ArrayList<>();
        for (var entry : grouped.entrySet()) {
            int actualFlags = entry.getKey();
            List<String> names = entry.getValue();

            String target = (names.size() == astrasCount)
                    ? "Для всех астр"
                    : "Для " + String.join(", ", names);

            lines.add("• " + target + ": " + explainDiff(requested, actualFlags));
        }
        return String.join("\n", lines);
    }

    /**
     * Производит человекочитаемое объяснение разницы между исходными и итоговыми
     * флаговыми масками вычисления.
     * <p>
     * Особый случай: если SwissEph (2) запрошены, но использован Moshier (4), сообщение
     * указывает, что файлы эфемерид не были найдены, и применён откат к Мошьеру.
     * В ином случае, список перечисляет, какие флаги были "отключены", а какие
     * "включены", опираясь на таблицу {@code FLAGS}.
     *
     * @param req битовая флаговая маска запроса
     * @param act битовая флаговая маска, фактически возвращённая вычислением
     * @return форматированную строку пояснения с точкой в конце
     */
    private static String explainDiff(int req, int act) {
        if ((req & 2) != 0 && (act & 4) != 0) {
            return "не найдены файлы эфемерид, расчёт выполнен по Мошьеру.";
        }

        int diff = req ^ act;
        List<String> dropped = new ArrayList<>();
        List<String> added = new ArrayList<>();

        for (var flag : FLAGS.entrySet()) {
            int bit = flag.getKey();
            if ((diff & bit) != 0) {
                if ((req & bit) != 0) dropped.add(flag.getValue());
                else added.add(flag.getValue());
            }
        }

        List<String> parts = new ArrayList<>();
        if (!dropped.isEmpty())
            parts.add("отключено [%s]".formatted(String.join(", ", dropped)));
        if (!added.isEmpty())
            parts.add("включено [%s]".formatted(String.join(", ", added)));

        return parts.isEmpty()
                ? "изменились системные флаги (" + req + " -> " + act + ")."
                : String.join(", ", parts) + ".";
    }
}
