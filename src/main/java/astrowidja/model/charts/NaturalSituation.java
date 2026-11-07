package astrowidja.model.charts;

import astrowidja.model.Astra;
import astrowidja.model.TimeSpacePoint;
import astrowidja.provider.ModusCalculationis;

public class NaturalSituation extends AstroSituation {
    TimeSpacePoint timeSpacePoint;
    ModusCalculationis modusCalculationis;

    @Override
    public SituationType getActualType() {
        return SituationType.NATURAL;
    }

    @Override
    public String info() {
        StringBuilder sb = new StringBuilder();

        for (Astra astra : astras)
            sb.append(astra.getFormattedPosition()).append("\n");

        return sb.toString();
    }

}
