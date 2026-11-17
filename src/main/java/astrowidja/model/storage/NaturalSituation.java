package astrowidja.model.storage;

import astrowidja.model.Astra;
import astrowidja.model.TimeSpacePoint;
import astrowidja.provider.ModusCalculationis;

public class NaturalSituation extends AstroSituation {
    TimeSpacePoint timeSpacePoint;
    ModusCalculationis modusCalculationis;

    public NaturalSituation(TimeSpacePoint timeSpacePoint, ModusCalculationis modusCalculationis) {
        this.timeSpacePoint = timeSpacePoint;
        this.modusCalculationis = modusCalculationis;
    }

    public NaturalSituation() {
    }

    @Override
    public SituationType getActualType() {
        return SituationType.NATURAL;
    }

    @Override
    public String info() {
        StringBuilder sb = new StringBuilder();
        sb.append(getDescription()).append("\n");
        for (Astra astra : astras)
            sb.append(astra.getFormattedPosition()).append("\n");

        return sb.toString();
    }

    @Override
    public String getDescription() {
        return "Небо на " + timeSpacePoint.getLocalDateTime()
                + " в " + timeSpacePoint.getGeolocation().toMeaningfullString();
    }

    public TimeSpacePoint getTimeSpacePoint() {
        return timeSpacePoint;
    }

    public void setTimeSpacePoint(TimeSpacePoint tsp) {
        this.timeSpacePoint = tsp;
    }

    public ModusCalculationis getModusCalculationis() {
        return modusCalculationis;
    }

    public void setModusCalculationis(ModusCalculationis modus) {
        this.modusCalculationis = modus;
    }


}
