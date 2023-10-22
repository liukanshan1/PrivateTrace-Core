package Priloc.area.basic;

import Priloc.utils.Constant;
import Priloc.utils.User;
import sg.smu.securecom.keys.PaillierKey;

import java.io.Serializable;
import java.util.List;

public class Circle implements Serializable {
    private Point center;
    private double radius;
    public static final circleFilter DISTANCE_FILTER = new distantFilter();
    public static final circleFilter AREA_FILTER = new areaFilter();

    public Circle(Point center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    public Point getCenter() {
        return center;
    }

    @Override
    public String toString() {
        return "Circle{" +
                "center=" + center +
                ", radius=" + radius +
                '}';
    }

    public boolean isInside(Circle c) {
        return center.distance(c.center) <= Math.abs(radius - c.radius);
    }

    public boolean isInside(Point p) {
        return center.distance(p) <= radius;
    }

    public static boolean isInside(Point p, Circle[] circles) {
        for (Circle c : circles) {
            if (c.isInside(p)) return true;
        }
        return false;
    }

    public EncryptedCircle encrypt(){
        return new EncryptedCircle(this);
    }

    public EncryptedCircle enc(){
        return encrypt();
    }

    public static EncryptedCircle[] encrypt(Circle[] circles,PaillierKey pk){
        EncryptedCircle[] encryptedCircles = new EncryptedCircle[circles.length];
        for(int i = 0; i < circles.length; i++){
            encryptedCircles[i] = circles[i].encrypt();
        }
        return encryptedCircles;
    }

    public double area() {
        return Math.PI * radius * radius;
    }

    public boolean isIntersect(Circle c) {
        return center.distance(c.center) <= radius + c.radius;
    }

    public boolean isIntersect(List<Circle> circles) {
        for (Circle c : circles) {
            if (isIntersect(c)) {
                return true;
            }
        }
        return false;
    }

    /* AI生成代码，未验证逻辑！ */
    public double intersectArea(Circle c) {
        if (!isIntersect(c)) {
            return 0;
        }
        double d = center.distance(c.center);
        double r1 = radius;
        double r2 = c.radius;
        double a = Math.acos((r1 * r1 + d * d - r2 * r2) / (2 * r1 * d));
        double b = Math.acos((r2 * r2 + d * d - r1 * r1) / (2 * r2 * d));
        return r1 * r1 * a + r2 * r2 * b - d * r1 * Math.sin(a);
    }

    /* 新增的圆c的有用面积占圆c的面积的百分比 */
    public double utility(Circle c) {
        return 1 - intersectArea(c) / c.area();
    }

    public static abstract class circleFilter {
        public abstract boolean reject(Circle c, Circle newCircle, double strict);
        public abstract boolean reject(Circle c, Circle newCircle);
    }

    public static class distantFilter extends circleFilter {
        @Override
        public boolean reject(Circle c, Circle newCircle, double strict) {
            if (newCircle.radius > Constant.RADIUS) {
                return true;
            }
            if (Constant.REJECT_R_LESS_P5) {
                if (newCircle.radius < 0.5) {
                    return true;
                }
            }
            double distance = c.center.distance(newCircle.center);
            return distance < (c.radius + newCircle.radius) * strict + Math.abs(c.radius - newCircle.radius) * (1-strict);
        }

        @Override
        public String toString() {
            return "距离过滤器";
        }

        @Override
        public boolean reject(Circle c, Circle newCircle) {
            return reject(c, newCircle, 0.2);
        }
    }

    public static class areaFilter extends circleFilter {
        @Override
        public boolean reject(Circle c, Circle newCircle, double strict) {
            if (newCircle.radius > Constant.RADIUS) {
                return true;
            }
            if (Constant.REJECT_R_LESS_P5) {
                if (newCircle.radius < 0.5) {
                    return true;
                }
            }
            return c.utility(newCircle) < strict;
        }

        @Override
        public String toString() {
            return "面积过滤器";
        }

        @Override
        public boolean reject(Circle c, Circle newCircle) {
            return reject(c, newCircle, 0.5);
        }
    }
}
