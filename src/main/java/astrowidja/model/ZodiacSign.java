package astrowidja.model;


import static astrowidja.utils.CelestialMechanics.normalizeCoordinate;

/**
 * Представление зодиакального знака, содержит символ.
 */
public enum ZodiacSign {
    ARIES('♈'),
    TAURUS('♉'),
    GEMINI('♊'),
    CANCER('♋'),
    LEO('♌'),
    VIRGO('♍'),
    LIBRA('♎'),
    SCORPIO('♏'),
    SAGITTARIUS('♐'),
    CAPRICORN('♑'),
    AQUARIUS('♒'),
    PISCES('♓');

    public final char symbol;

    ZodiacSign(char symbol) {
        this.symbol = symbol;
    }

    public static char zodiumIcon(double position) {
        return getZodiumOf(position).symbol;
    }

    public static ZodiacSign getZodiumOf(double position) {
        return values()[(int) normalizeCoordinate(position) / 30];
    }

}
