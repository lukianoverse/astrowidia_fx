package astrowidja.service;

import astrowidja.model.storage.AstroWork;
import astrowidja.model.storage.Album;
import astrowidja.repository.ChartRepository;

import java.io.IOException;

public class LibraryService {
    private static final String DEFAULT_LIST_TITLE = "Карты";
    
    private final UserLibrary library;
    private final ChartRepository repository;

    public LibraryService(UserLibrary library, ChartRepository repository) {
        this.library = library;
        this.repository = repository;
    }

    /**
     * Сценарий 1 (Онтогенез): Сохранение работы без явного выбора списка.
     */
    public void saveWorkToDefault(AstroWork work) throws IOException {
        Album defaultList = getOrCreateDefaultList();
        saveWork(work, defaultList);
    }

    /**
     * Сценарий 2: Сохранение работы в конкретный, выбранный пользователем список.
     */
    public void saveWork(AstroWork work, Album targetList) throws IOException {
        if (!targetList.getWorks().contains(work)) {
            targetList.getWorks().add(work);
        }
        targetList.touch();
        library.indexWork(work); // Регистрируем в сквозных картах ОЗУ
        
        // Делегируем сброс в ПЗУ нашему репозиторию
        repository.saveWork(work, targetList);
    }

    /**
     * Ищет список "Карты", а если его нет в природе — творит его.
     */
    private Album getOrCreateDefaultList() throws IOException {
        // Сначала ищем среди уже существующих в памяти списков
        return library.getAllAlbums().stream()
                .filter(list -> list.getTitle().equalsIgnoreCase(DEFAULT_LIST_TITLE))
                .findFirst()
                .orElseGet(() -> {
                    // Если списка нет (самый первый запуск) — создаем!
                    Album newList = new Album(DEFAULT_LIST_TITLE);
                    newList.setComment("Основной список карт по умолчанию");
                    library.registerAlbum(newList);
                    return newList;
                });
    }
}
