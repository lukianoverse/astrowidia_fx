package astrowidja.repository;

import astrowidja.model.storage.AstroWork;
import astrowidja.model.storage.WorkList;

import java.io.IOException;
import java.util.List;

public interface ChartRepository {

    // --- Стартовая загрузка ---
    List<WorkList> loadAllLists() throws IOException;

    // --- Операции с метаданными самого Списка (имя, комментарий) ---
    void saveListMetadata(WorkList list) throws IOException;
    void deleteList(WorkList list) throws IOException;

    // --- Атомарные операции с Работами (AstroWork) ---
    void saveWork(AstroWork work, WorkList targetList) throws IOException;
    void deleteWork(AstroWork work, WorkList sourceList) throws IOException;
    void moveWork(AstroWork work, WorkList sourceList, WorkList targetList) throws IOException;

}
