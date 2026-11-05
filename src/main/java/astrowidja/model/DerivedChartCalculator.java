package astrowidja.model;

import astrowidja.model.charts.AstralSituation;

import java.util.Map;

@FunctionalInterface
public interface DerivedChartCalculator {
    /**
     * @param baseCharts Массив исходных астроситуаций, на основе которых считается новая.
     * @param args Словарь аргументов, необходимых для расчёта.
     * @return новую AstralSituation, вычисленную из полученных данных.
     */
    AstralSituation calculate(AstralSituation[] baseCharts, Map<String, Object> args);
}
