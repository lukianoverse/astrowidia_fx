package astrowidja.model.storage;

public enum DuplicatePolicy {
    FAIL_IF_EXISTS, // По умолчанию: не трогать, если имя занято
    OVERWRITE,      // Перезаписать существующую
    RENAME          // Добавить с авто-суффиксом (например, "Карта (2)")
}