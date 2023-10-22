package Priloc.utils;

import Priloc.area.basic.Circle;

import java.util.Arrays;

public class Constant {
    public static final int FIXED_POINT = 8;
    public static final int THREAD = 12;
    public static final int KEY_LEN = 512;
    public static final int[] REDUCE = new int[]{2160000, 1820000, -5690000};

    public static final boolean IGNORE_DATE = true;
    public static final boolean REJECT_R_LESS_P5 = true;

    // 时间段大小
    public final static int INTERVAL = 10;
    // 每个时间段的移动最大距离
    public static final double RADIUS = 200.0;
    // TODO 优化 不同时间段的活跃距离
    public static final double[] COMPARE_DISTANCE = new double[]{1200 * 1200.0};
    public static final int[] PRUNE_NUM = new int[]{0, 2};
    // 控制形状拟合效果
    public static final int TRIANGLE_NUM = 100;
    public static final Circle.circleFilter FILTER = new Circle.distantFilter(); //new Circle.areaFilter();

    public static String toStr() {
        return "Constant{" +
                "INTERVAL=" + INTERVAL +
                ", RADIUS=" + RADIUS +
                ", IGNORE_DATE=" + IGNORE_DATE +
                ", REJECT_R_LESS_P5=" + REJECT_R_LESS_P5 +
                ", COMPARE_DISTANCE=" + Arrays.toString(COMPARE_DISTANCE) +
                ", PRUNE_NUM=" + Arrays.toString(PRUNE_NUM) +
                ", TRIANGLE_NUM=" + TRIANGLE_NUM +
                ", FILTER=" + FILTER +
                '}';
    }
}
