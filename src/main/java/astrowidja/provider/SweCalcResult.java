package astrowidja.provider;

public record SweCalcResult(
        int status, // битовая маска, по которой фактически выполнился расчёт (-1 для неуспеха)
        double longitude,   // долгота (здесь и далее: в выбранной системе координат)
        double latitude,    // широта
        double distance,    // расстояние до тела
        double longitudinalVelocity,    // скорость по долготе
        double latitudinalVelocity,    // скорость по широте
        double radialVelocity,      // лучевая скорость
        String serr     // сообщение об ошибке или предупреждение
) {
    public SweCalcResult(int status, double[] xp, String serr) {
        this(status, xp[0], xp[1], xp[2], xp[3], xp[4], xp[5], serr);
    }

    public boolean isRetrograde() {
        return longitudinalVelocity < 0;
    }

    public boolean isSuccess() {
        return status > -1;
    }

    public String getMessage() {
        return serr;
    }

}
