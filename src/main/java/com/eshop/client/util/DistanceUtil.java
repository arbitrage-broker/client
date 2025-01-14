package com.eshop.client.util;

import com.eshop.client.enums.DistanceType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DistanceUtil {

    /**
     * Calculate the length of the distance between two points by selecting a unit of measurement
     *
     * @param lat1 source latitude
     * @param lon1 source longitude
     * @param lat2 destination latitude
     * @param lon2 destination longitude
     * @param unit unit of measurement
     * @return distance with two decimal digit
     */
    public static double distance(double lat1, double lon1, double lat2, double lon2, DistanceType unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);

        dist = switch (unit) {
            case Miles -> dist * 60 * 1.1515;
            case Kilometers -> dist * 1.609344;
            case Nautical_Miles -> dist * 0.8684;
        };
        return BigDecimal.valueOf(dist).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /*
     * This function converts decimal degrees to radians
     * */
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*
     * This function converts radians to decimal degrees
     * */
    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
