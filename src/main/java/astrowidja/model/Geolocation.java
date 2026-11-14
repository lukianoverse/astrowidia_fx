package astrowidja.model;


public record Geolocation(
    double latitude,
    double longitude,
    double altitude,
    String ianaTimezone,
    String cityName
    ) {
//    private int rawOffsetMinutes;

    public String toMeaningfullString() {
        var sb = new StringBuilder();

        if (!Double.isNaN(latitude))
            sb.append(formatAngle(latitude, 90, 'С', 'Ю')).append("Ш");

        if (!Double.isNaN(longitude))
            sb.append(formatAngle(longitude, 180, 'В', 'З')).append("Д");

        if (!Double.isNaN(altitude))
            sb.append(" ").append(String.format("%.0fм", altitude));

        if (cityName != null && !cityName.isBlank())
            sb.append(" ").append(cityName);

        if (ianaTimezone != null && !ianaTimezone.isBlank())
            sb.append(" (").append(ianaTimezone).append(")");

        if (sb.isEmpty())
            sb.append("Пустое место");

        return sb.toString().trim();
    }

    private static String formatAngle(double degrees, double maxDeg, char pos, char neg) {
        boolean negative = degrees < 0;
        degrees = Math.abs(degrees);

        int d = (int) degrees;
        int m = (int) ((degrees - d) * 60);
        int s = (int) (((degrees - d) * 60 - m) * 60);

        String dir = (degrees == 0 || d == 0) ?
                "" : (negative ? String.valueOf(neg) : String.valueOf(pos));
        return String.format("%d°%02d′%02d″ %s", d, m, s, dir);
    }

}

