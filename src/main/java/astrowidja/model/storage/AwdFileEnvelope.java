package astrowidja.model.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;

// Венера требует порядка: заставляем Jackson писать поля в последовательности,
// чтобы заголовок версии формата был в первой строчке
@JsonPropertyOrder({ "awdVersion", "listName", "listComment", "modified", "works" })
public class AwdFileEnvelope {
    private final int awdVersion = 1;
    private String listName;       // Название списка (отображается в JavaFX)
    private String listComment;    // Описание списка
    private long modified;         // Метка времени последнего изменения
    private final List<AstroWork> works = new ArrayList<>();

    // Конструктор по умолчанию для Цереры (Jackson)
    public AwdFileEnvelope() {}

    public AwdFileEnvelope(String listName, String listComment) {
        this.listName = listName;
        this.listComment = listComment;
        setUpdated();
    }

    // Автоматическое обновление метки времени
    public void setUpdated() {
        this.modified = System.currentTimeMillis();
    }

    public int getAwdVersion() {
        return awdVersion;
    }
    public String getListName() {
        return listName;
    }
    public String getListComment() {
        return listComment;
    }
    public long getModified() {
        return modified;
    }
    public List<AstroWork> getWorks() {
        return works;
    }
    public void setListName(String listName) {
        this.listName = listName;
    }
    public void setListComment(String listComment) {
        this.listComment = listComment;
    }
    public void setModified(long modified) {
        this.modified = modified;
    }
}

