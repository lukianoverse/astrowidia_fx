package astrowidja.repository;

import astrowidja.model.storage.AstroWork;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class AWDFileChartRepository implements ChartRepository {

    private final ObjectMapper mapper;
    private final Path storagePath = Path.of("awdata");

    public AWDFileChartRepository() {
        this.mapper = new ObjectMapper();
        // Включаем поддержку LocalDateTime
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT); // Красивый JSON

        // Автоматически создаем папку /awdata при старте репозитория
        try {
            Files.createDirectories(storagePath);
        } catch (IOException e) {
            System.err.println("Не удалось создать сатурнианскую обитель /awdata: " + e.getMessage());
        }
    }


    @Override
    public void saveWork(AstroWork work) {

    }

    @Override
    public AstroWork getWorkById(String id) {
        return null;
    }

    @Override
    public List<AstroWork> getAllWorksFromList(String listName) {
        return List.of();
    }

    @Override
    public void deleteWork(String id) {

    }
}
