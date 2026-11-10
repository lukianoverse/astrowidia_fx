package astrowidja.core;

import astrowidja.model.storage.AstroWork;
import astrowidja.provider.AstroDataProvider;
import astrowidja.provider.AyanamshaMode;
import astrowidja.provider.CalcMethod;
import astrowidja.provider.ProjectionPlane;
import astrowidja.provider.ReferencePoint;
import astrowidja.model.LilithType;
import astrowidja.model.NodeType;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;

import static astrowidja.provider.CalcMethod.*;

public class AppContext {
    private static AppContext instance;
    /**
     * Поставщик данных о положениях небесных тел.
     */
    private final AstroDataProvider dataProvider = new AstroDataProvider();
    /**
     * Реестр астросущностей, известных сейчас Приложению
     */
    private final AstroRegistry astroRegistry = new AstroRegistry();
    // TODO: акромя общего реестра астросущностей, нужна механика
    //  управления списками пользователя

    private final Settings currentSettings = new Settings();
    private Path settingsFilePath;
    /**
     * Карта, активная в данный момент. То есть отображаемая и доступная для расчётов.
     */
    private AstroWork activeChart = null;

    private AppContext() {
        initSettingsPath();
        loadSettings();
    }

    // TODO: добавить в настройки пути к базе данных и к эфемеридам

    private void initSettingsPath() {
        // строго локально в папке запуска программы
        Path rootDir = Paths.get(".");

        // путь к файлу настроек: ./settings.ini
        this.settingsFilePath = rootDir.resolve("settings.ini");

        // путь к репозиторию карт: ./awdata
        Path awdataFolder = rootDir.resolve("awdata");

        // путь к базе эфемерид: ./ephe
        Path epheFolder = rootDir.resolve("ephe");

        // Создадим папку для списков AWD, если её ещё нет,
        // чтобы если чо репозиторий не ругался при старте
        if (!Files.exists(awdataFolder)) {
            try {
                Files.createDirectories(awdataFolder);
                // TODO: это всё в лог, как цивилизованные люди
                System.out.println("Создана локальная папка библиотек: "
                        + awdataFolder.toAbsolutePath().normalize());
            } catch (IOException e) {
                System.err.println("Не удалось создать папку awdata: " + e.getMessage());
            }
        }
        //  и папку эфемерид пока здесь же по умолчанию
        if (!Files.exists(epheFolder)) {
            try {
                Files.createDirectories(epheFolder);
                System.out.println("Создана локальная папка эфемерид: "
                        + epheFolder.toAbsolutePath().normalize());
            } catch (IOException e) {
                System.err.println("Не удалось создать папку ephe: " + e.getMessage());
            }
        }
    }

