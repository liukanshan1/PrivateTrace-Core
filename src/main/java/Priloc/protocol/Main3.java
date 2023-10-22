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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main3 {
    public static void main(String[] args) throws Exception {
        System.out.println(Constant.toStr());
        User.pai.setDecryption(User.prikey);
        ExecutorService pool = Executors.newFixedThreadPool(Constant.THREAD);
        StopWatch stopWatch = new StopWatch();
        // Part-1 人人比较
        // 初始化阳性
        CCircleTree cCircleTree = (CCircleTree) Utils.readObject("./tree.e");
        Trajectory[] positiveTrajectories = (Trajectory[]) Utils.readObject("./pos.p");
        // 初始化阴性
        EncTrajectory[] eNegativeTrajectories = (EncTrajectory[]) Utils.readObject("./neg.e");
        Trajectory[] negativeTrajectories = (Trajectory[]) Utils.readObject("./neg.p");
        // 比较
        cCircleTree.addWork(eNegativeTrajectories);
        stopWatch.start("比较");
        for (int i = 0; i < Constant.THREAD; i++) {
            //System.out.println(cCircleTree.compare(eNegativeTrajectories[i]));
            pool.execute(cCircleTree::run);
        }
        pool.shutdown();
        pool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
        System.out.println(stopWatch.getTotalTimeSeconds());
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

    private static Pair<CCircleTree, Trajectory[]> getCCircleTree(ExecutorService pool, StopWatch stopWatch) throws Exception{
        // 读取阳性轨迹
        stopWatch.start("读取阳性轨迹");
        String[] positivePath = new String[]{
                "./Geolife Trajectories 1.3/Data/000/Trajectory/20090503033926.plt",
                "./Geolife Trajectories 1.3/Data/000/Trajectory/20090705025307.plt",
                "./Geolife Trajectories 1.3/Data/000/Trajectory/20090418172238.plt"
        };
        TrajectoryReader[] positiveReaders = new TrajectoryReader[positivePath.length];
        for (int i = 0; i < positivePath.length; i++) {
            positiveReaders[i] = new TrajectoryReader(positivePath[i]);
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
        for (int i = 0; i < positivePath.length; i++) {
            future[i] = pool.submit(positiveTrajectories[i]);
        }
        for (int i = 0; i < positivePath.length; i++) {
            ePositiveTrajectories[i] = future[i].get();
        }
        stopWatch.stop();
        System.out.println("加密完成" + stopWatch.shortSummary());
        // 建立同心圆树
        CCircleTree cCircleTree = new CCircleTree();
        for (int i = 0; i < positivePath.length; i++) {
            cCircleTree.add(ePositiveTrajectories[i]);
        }
        stopWatch.start("建立同心圆树");
        cCircleTree.init();
        stopWatch.stop();
        System.out.println("建立同心圆树完成" + stopWatch.shortSummary());
        return new Pair<>(cCircleTree, positiveTrajectories);
    }
}
