package Priloc.geo;

import Priloc.utils.Constant;
import Priloc.utils.Pair;
import Priloc.utils.Turple;

public class Utils {

    private static final double X_PI = Math.PI * 3000.0 / 180.0;
    private static final double PI = Math.PI;
    private static final double A = 6378245.0;
    private static final double EE = 0.00669342162296594323;

    private static double transformLat(double lng, double lat) {
        double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLon(double lng, double lat) {
        double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * WGS84转GCJ02(火星坐标系)
     * @param lng WGS84坐标系的经度
     * @param lat WGS84坐标系的纬度
     */
    public static Pair<Double, Double> wgs84ToGcj02(double lng, double lat) {
        double dLat = transformLat(lng - 105.0, lat - 35.0);
        double dLng = transformLon(lng - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
        dLng = (dLng * 180.0) / (A / sqrtMagic * Math.cos(radLat) * PI);
        double mgLat = lat + dLat;
        double mgLng = lng + dLng;
        return new Pair<>(mgLng, mgLat);
    }

    /**
     * GCJ02(火星坐标系)转XYZ
     * @param lng GCJ02坐标系的经度
     * @param lat GCJ02坐标系的纬度
     * 后期可增加高程信息优化精度
     */

    public static Turple<Double, Double, Double> gcj02ToXYZ(double lng, double lat) {
        return gcj02ToXYZ(lng, lat, 0);
    }

    public static Turple<Double, Double, Double> gcj02ToXYZ(double lng, double lat, double high) {
        double n = 6378137.0 / Math.sqrt(1 - EE * Math.pow(Math.sin(lat * PI / 180.0), 2));
        double x = (n + high) * Math.cos(lat * PI / 180.0) * Math.cos(lng * PI / 180.0)+ Constant.REDUCE[0];
        double y = (n + high) * Math.cos(lat * PI / 180.0) * Math.sin(lng * PI / 180.0) + Constant.REDUCE[1];
        double z = (n * (1 - EE) + high) * Math.sin(lat * PI / 180.0) + Constant.REDUCE[2];
        return new Turple<>(x, y, z);
    }
}
