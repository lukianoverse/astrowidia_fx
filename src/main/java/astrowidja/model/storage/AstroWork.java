package astrowidja.model.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AstroWork {
    private final String id;
    private String title;
    //    private CaseType type;
    // конкретные снимки неба
    private final List<AstroSituation> situations = new ArrayList<>();

    // метаинформация
    private String comment;
    private final List<String> tags = new ArrayList<>();
    private final List<String> linkedWorksIDs = new ArrayList<>();


    public AstroWork() {
        this.id = UUID.randomUUID().toString();
    }

    public AstroWork(String title) {
        this();
        this.title = title;
    }

    public void addSituation(AstroSituation situation) {
        situations.add(situation);
    }

    public boolean isSingle() {
        return situations.size() == 1;
    }

    public boolean isMultiple() {
        return situations.size() > 1;
    }

    public void removeSituation(AstroSituation situation) {
        situations.remove(situation);
    }

    public String getId() {
        return id;
    }
    public void addTag(String tag) {
        tags.add(tag);
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }

    public void addLinkedWork(String workId) {
        linkedWorksIDs.add(workId);
    }

    public void removeLinkedWork(String workId) {
        linkedWorksIDs.remove(workId);
    }

    public String getTitle() {
        return title;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<AstroSituation> getSituations() {
        return situations;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getLinkedWorksIDs() {
        return linkedWorksIDs;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String info() {
        StringBuilder desc = new StringBuilder();
        desc.append(title).append("\n");
        for (AstroSituation situation : situations)
            desc.append(situation.info()).append("\n");
        return desc.toString();
    }
}
