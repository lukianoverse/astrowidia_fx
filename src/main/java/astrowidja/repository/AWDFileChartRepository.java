package astrowidja.repository;

import astrowidja.model.storage.AstroWork;
import astrowidja.model.storage.WorkList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class AWDFileChartRepository implements ChartRepository {

    private final ObjectMapper mapper;
    private final Path storageDir = Path.of("awdata");

    public AWDFileChartRepository() {
        this.mapper = new ObjectMapper();
        // Включаем поддержку LocalDateTime
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT); // Красивый JSON

        // Автоматически создаем папку /awdata при старте репозитория
        try {
            Files.createDirectories(storageDir);
        } catch (IOException e) {
            System.err.println("Не удалось создать сатурнианскую обитель /awdata: " + e.getMessage());
        }
    }


//    @Override
//    public void saveWork(AstroWork work) {
//
//    }
//
//    @Override
//    public AstroWork getWorkById(String id) {
//        return null;
//    }
//
//    @Override
//    public List<AstroWork> getAllWorksFromList(String listName) {
//        return List.of();
//    }
//
//    @Override
//    public void deleteWork(String id) {
//
//    }

    @Override
    public List<WorkList> loadAllLists() throws IOException {
        return List.of();
    }

    @Override
    public void saveListMetadata(WorkList list) throws IOException {
        // Если юзер создал новый пустой список другой кнопочкой — сбрасываем его так же!
        flushListToFile(list);
    }

    @Override
    public void deleteList(WorkList list) throws IOException {

    }

    @Override
    public void saveWork(AstroWork work, WorkList targetList) throws IOException {
        // Для плоского файла сохранение работы — это сброс целевого списка целиком
        flushListToFile(targetList);
    }

    @Override
    public void deleteWork(AstroWork work, WorkList sourceList) throws IOException {

    }

    @Override
    public void moveWork(AstroWork work, WorkList sourceList, WorkList targetList) throws IOException {

    }

    /**
     * Атомарная запись состояния списка в файл на диске.
     */
    private void flushListToFile(WorkList list) throws IOException {
        String safeTitle = list.getTitle().replaceAll("[^a-zA-Zа-яА-Я0-9_\\-]", "_");
        String fileName = "%s__%s.awd".formatted(safeTitle, list.getId());
        Path targetFile = storageDir.resolve(fileName);

        // Церера превращает объектный граф в JSON и кладет в ПЗУ
        mapper.writeValue(targetFile.toFile(), list);
    }

}
