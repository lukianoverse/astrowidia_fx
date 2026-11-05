package astrowidja.provider;

import astrowidja.core.AstroRegistry;
import swisseph.SweConst;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static swisseph.SweConst.*;

public class EpheChecker {

    // Карта для объектов, которые упакованы в групповые базовые файлы
    private static final Map<Integer, String> BASE_FILES_MAP = new HashMap<>();

    static {
        // Все основные планеты от Солнца до Плутона требуют sepl_18.se1
        String planetFile = "sepl_18.se1";
        BASE_FILES_MAP.put(SE_SUN, planetFile);
        BASE_FILES_MAP.put(SE_MERCURY, planetFile);
        BASE_FILES_MAP.put(SE_VENUS, planetFile);
        BASE_FILES_MAP.put(SE_MARS, planetFile);
        BASE_FILES_MAP.put(SE_JUPITER, planetFile);
        BASE_FILES_MAP.put(SE_SATURN, planetFile);
        BASE_FILES_MAP.put(SE_URANUS, planetFile);
        BASE_FILES_MAP.put(SE_NEPTUNE, planetFile);
        BASE_FILES_MAP.put(SE_PLUTO, planetFile);

        // Луна и её истинные факторы требуют semo_18.se1
        String moonFile = "semo_18.se1";
        BASE_FILES_MAP.put(SE_MOON, moonFile);
        BASE_FILES_MAP.put(SE_TRUE_NODE, moonFile); // Истинный Узел считается по Луне
        BASE_FILES_MAP.put(SE_OSCU_APOG, moonFile);  // Истинная Лилит считается по Луне

        // Главные астероиды требуют seas_18.se1
        String asterFile = "seas_18.se1";
        BASE_FILES_MAP.put(SE_CERES, asterFile);
        BASE_FILES_MAP.put(SE_CHIRON, asterFile);
        BASE_FILES_MAP.put(SE_PHOLUS, asterFile);
        BASE_FILES_MAP.put(SE_VESTA, asterFile);
        BASE_FILES_MAP.put(SE_PALLAS, asterFile);
        BASE_FILES_MAP.put(SE_JUNO, asterFile);

        // Интерполированная Лилит требует seft_18.se1
        BASE_FILES_MAP.put(SE_INTP_APOG, "seft_18.se1");
    }

    /**
     * Проверяет для астры по данному швейцарскому номеру, может ли
     * программа вычислять её положения по формулам или по эфемеридам,
     * если соответствующий данному телу файл есть в базе.
     */
    public static boolean isCalculatable(int swissId) {
        // 1. Если объект moshierable (Средний узел, Средняя Лилит) — файлы НЕ нужны, всегда true
        if (isMoshierable(swissId)) return true;

        Path epheFolder = Paths.get(".").resolve("ephe");

        // 2. Если объект зашит в базовый файл
        if (BASE_FILES_MAP.containsKey(swissId)) {
            return Files.exists(epheFolder.resolve(BASE_FILES_MAP.get(swissId)));
        }

        // 3. Если это малая планета или ТНО (вычисляем путь через формулу astN)
        // В SwissEphemeris ID астероидов идут со смещением SE_AST_OFFSET (10000)
        if (swissId >= SweConst.SE_AST_OFFSET) {
            int asteroidNumber = swissId - SweConst.SE_AST_OFFSET;
            int folderNumber = asteroidNumber / 1000;
            String formattedNumber = String.format("%05d", asteroidNumber);
            String relativePath = "ast" + folderNumber + "/se" + formattedNumber + ".se1";
            
            return Files.exists(epheFolder.resolve(relativePath));
        }

        return false;
    }

    private static boolean isMoshierable(int swissId) {
        return Arrays.stream(AstroRegistry.MOSHIERABLE)
                .anyMatch(id -> id == swissId);
    }
}
