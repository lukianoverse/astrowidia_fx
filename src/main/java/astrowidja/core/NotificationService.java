package astrowidja.core;

import astrowidja.fx.BottomToastBanner;
import astrowidja.model.storage.DuplicatePolicy;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

import java.util.Optional;

public class NotificationService {

    private static BottomToastBanner banner;

    public static void registerBanner(BottomToastBanner toastBanner) {
        banner = toastBanner;
    }

    // 1. Тихие уведомления и предупреждения (идут в нижний тост)
    public static void showInfo(String text) {
        if (banner != null && text != null && !text.isBlank())
            javafx.application.Platform.runLater(() -> banner.showToast(text));
    }

    // 2. Критические ошибки (модальный Alert по центру)
    public static void showError(String title, String text) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(title);
            alert.setContentText(text);
            alert.showAndWait();
        });
    }


    /**
     * Показывает модальный диалог выбора действия при дубликате имени карты.
     *
     * @param chartName Имя конфликтующей карты
     * @return Optional с выбранной стратегией, или Optional.empty(), если пользователь нажал «Отмена» / закрыл окно.
     */
    public static Optional<DuplicatePolicy> askDuplicatePolicy(String chartName) {
        Alert alert = createDuplicationAlert(chartName);

        // Настраиваем кнопки (правильные ButtonData для корректного порядка на разных ОС)
        ButtonType btnOverwrite = new ButtonType("Заменить", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnRename = new ButtonType("Сохранить как копию", ButtonBar.ButtonData.OTHER);
        ButtonType btnCancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnOverwrite, btnRename, btnCancel);

        // showAndWait() блокирует поток до ответа пользователя
        return alert.showAndWait().flatMap(clickedButton -> {
            if (clickedButton == btnOverwrite) {
                return Optional.of(DuplicatePolicy.OVERWRITE);
            }
            if (clickedButton == btnRename) {
                return Optional.of(DuplicatePolicy.RENAME);
            }
            return Optional.empty(); // Нажата «Отмена» или крестик окна
        });
    }

    private static Alert createDuplicationAlert(String chartName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Конфликт имён");
        alert.setHeaderText("Карта «%s» уже есть в этом списке".formatted(chartName));
        alert.setContentText("Как поступить с новой картой?");

        // Привязываем диалог к главному окну (если баннер зарегистрирован в сцене),
        // чтобы окно всплывало ровно по центру приложения, а не в углу монитора
        if (banner != null && banner.getScene() != null) {
            Window owner = banner.getScene().getWindow();
            alert.initOwner(owner);
        }
        return alert;
    }

}
