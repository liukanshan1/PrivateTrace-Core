package Priloc.area.shape;

import Priloc.area.basic.Circle;
import Priloc.area.basic.Plane;
import Priloc.area.basic.Point;
import Priloc.geo.Location;
import Priloc.utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class Triangle implements Shape {

    private Point point1, point2, point3;
    private double maxX, maxY, maxZ, minX, minY, minZ;
    private Plane plane;
    private double side12, side13, side23;
    private double area = 0;
    private boolean initialized = false;

    public Triangle(Point point1, Point point2, Point point3, boolean lazyInit) {
        this.point1 = point1;
        this.point2 = point2;
        this.point3 = point3;
    }

    public Triangle(Point point1, Point point2, Point point3) {
        this.point1 = point1;
        this.point2 = point2;
        this.point3 = point3;
        init();
    }

    public Triangle(Location loc1, Location loc2, Location loc3) {
        this(new Point(loc1), new Point(loc2), new Point(loc3));
    }

    @Override
    public void init() {
        if(initialized) return;
        this.maxX = Math.max(point1.getX(), Math.max(point2.getX(), point3.getX()));
        this.maxY = Math.max(point1.getY(), Math.max(point2.getY(), point3.getY()));
        this.maxZ = Math.max(point1.getZ(), Math.max(point2.getZ(), point3.getZ()));
        this.minX = Math.min(point1.getX(), Math.min(point2.getX(), point3.getX()));
        this.minY = Math.min(point1.getY(), Math.min(point2.getY(), point3.getY()));
        this.minZ = Math.min(point1.getZ(), Math.min(point2.getZ(), point3.getZ()));
        this.plane = new Plane(point1, point2, point3);
        this.side12 = point1.distance(point2);
        this.side13 = point1.distance(point3);
        this.side23 = point2.distance(point3);
        double s = (side12 + side13 + side23) / 2;
        this.area = Math.sqrt(s * (s - side12) * (s - side13) * (s - side23));
        initialized = true;
    }

    /* 未检查是否init！ */
    public double getArea() {
        return area;
    }

    public boolean isInside(Point p, double error) {
        if (!initialized) {
            init();
        }
        if (p.getX() > maxX || p.getX() < minX || p.getY() > maxY || p.getY() < minY || p.getZ() > maxZ || p.getZ() < minZ) {
            return false;
        }
        if (plane.isInside(p)) {
            double a = new Triangle(point1, point2, p).getArea();
            double b = new Triangle(point1, point3, p).getArea();
            double c = new Triangle(point2, point3, p).getArea();
            // 允许一些误差值
            return Math.abs(a + b + c - area) < error;
        }
        return false;
    }

    public boolean isInside(Point p) {
        return isInside(p, 0.01);
    }

    private Point samplingPoint() {
        if (!initialized) {
            init();
        }
        while (true) {
            // 没有实现一定概率上关注三个角（FREED仓库里面的shapes.py）
            double x = Math.random() * (maxX - minX) + minX;
            double y = Math.random() * (maxY - minY) + minY;
            Point p = plane.getPointFromXY(x, y);
            if (isInside(p)) {
                return p;
            }
        }
    }

    private Point[] samplingPoints(int num) {
        Point[] points = new Point[num];
        for (int i = 0; i < num; i++) {
            points[i] = samplingPoint();
        }
        return points;
    }

    private Circle getInscribedCircle(Point point) {
        if (!initialized) {
            init();
        }
        Triangle t12 = new Triangle(point1, point2, point);
        Triangle t13 = new Triangle(point1, point3, point);
        Triangle t23 = new Triangle(point2, point3, point);
        double h12 = 2 * t12.getArea() / side12;
        double h13 = 2 * t13.getArea() / side13;
        double h23 = 2 * t23.getArea() / side23;
        double r = Math.min(h12, Math.min(h13, h23));
        return new Circle(point, r);
    }

    @Override
    public Circle[] fitByCircle(int num, Circle.circleFilter filter, double strict) {
        Circle[] circles = new Circle[num];
        for (int i = 0; i < num; i++) {
            Circle c;
            while (true) {
                c = getInscribedCircle(samplingPoint());
                if (filter(circles, i, c, filter, strict)) {
                    break;
                }
            }
            circles[i] = c;
        }
        return circles;
    }

    private boolean filter(Circle[] circles, int end, Circle c, Circle.circleFilter filter, double strict) {
        for (int j = 0; j < end; j++) {
            if (filter.reject(circles[j], c, strict)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Circle[] fitByCircle(int num, Circle.circleFilter filter) {
        Circle[] circles = new Circle[num];
        for (int i = 0; i < num; i++) {
            Circle c;
            while (true) {
                c = getInscribedCircle(samplingPoint());
                if (filter(circles, i, c, filter)) {
                    break;
                }
            }
            circles[i] = c;
        }
        return circles;
    }

    private boolean filter(Circle[] circles, int end, Circle c, Circle.circleFilter filter) {
        for (int j = 0; j < end; j++) {
            if (filter.reject(circles[j], c)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public double checkCoverage(int num, Circle[] circles) {
        Point[] points = samplingPoints(num);
        List<Circle> circleList = new ArrayList<>();
        for (Point p : points) {
            circleList.add(new Circle(p, Constant.RADIUS));
        }
        double count = 0.0;
        for (Circle c : circleList) {
            if (c.isIntersect(List.of(circles))) {
                count++;
            }
        }
        return count / num;
    }
}
