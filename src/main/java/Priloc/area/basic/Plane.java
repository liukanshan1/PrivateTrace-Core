package Priloc.area.basic;

public class Plane {
    private double a, b, c, d;

    public Plane(Point p1, Point p2, Point p3) {
        a = (p2.getY() - p1.getY()) * (p3.getZ() - p1.getZ()) - (p3.getY() - p1.getY()) * (p2.getZ() - p1.getZ());
        b = (p2.getZ() - p1.getZ()) * (p3.getX() - p1.getX()) - (p3.getZ() - p1.getZ()) * (p2.getX() - p1.getX());
        c = (p2.getX() - p1.getX()) * (p3.getY() - p1.getY()) - (p3.getX() - p1.getX()) * (p2.getY() - p1.getY());
        d = -a * p1.getX() - b * p1.getY() - c * p1.getZ();
    }

    @Override
    public String toString() {
        return "Plane{" +
                "a=" + a +
                ", b=" + b +
                ", c=" + c +
                ", d=" + d +
                '}';
    }

    public boolean isInside(Point p) {
        return a * p.getX() + b * p.getY() + c * p.getZ() + d == 0;
    }

    public Point getPointFromXY(double x, double y) {
        try {
            return new Point(x, y, (-a * x - b * y - d) / c);
        } catch (ArithmeticException e){
            System.out.println("Plane is parallel to XY plane");
            return null;
        }
    }

    public Point getPointFromYZ(double y, double z){
        try {
            return new Point((-b*y - c*z - d)/a, y, z);
        } catch (ArithmeticException e){
            System.out.println("Plane is parallel to YZ plane");
            return null;
        }
    }

    public Point getPoinFromXZ(double x, double z){
        try {
            return new Point(x, (-a*x - c*z - d)/b, z);
        } catch (ArithmeticException e){
            System.out.println("Plane is parallel to XZ plane");
            return null;
        }
    }
}
