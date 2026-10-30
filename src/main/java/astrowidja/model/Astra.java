package astrowidja.model;

import astrowidja.provider.SweCalcResult;
import astrowidja.utils.CelestialMechanics;

public class Astra implements ZodiacPoint {

    private String name;
    private Chart heaven;
    private SweCalcResult ephemeris;    // или всё-таки ограничиться лишь долготой и ретроградностью?
    private AstroEntity entity;

    @Override
    public double getZodiacPosition() {
        return ephemeris.longitude();
    }

    public boolean isRetrograde() {
        return ephemeris.isRetrograde();
    }

    public String getName() {
        return name;
    }

    public Chart getHeaven() {
        return heaven;
    }

    public String getSymbol() {
        return entity.getSymbol();
    }

    public Astra(AstroEntity entity, Chart heaven, SweCalcResult ephemeris) {
        name = entity.getName();
        this.heaven = heaven;
        this.ephemeris = ephemeris;
        this.entity = entity;
    }

    public static boolean ofSameHeaven(Astra a, Astra b) {
        return a.getHeaven() == b.getHeaven();
    }

    @Override
    public String toString() {
        return "%s (%s) %s"
                .formatted(name,
                        heaven.getName(),
                        getZodiacDegree());
    }

    /**
     * @return знак зодиака, в котором находится астра.
     */
    public ZodiacSign getZodiacSign() {
        return ZodiacSign.getZodiumOf(getZodiacPosition());
    }

    /**
     * Выдаёт инфу об астре в виде "название (градус_зодиака)"
     *
     * @return строку с названием и градусом астры.
     */
    public String getNameWithZodiacDegree() {
        return "%s (%s)".formatted(name, getZodiacDegree());
    }


    /**
     * Выдаёт зодиакальное положение астры, как предоставляется
     * функцией {@link CelestialMechanics#zodiacDegree(double)}
     *
     * @return строковое представление зодиакального градуса, в котором расположена астра.
     */
    public String getZodiacDegree() {
        return CelestialMechanics.zodiacDegree(getZodiacPosition());
    }


    /**
     * Выдаёт строку, представляющую символ астры и её зодиакально положение.
     *
     * @return строку вида "символ (положение)",
     * где 'положение' представлено как градус знака.
     */
    public String getSymbolWithDegree() {
        return "%s (%s)".formatted(getSymbol(), getZodiacDegree());
    }

    /**
     * Выдаёт строку, представляющую символ астры и обладателя неба с нею.
     *
     * @return строку вида "символ (обладатель)", где 'обладатель' -
     * название карты, в которой расположена астра.
     */
    public String getSymbolWithOwner() {
        return "%s (%s)".formatted(getSymbol(), heaven.getName());
    }

    public String getFormattedPosition() {
        int[] coor = CelestialMechanics.degreesToSignAndCoors(getZodiacPosition());
        return "%s%s\t\t%c %2d°%2d'%2d\"".formatted(
                getSymbol(),
                isRetrograde() ? "R " : "  ",
                ZodiacSign.values()[coor[0] - 1].symbol,
                coor[1],
                coor[2],
                coor[3]);
    }


}
