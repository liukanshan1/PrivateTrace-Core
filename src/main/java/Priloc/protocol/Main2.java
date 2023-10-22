package Priloc.protocol;

import Priloc.area.basic.Circle;
import Priloc.area.basic.EncryptedCircle;
import Priloc.area.shape.Polygon;
import Priloc.area.shape.Shape;
import Priloc.area.shape.Triangle;
import Priloc.data.EncTrajectory;
import Priloc.data.TimeLocationData;
import Priloc.data.Trajectory;
import Priloc.geo.Location;
import Priloc.utils.Constant;
import Priloc.utils.Pair;
import Priloc.utils.User;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static Priloc.protocol.Main.getEncTrajectories;

public class Main2 {
    public static void main(String[] args) throws Exception {
        System.out.println(Constant.toStr());
        User.pai.setDecryption(User.prikey);
        ExecutorService pool = Executors.newFixedThreadPool(Constant.THREAD);
        StopWatch stopWatch = new StopWatch();
        // Part-1 范围比较
        // 生成范围
        stopWatch.start("创建范围");
        Location l1 = new Location(39.913385, 116.415884);
        Location l2 = new Location(39.915744, 116.417761);
        Location l3 = new Location(39.91306, 116.419576);
        Triangle triangle = new Triangle(l1, l2, l3); // 王府井 市中心三角
        l1 = new Location(40.004086, 116.393274);
        l2 = new Location(39.994413, 116.393884);
        l3 = new Location(39.994911, 116.407646);
        Location l4 = new Location(40.004721, 116.407036);
        Polygon p1 = new Polygon(l1, l2, l3, l4); // 国家体育中心鸟巢
        Shape[] shapes = new Shape[2];
        shapes[0] = triangle;
        shapes[1] = p1;
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeSeconds());
        // 拟合
        Future<Circle[]>[] futures = new Future[shapes.length];
        Circle[][] temp = new Circle[shapes.length][];
        stopWatch.start("拟合");
        for (int i = 0; i < shapes.length; i++) {
            futures[i] = pool.submit(shapes[i]::fit);
        }
        for (int i = 0; i < shapes.length; i++) {
            temp[i] = futures[i].get();
        }
        stopWatch.stop();
        List<Circle> cs = new ArrayList<>();
        for (int i = 0; i < shapes.length; i++) {
            System.out.println("拟合效果：" + shapes[i].checkCoverage(10000000, temp[i]));
            cs.addAll(Arrays.asList(temp[i]));
        }
        Circle[] circles = cs.toArray(new Circle[cs.size()]);
        System.out.println(stopWatch.getTotalTimeSeconds());
        // 加密
        EncryptedCircle[] encCircles = new EncryptedCircle[circles.length];
        Future<EncryptedCircle>[] future = new Future[circles.length];
        stopWatch.start("加密");
        for (int i = 0; i < circles.length; i++) {
            future[i] = pool.submit(circles[i]::enc);
        }
        for (int i = 0; i < circles.length; i++) {
            encCircles[i] = future[i].get();
        }
        stopWatch.stop();
        CCircleTree cCircleTree = new CCircleTree();
        for (int i = 0; i < encCircles.length; i++) {
            cCircleTree.add(new EncTrajectory(encCircles[i]));
        }
        System.out.println(stopWatch.getTotalTimeSeconds());
        // 建立范围树
        stopWatch.start("建立范围树");
        cCircleTree.init(true);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeSeconds());
        // 初始化阴性
        Pair<EncTrajectory[], Trajectory[]> pair1 = getEncTrajectories(pool, stopWatch);
        EncTrajectory[] eNegativeTrajectories = pair1.first;
        Trajectory[] negativeTrajectories = pair1.second;
        // 比较
        cCircleTree.addWork(eNegativeTrajectories);
        stopWatch.start("比较");
        for (int i = 0; i < Constant.THREAD; i++) {
            pool.execute(cCircleTree::run);
        }
        pool.shutdown();
        pool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
        System.out.println(stopWatch.getTotalTimeSeconds());
        // 明文下比较
        for (int i = 0; i < negativeTrajectories.length; i++) {
            List<TimeLocationData> timeLocationData = negativeTrajectories[i].getTLDs();
            for (int j = 0; j < timeLocationData.size(); j++) {
                Circle circle = timeLocationData.get(j).getCircle();
                if (circle.isIntersect(Arrays.asList(circles))) {
                    System.out.println("轨迹" + negativeTrajectories[i] + "与范围相交!!!");
                    break;
                }
            }
        }
    }
}
