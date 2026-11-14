package astrowidja.fx;

import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class BottomToastBanner extends HBox {

    private final Label textLabel = new Label();

    public BottomToastBanner() {
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(10, 16, 10, 16));
        this.setSpacing(12);
        this.setMaxHeight(USE_PREF_SIZE);
        this.setMaxWidth(650); // Чтобы не растягивалась на весь монитор
        
        // Стиль аккуратной плавающей карточки снизу
        this.setStyle(
            "-fx-background-color: #2b2d30;" +
            "-fx-text-fill: #f0f0f0;" +
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.35), 10, 0, 0, 4);"
        );

        textLabel.setStyle("-fx-text-fill: #f0f0f0; -fx-wrap-text: true;");
        HBox.setHgrow(textLabel, Priority.ALWAYS);

        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #aaaaaa; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> hideToast());

        this.getChildren().addAll(textLabel, closeBtn);
        
        // Прижимаем к низу StackPane с отступом
        StackPane.setAlignment(this, Pos.BOTTOM_CENTER);
        StackPane.setMargin(this, new Insets(0, 20, 20, 20));
        
        this.setVisible(false);
    }

    public void showToast(String warnings) {
        if (warnings == null || warnings.isBlank()) {
            this.setVisible(false);
            return;
        }
        textLabel.setText(warnings);
        this.setVisible(true);
        
        // Выезжаем снизу вверх
        this.setTranslateY(60);
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(250), this);
        slideUp.setToY(0);
        slideUp.play();
    }

    public void hideToast() {
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(200), this);
        slideDown.setToY(60);
        slideDown.setOnFinished(e -> this.setVisible(false));
        slideDown.play();
    }
}
