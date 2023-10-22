package Priloc.protocol;

import Priloc.data.EncTrajectory;
import Priloc.data.Trajectory;
import Priloc.data.TrajectoryReader;
import Priloc.utils.Constant;
import Priloc.utils.Pair;
import Priloc.utils.User;
import Priloc.utils.Utils;
import org.springframework.util.StopWatch;

import java.io.File;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println(Constant.toStr());
        User.pai.setDecryption(User.prikey);
        ExecutorService pool = Executors.newFixedThreadPool(Constant.THREAD);
        StopWatch stopWatch = new StopWatch();
        File dir = new File("./GeoLife Trajectories 1.3/data by date/68/20081116/");
        String[] tFiles = dir.list();
        // Part-1 人人比较
        // 初始化阳性
        Pair<CCircleTree, Trajectory[]> pair = getCCircleTree(pool, stopWatch);
        CCircleTree cCircleTree = pair.first;
        Trajectory[] positiveTrajectories = pair.second;
        //Utils.writeObject(cCircleTree, "tree.e");
        //Utils.writeObject(positiveTrajectories, "./pos.p");
        // 初始化阴性
        Pair<EncTrajectory[], Trajectory[]> pair1 = getEncTrajectories(pool, stopWatch);
        EncTrajectory[] eNegativeTrajectories = pair1.first;
        Trajectory[] negativeTrajectories = pair1.second;
        //Utils.writeObject(eNegativeTrajectories, "./neg.e");
        //Utils.writeObject(negativeTrajectories, "./neg.p");
        // 比较
//        cCircleTree.addWork(eNegativeTrajectories);
//        stopWatch.start("比较");
//        for (int i = 0; i < Constant.THREAD; i++) {
//            //System.out.println(cCircleTree.compare(eNegativeTrajectories[i]));
//            pool.execute(cCircleTree::run);
//        }
//        pool.shutdown();
//        pool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
//        stopWatch.stop();
//        System.out.println(stopWatch.prettyPrint());
//        System.out.println(stopWatch.getTotalTimeSeconds());
        // 明文下比较
        for (int i = 0; i < negativeTrajectories.length; i++) {
            for (Trajectory trajectory : positiveTrajectories) {
                if (trajectory.getName().equals(negativeTrajectories[i].getName())) {
                   // continue;
                }
                if (Trajectory.isIntersect(trajectory, negativeTrajectories[i])) {
                    System.out.println(negativeTrajectories[i]);
                    break;
                }
            }
        }
    }

    public static Pair<EncTrajectory[], Trajectory[]> getEncTrajectories(ExecutorService pool, StopWatch stopWatch) throws Exception{
        stopWatch.start("读取待比较的轨迹");
        String pathname = "./GeoLife Trajectories 1.3/Data/002/Trajectory/";
        File dir = new File(pathname);
        String[] negativePath = dir.list();
        TrajectoryReader[] negativeReaders = new TrajectoryReader[negativePath.length];
        for (int i = 0; i < negativePath.length; i++) {
            negativeReaders[i] = new TrajectoryReader(pathname + negativePath[i]);
        }
        Trajectory[] negativeTrajectories = new Trajectory[negativePath.length];
        for (int i = 0; i < negativePath.length; i++) {
            negativeTrajectories[i] = negativeReaders[i].load();
        }
        stopWatch.stop();
        System.out.println("数据读取完成" + stopWatch.shortSummary());
        // 加密待比较轨迹
        EncTrajectory[] eNegativeTrajectories = new EncTrajectory[negativePath.length];
        Future<EncTrajectory>[] future1 = new Future[negativePath.length];
//        stopWatch.start("加密待比较轨迹");
//        for (int i = 0; i < negativePath.length; i++) {
//            future1[i] = pool.submit(negativeTrajectories[i]);
//        }
//        for (int i = 0; i < negativePath.length; i++) {
//            eNegativeTrajectories[i] = future1[i].get();
//        }
//        stopWatch.stop();
        System.out.println("加密完成" + stopWatch.shortSummary());
        return new Pair<>(eNegativeTrajectories, negativeTrajectories);
    }

    private static Pair<CCircleTree, Trajectory[]> getCCircleTree(ExecutorService pool, StopWatch stopWatch) throws Exception{
        // 读取阳性轨迹
        stopWatch.start("读取阳性轨迹");
        String pathname = "./GeoLife Trajectories 1.3/Data/001/Trajectory/";
        File dir = new File(pathname);
        String[] positivePath = dir.list();
        TrajectoryReader[] positiveReaders = new TrajectoryReader[positivePath.length];
        for (int i = 0; i < positivePath.length; i++) {
            positiveReaders[i] = new TrajectoryReader(pathname + positivePath[i]);
        }
        Trajectory[] positiveTrajectories = new Trajectory[positivePath.length];
        for (int i = 0; i < positivePath.length; i++) {
            positiveTrajectories[i] = positiveReaders[i].load();
        }
        stopWatch.stop();
        System.out.println("数据读取完成" + stopWatch.shortSummary());
        // 加密轨迹
        EncTrajectory[] ePositiveTrajectories = new EncTrajectory[positivePath.length];
        Future<EncTrajectory>[] future = new Future[positivePath.length];
        stopWatch.start("加密轨迹");
//        for (int i = 0; i < positivePath.length; i++) {
//            future[i] = pool.submit(positiveTrajectories[i]);
//        }
//        for (int i = 0; i < positivePath.length; i++) {
//            ePositiveTrajectories[i] = future[i].get();
//        }
//        stopWatch.stop();
        System.out.println("加密完成" + stopWatch.shortSummary());
        // 建立同心圆树
        CCircleTree cCircleTree = new CCircleTree();
//        for (int i = 0; i < positivePath.length; i++) {
//            cCircleTree.add(ePositiveTrajectories[i]);
//        }
        //stopWatch.start("建立同心圆树");
        //cCircleTree.init();
        stopWatch.stop();
        System.out.println("建立同心圆树完成" + stopWatch.shortSummary());
        return new Pair<>(cCircleTree, positiveTrajectories);
    }
}
