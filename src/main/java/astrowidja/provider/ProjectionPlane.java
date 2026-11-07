package astrowidja.provider;

public enum ProjectionPlane {
    ECLIPTIC,   // Стандартный круг (долгота/широта)
    EQUATOR,    // Экваториальная система (прямое восхождение/склонение)
    HORIZON,    // Горизонтальная система (азимут/высота)
    GALAXY      // Галактическая плоскость (для Pro+ версии 😉)
}