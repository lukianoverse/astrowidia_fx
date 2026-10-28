package astrowidja.core;

import astrowidja.model.Chart;
import astrowidja.provider.AstroDataProvider;
import astrowidja.provider.CalcMethod;
import astrowidja.provider.LilithType;
import astrowidja.provider.NodeType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    private final Settings settings = new Settings();
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
        try (InputStream is = Files.newInputStream(settingsFilePath)) {
            Properties props = new Properties();
            props.load(is);

            // Читаем значения из файла, если строки кривые — сработает дефолт
            if (props.containsKey("calcMethod"))
                settings.setCalcMethod(CalcMethod.valueOf(props.getProperty("calcMethod")));
            if (props.containsKey("nodeType"))
                settings.setNodeType(NodeType.valueOf(props.getProperty("nodeType")));
            if (props.containsKey("lilithType"))
                settings.setLilithType(LilithType.valueOf(props.getProperty("lilithType")));
        } catch (Exception e) {
            // TODO: заменить на уведомление
            System.err.println("Ошибка чтения настроек, используются дефолты: " + e.getMessage());
        }
    }

    public void saveSettings() {
        try {
            Files.createDirectories(settingsFilePath.getParent());
            try (OutputStream os = Files.newOutputStream(settingsFilePath)) {
                Properties props = new Properties();
                props.setProperty("calcMethod", settings.getCalcMethod().name());
                props.setProperty("nodeType", settings.getNodeType().name());
                props.setProperty("lilithType", settings.getLilithType().name());

                props.store(os, "AstroWidja Settings");
                System.out.println("Настройки успешно сохранены в " + settingsFilePath);
                //TODO: это всё логирование куда-нибудь прикрутить,
                // там же и история расчётов и т.п.
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
     * Синглетон для удержания глобальных компонентов.
     * @return сам себя одного и того же.
     */
//    public static AppContext getInstance() {
//        return instance;
//    }
    public static synchronized AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
            instance.astroRegistry.init();
        }
        return instance;
    }


    public AstroRegistry getAstroRegistry() {
        return astroRegistry;
    }

    public AstroDataProvider getDataProvider() {
        return dataProvider;
    }

    public Settings getSettings() {
        return settings;
    }


}
