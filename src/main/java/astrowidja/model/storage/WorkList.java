package astrowidja.model.storage;

import astrowidja.utils.IdGenerator;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Конверт для AWD-файла библиотеки карт астролога.
 */
// Венера сохраняет порядок полей при сериализации в файл
@JsonPropertyOrder({"awdVersion", "id", "title", "comment", "modified", "works"})
public class WorkList {
    // Текущая версия программы — константа класса
    public static final int CURRENT_AWD_VERSION = 1;

    private final int awdVersion;
    private final String id;
    private String title;
    private String comment;
    private long modified;
    private final List<AstroWork> works = new ArrayList<>();

    // 1. Конструктор для создания НОВОГО списка в программе
    public WorkList(String title) {
        this.awdVersion = CURRENT_AWD_VERSION; // Зашиваем актуальную версию
        this.id = IdGenerator.generate("lst");
        setTitle(title); // Используем валидацию сеттера
        touch();
    }

    // 2. Конструктор для Цереры (Jackson): принимает ТОЛЬКО final-поля!
    @JsonCreator
    public WorkList(
            @JsonProperty("awdVersion") int awdVersion,
            @JsonProperty("id") String id) {
        this.awdVersion = awdVersion;
        this.id = id;
    }

    // ставится текущее время
    public void touch() {
        this.modified = System.currentTimeMillis();
    }


    public int getAwdVersion() {
        return awdVersion;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getModified() {
        return modified;
    }

    public void setModified(long modified) {
        this.modified = modified;
    }

    public List<AstroWork> getWorks() {
        return works;
    }


}

