package astrowidja.core;

import astrowidja.model.Chart;
import astrowidja.provider.AstroDataProvider;
import astrowidja.provider.CalcMethod;
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
import java.util.Properties;

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
    private Chart activeChart = null;

    private AppContext() {
        initSettingsPath();
        loadSettings();
    }

    private void initSettingsPath() {
        // строго локально в папке запуска программы
        Path rootDir = Paths.get(".");

        // путь к файлу настроек: ./settings.ini
        this.settingsFilePath = rootDir.resolve("settings.ini");

        // путь к репозиторию карт: ./awdata
        Path awdataFolder = rootDir.resolve("awdata");

        // Попутно создадим папку для списков AWD, если её ещё нет,
        // чтобы AWDFileChartRepository не ругался при старте
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
                currentSettings.setCalcMethod(CalcMethod.valueOf(props.getProperty("calcMethod")));
            if (props.containsKey("nodeType"))
                currentSettings.setNodeType(NodeType.valueOf(props.getProperty("nodeType")));
            if (props.containsKey("lilithType"))
                currentSettings.setLilithType(LilithType.valueOf(props.getProperty("lilithType")));
            if (props.containsKey("activeAstras")) {
                String[] names = props.getProperty("activeAstras").split(",");
                currentSettings.getAstrasToCalculate().setAll(names);
            } else {
                // Выбранные по умолчанию астры
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

    public void setActiveChart(Chart activeChart) {
        this.activeChart = activeChart;
    }

    public Chart getActiveChart() {
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
            postConstruct();
        }
        return instance;
    }

    private static void postConstruct() {
        instance.astroRegistry.init();
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
