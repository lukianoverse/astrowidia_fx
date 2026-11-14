package astrowidja.exception;

public class DuplicateChartException extends RuntimeException {
    private final String chartName;

    public DuplicateChartException(String chartName) {
        super("Карта с именем '" + chartName + "' уже существует");
        this.chartName = chartName;
    }

    public String getChartName() {
        return chartName;
    }
}