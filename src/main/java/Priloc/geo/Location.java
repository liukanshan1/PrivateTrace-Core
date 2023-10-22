package Priloc.geo;

import Priloc.area.basic.EncryptedPoint;
import Priloc.utils.Turple;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalCoordinates;

import java.io.Serializable;

public class Location implements Serializable {
    private double latitude;
    private double longitude;
    private double altitude = 0.0;

    public Location(double latitude, double longitude, double altitude) {
        this(latitude, longitude);
        this.altitude = altitude;
    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "PlainLocation{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                //", XYZ=" + toXYZ() +
                '}';
    }

    public Turple<Double, Double, Double> toXYZ() {
        return Utils.gcj02ToXYZ(longitude, latitude, altitude);
    }

    public EncryptedPoint encrypt() {
        Turple<Double, Double, Double> xyz = toXYZ();
        return new EncryptedPoint(xyz.first, xyz.second, xyz.third);
    }

    public double squareDistance(Location other) {
//        Turple<Double, Double, Double> xyz1 = toXYZ();
//        Turple<Double, Double, Double> xyz2 = other.toXYZ();
//        return Math.pow(xyz1.first - xyz2.first, 2) + Math.pow(xyz1.second - xyz2.second, 2) + Math.pow(xyz1.third - xyz2.third, 2);
        return Math.pow(distance(other), 2);
    }

    public double distance(Location other) {
//        return Math.sqrt(squareDistance(other));
        GlobalCoordinates source = new GlobalCoordinates(latitude, longitude);
        GlobalCoordinates target = new GlobalCoordinates(other.latitude, other.longitude);
        return new GeodeticCalculator().calculateGeodeticCurve(Ellipsoid.WGS84, source, target).getEllipsoidalDistance();
    }

    public double encodeDistance(Location other) {
        Turple<Double, Double, Double> xyz1 = toXYZ();
        Turple<Double, Double, Double> xyz2 = other.toXYZ();
        double squareDist = Math.pow(xyz1.first - xyz2.first, 2) + Math.pow(xyz1.second - xyz2.second, 2) + Math.pow(xyz1.third - xyz2.third, 2);
        return Math.sqrt(squareDist);
    }
}

