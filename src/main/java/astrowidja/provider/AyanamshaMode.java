package astrowidja.provider;

public enum AyanamshaMode {
    TROPICAL(-1), // Тропический зодиак (SwissEph не требует включения сидерического режима)
    LAHIRI(0),    // SE_SIDM_LAHIRI
    RAMAN(1),     // SE_SIDM_RAMAN
    FAGAN_BRADLEY(2), // SE_SIDM_FAGAN_BRADLEY
    KRISHNAMURTI(5),  // SE_SIDM_KRISHNAMURTI
    USER_DEFINED(21); // SE_SIDM_USER (Пользовательский сдвиг)

    private final int swissEphConstant;

    AyanamshaMode(int swissEphConstant) {
        this.swissEphConstant = swissEphConstant;
    }

    public int getSwissEphConstant() {
        return swissEphConstant;
    }
}