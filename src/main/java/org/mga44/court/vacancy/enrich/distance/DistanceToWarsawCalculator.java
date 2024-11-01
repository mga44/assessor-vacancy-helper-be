package org.mga44.court.vacancy.enrich.distance;

import org.mga44.court.vacancy.geo.Coordinates;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class DistanceToWarsawCalculator {

    private static final int EARTH_RADIUS = 6371;
    private static final DecimalFormat df = new java.text.DecimalFormat("# km");

    public static String calculateDistance(BigDecimal lat2, BigDecimal lon2) {
        Coordinates warsawCoordinates = new Coordinates(
                new BigDecimal("52.2337172"),
                new BigDecimal("21.071432235636493")
        );
        double lat1Rad = Math.toRadians(warsawCoordinates.lat().doubleValue());
        double lon1Rad = Math.toRadians(warsawCoordinates.lon().doubleValue());
        double lat2Rad = Math.toRadians(lat2.doubleValue());
        double lon2Rad = Math.toRadians(lon2.doubleValue());

        double x = (lon2Rad - lon1Rad) * Math.cos((lat1Rad + lat2Rad) / 2);
        double y = (lat2Rad - lat1Rad);
        double distance = Math.sqrt(x * x + y * y) * EARTH_RADIUS;

        return df.format(distance);
    }
}
