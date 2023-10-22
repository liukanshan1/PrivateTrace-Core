package Priloc.area.shape;

import Priloc.area.basic.Circle;
import Priloc.geo.Location;

public class Polygon implements Shape {
    private Triangle[] triangles;

    public Polygon(Location... locations) {
        triangles = new Triangle[locations.length - 2];
        for(int i = 0; i < locations.length - 2; i++) {
            triangles[i] = new Triangle(locations[0], locations[i + 1], locations[i + 2]);
        }
    }

    @Override
    public void init() {
        for(Triangle triangle : triangles) {
            triangle.init();
        }
    }

    @Override
    public Circle[] fitByCircle(int num, Circle.circleFilter filter, double strict) {
        Circle[] circles = new Circle[num * triangles.length];
        for (int i = 0; i < triangles.length; i++) {
            Circle[] cs = triangles[i].fitByCircle(num, filter, strict);
            System.arraycopy(cs, 0, circles, i * num, num);
        }
        return circles;
    }

    @Override
    public Circle[] fitByCircle(int num, Circle.circleFilter filter) {
        Circle[] circles = new Circle[num * triangles.length];
        for (int i = 0; i < triangles.length; i++) {
            Circle[] cs = triangles[i].fitByCircle(num, filter);
            System.arraycopy(cs, 0, circles, i * num, num);
        }
        return circles;
    }

    @Override
    public double checkCoverage(int num, Circle[] circles) {
        double res = 0;
        int len = circles.length / triangles.length;
        Circle[] cs = new Circle[len];
        for (int i = 0; i < triangles.length; i++) {
            System.arraycopy(circles, i * len, cs, 0, len);
            res += triangles[i].checkCoverage(num, cs);
        }
        return res / triangles.length;
    }
}
