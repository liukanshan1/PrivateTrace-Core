package Priloc.area.basic;

import Priloc.geo.Location;
import Priloc.utils.Turple;

import java.io.Serializable;

public class Point implements Serializable {
    private double x, y, z;

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point(Location loc) {
        Turple<Double, Double, Double> xyz = loc.toXYZ();
        this.x = xyz.first;
        this.y = xyz.second;
        this.z = xyz.third;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double distance(Point p) {
        return Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2) + Math.pow(p.z - z, 2));
    }

    public EncryptedPoint encrypt() {
        return new EncryptedPoint(this);
    }

    public static double distance(Point p1, Point p2) {
        return p1.distance(p2);
    }
}