    private void loadSettings() {
        if (!Files.exists(settingsFilePath)) {
            // Если файла нет, прога работает на дефолтах, создадим файл при выходе
            return;
        }

        try (InputStream is = Files.newInputStream(settingsFilePath);
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            Properties props = new Properties();
            props.load(isr);

            // Читаем значения из файла, если строки кривые — сработает дефолт
            if (props.containsKey("calcMethod"))
                try {
                    currentSettings.setCalcMethod(CalcMethod.valueOf(props.getProperty("calcMethod")));
                } catch (Exception e) {
                    System.err.println("[Настройки] Ошибка чтения calcMethod, оставлен дефолт: " + e.getMessage());
                }

            if (props.containsKey("referencePoint"))
                try {
                    currentSettings.setReferencePoint(ReferencePoint.valueOf(props.getProperty("referencePoint")));
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(e);
                }

            if (props.containsKey("ayanamsha"))
                try {
                    currentSettings.setAyanamsha(AyanamshaMode.valueOf(props.getProperty("ayanamsha")));
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(e);
                }

            if (props.containsKey("projectionPlane"))
                try {
                    currentSettings.setProjectionPlane(ProjectionPlane.valueOf(props.getProperty("projectionPlane")));
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(e);
                }

            if (props.containsKey("userAyanamshaOffset"))
                try {
                    currentSettings.setUserAyanamshaOffset(Double.parseDouble(props.getProperty("userAyanamshaOffset")));
                } catch (NumberFormatException e) {
                    System.err.println("[Настройки] Ошибка парсинга числа userAyanamshaOffset (неверный формат): " + e.getMessage());
                }


            if (props.containsKey("nodeType"))
                try {
                    currentSettings.setNodeType(NodeType.valueOf(props.getProperty("nodeType")));
                } catch (Exception e) {
                    System.err.println("[Настройки] Ошибка чтения nodeType, оставлен дефолт: " + e.getMessage());
                }

            if (props.containsKey("lilithType"))
                try {
                    currentSettings.setLilithType(LilithType.valueOf(props.getProperty("lilithType")));
                } catch (Exception e) {
                    System.err.println("[Настройки] Ошибка чтения lilithType, оставлен дефолт: " + e.getMessage());
                }


            // Загрузка активных астр
            if (props.containsKey("activeAstras")) {
                try {
                    String[] names = props.getProperty("activeAstras").split(",");
                    currentSettings.getAstrasToCalculate().setAll(names);
                } catch (Exception e) {
                    System.err.println("[Настройки] Ошибка разбора списка activeAstras: " + e.getMessage());
                }
            } else {
                // Набор по умолчанию
                currentSettings.getAstrasToCalculate()
                        .setAll("Солнце", "Луна", "Меркурий", "Венера", "Марс",
                                "Юпитер", "Сатурн", "Уран", "Нептун");
            }
        } catch (Exception e) {
            // TODO: заменить на уведомление
            System.err.println("Ошибка чтения настроек, используются дефолты: " + e.getMessage());
        }
    }

    public void saveSettings() {
        try {
            Files.createDirectories(settingsFilePath.getParent());
            try (OutputStream os = Files.newOutputStream(settingsFilePath);
                 OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
                 PrintWriter pw = new PrintWriter(osw)) {

                pw.println("# AstroWidja Settings");
                pw.printf("calcMethod=%s%n", currentSettings.getCalcMethod().name());
                pw.printf("referencePoint=%s%n", currentSettings.getReferencePoint().name());
                pw.printf("ayanamsha=%s%n", currentSettings.getAyanamsha().name());
                pw.printf("projectionPlane=%s%n", currentSettings.getProjectionPlane().name());
                pw.printf(Locale.ROOT,"userAyanamshaOffset=%.4f%n", currentSettings.getUserAyanamshaOffset());
                pw.printf("nodeType=%s%n", currentSettings.getNodeType().name());
                pw.printf("lilithType=%s%n", currentSettings.getLilithType().name());
                pw.printf("activeAstras=%s%n", String.join(",", currentSettings.getAstrasToCalculate()));
                pw.flush();

                System.out.println("Настройки успешно сохранены в " + settingsFilePath);
            }
        } catch (IOException e) {
            System.err.println("Не удалось сохранить настройки: " + e.getMessage());
        }
    }

    public void setActiveChart(AstroWork activeChart) {
        this.activeChart = activeChart;
    }

    public AstroWork getActiveChart() {
        return activeChart;
    }

    /**
     * Возвращает синглтон-экземпляр {@code AppContext}.
     * <p>
     * Экземпляр создаётся лениво: при первом обращении он инициализируется
     * в два этапа: сначала конструктор создаёт настройки, затем метод {@code postConstruct()}
     * иициализирует астры на основе настроек. Метод на всякий случай синхронизирован.
     *
     * @return глобальный экземпляр {@code AppContext}
     */
    public static synchronized AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
            instance.postConstruct();
        }
        return instance;
    }

    private void postConstruct() {
        // инициализируем набор активных астросущностей
        astroRegistry.init();
        // запускаем астро-движок
        CalcMethod calcMethod = currentSettings.getCalcMethod();
        if (calcMethod == MOSHIER)
            dataProvider.init();
        if (calcMethod == SWISSEPH)
            dataProvider.init("./ephe");
    }


    public AstroRegistry getAstroRegistry() {
        return astroRegistry;
    }

    public AstroDataProvider getDataProvider() {
        return dataProvider;
    }

    public Settings getCurrentSettings() {
        return currentSettings;
    }


}
