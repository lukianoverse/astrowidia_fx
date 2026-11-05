package astrowidja.model.charts;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StarCase {
    private final String id;
    private String title;
    //    private CaseType type;
    // конкретные снимки неба
    private List<AstralSituation> situations = new ArrayList<>();
    // метаинформация
    private String comment;
    private List<String> tags = new ArrayList<>();
    private List<String> linkedCasesIDs;


    public StarCase() {
        this.id = UUID.randomUUID().toString();
    }

    public StarCase(String title) {
        this();
        this.title = title;
    }


    public boolean isSingle() {
        return situations.size() == 1;
    }

    public boolean isMultiple() {
        return situations.size() > 1;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<AstralSituation> getSituations() {
        return situations;
    }


    public String getComment() {
        return comment;
    }

    public List<String> getLinkedCasesIDs() {
        return linkedCasesIDs;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
