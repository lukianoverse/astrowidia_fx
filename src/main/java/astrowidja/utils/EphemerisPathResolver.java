package astrowidja.utils;

public class EphemerisPathResolver {

    /**
     * Вычисляет относительный путь к файлу астероида по его официальному номеру.
     * @param asteroidNumber официальный номер IAU (например: 16, 90377, 136199)
     * @return строка пути вида "ast136/se136199.se1" или "ast0/se00016.se1"
     */
    public static String resolveAsteroidPath(int asteroidNumber) {
        // 1. Находим номер папки (целочисленное деление на 1000)
        // Для 16 -> 0, для 90377 -> 90, для 136199 -> 136
        int folderNumber = asteroidNumber / 1000;

        // 2. Форматируем номер астероида, чтобы он был строго 5-значным с ведущими нулями
        // %05d превратит 16 в "00016", а 136199 оставит "136199" (так как в нем уже больше 5 знаков)
        String formattedNumber = String.format("%05d", asteroidNumber);

        // 3. Собираем всё в итоговый относительный путь по стандарту Swiss Ephemeris
        return "ast" + folderNumber + "/se" + formattedNumber + ".se1";
    }

    // Быстрый тест для проверки векторов
    public static void main(String[] args) {
        System.out.println(resolveAsteroidPath(16));     // ast0/se00016.se1  (Психея)
        System.out.println(resolveAsteroidPath(7066));   // ast7/se07066.se1  (Несс)
        System.out.println(resolveAsteroidPath(90377));  // ast90/se90377.se1 (Седна)
        System.out.println(resolveAsteroidPath(136199)); // ast136/se136199.se1 (Эрида)
    }
}
