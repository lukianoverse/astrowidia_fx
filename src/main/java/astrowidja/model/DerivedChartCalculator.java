package astrowidja.model;

import astrowidja.model.charts.AstroSituation;

import java.util.Map;

@FunctionalInterface
public interface DerivedChartCalculator {
    /**
     * Создаёт новую АстроСитуацию на основе полученных исходных карт и аргументов.
     * @param baseCharts Массив исходных астроситуаций, на основе которых считается новая.
     * @param args Словарь аргументов, необходимых для расчёта.
     * @return новую AstralSituation, вычисленную из полученных данных.
     */
    AstroSituation calculate(AstroSituation[] baseCharts, Map<String, Object> args);
}
