package astrowidja.repository;

import astrowidja.model.storage.AstroWork;
import astrowidja.model.storage.Album;

import java.io.IOException;
import java.util.List;

public interface ChartRepository {

    // --- Стартовая загрузка ---
    List<Album> loadAllLists() throws IOException;

    // --- Операции с метаданными самого Списка (имя, комментарий) ---
    void saveListMetadata(Album list) throws IOException;
    void deleteList(Album list) throws IOException;

    // --- Атомарные операции с Работами (AstroWork) ---
    void saveWork(AstroWork work, Album targetList) throws IOException;
    void deleteWork(AstroWork work, Album sourceList) throws IOException;
    void moveWork(AstroWork work, Album sourceList, Album targetList) throws IOException;

}
