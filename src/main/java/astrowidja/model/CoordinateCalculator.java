package astrowidja.model;

import astrowidja.provider.ModusCalculationis;
import astrowidja.provider.SweCalcResult;

@FunctionalInterface
public interface CoordinateCalculator {
    /**
     * Считает эфемериды для конкретной сущности.
     */
    SweCalcResult calculate(TimeSpacePoint tsp, ModusCalculationis modus, AstroEngine engine);
}
