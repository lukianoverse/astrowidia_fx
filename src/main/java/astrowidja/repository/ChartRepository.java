package astrowidja.repository;

import astrowidja.model.storage.AstroWork;

import java.util.List;

public interface ChartRepository {
    public void saveWork(AstroWork work);
    public AstroWork getWorkById(String id);
    public List<AstroWork> getAllWorksFromList(String listName);
    public void deleteWork(String id);

}
