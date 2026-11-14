package astrowidja.utils;

import java.util.Random;

public class IdGenerator {
    // 19.10.2026 23:03:01 MSK в секундах Unix
    private static final long AW_EPOCH_SECONDS = 1760904181L;
    private static final int MACHINE_ID = calculateMachineId();
    private static final Random RANDOM = new Random();

    // Набор прикольных буквенно-цифровых символов для соли
    private static final String ALPHABET = "abcdefghijklmopqrstuvwxyz2345678";


    public static String generate(String prefix) {
        // 1. Считаем секунды от нашей эпохи
        long currentSeconds = System.currentTimeMillis() / 1000;
        long astroSeconds = Math.max(0, currentSeconds - AW_EPOCH_SECONDS); // для красоты

        // Переводим время в компактный 16-ричный вид (например, "a4b2f1")
        String hexTime = Long.toHexString(astroSeconds);

        // 2. Генерируем 3 символа случайной Nano-соли для защиты от дублей в одну секунду
        StringBuilder salt = new StringBuilder(3);
        for (int i = 0; i < 3; i++)
            salt.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));

        // 3. Собираем финальную строку: префикс + время + машина + соль
        // Пример: wrk_5b2a1_42_z7b
        return "%s_%s_%02d_%s".formatted(prefix, hexTime, MACHINE_ID, salt.toString());
    }

    private static int calculateMachineId() {
        // Собираем всё, до чего Сатурн может дотянуться без ошибок
        String fingerprint = String.join("-",
                System.getProperty("user.name", "astrolog"),
                System.getProperty("os.name", "systema"),
                System.getProperty("os.arch", "technika"),
                // Проверяем имя компьютера для Windows, если null — пробуем для Linux, если null — пишем "хуй"
                System.getenv("COMPUTERNAME") != null ? System.getenv("COMPUTERNAME") :
                        (System.getenv("HOSTNAME") != null ? System.getenv("HOSTNAME") : "sex")
        );

        // Переводим строковый отпечаток в число от 00 до 99
        return Math.abs(fingerprint.hashCode()) % 100;
    }

}
