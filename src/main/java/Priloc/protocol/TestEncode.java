package Priloc.protocol;

import Priloc.data.TimeLocationData;
import Priloc.data.Trajectory;
import Priloc.data.TrajectoryReader;
import Priloc.geo.Location;
import Priloc.utils.Constant;

import java.util.List;

import static java.lang.System.exit;

public class TestEncode {
//    public static void main(String[] args) throws Exception {
//        String[] negativePath = new String[]{
//                "./Geolife Trajectories 1.3/Data/000/Trajectory/20090503033926.plt",
//                "./Geolife Trajectories 1.3/Data/000/Trajectory/20090705025307.plt"
//        };
//        TrajectoryReader[] negativeReaders = new TrajectoryReader[negativePath.length];
//        for (int i = 0; i < negativePath.length; i++) {
//            negativeReaders[i] = new TrajectoryReader(negativePath[i]);
//        }
//        Trajectory[] negativeTrajectories = new Trajectory[negativePath.length];
//        for (int i = 0; i < negativePath.length; i++) {
//            negativeTrajectories[i] = negativeReaders[i].load();
//        }
//        double maxError = 0;
//        for (int i = 0; i < negativeTrajectories.length; i++) {
//            List<TimeLocationData> tlds = negativeTrajectories[i].getTLDs();
//            for(int j = 0; j < tlds.size() - 1; j++) {
//                Location l1 = tlds.get(j).getLoc();
//                Location l2 = tlds.get(j + 1).getLoc();
//                double expect = l1.distance(l2);
//                if (expect > 2 * Constant.RADIUS) {
//                    continue;
//                }
//                double actual = l1.encodeDistance(l2);
//                //System.out.println(Math.abs(expect - actual));
//                if (Math.abs(expect - actual) > maxError) {
//                    maxError = Math.abs(expect - actual);
//                    System.out.println(maxError);
//                }
////                if (Math.abs(expect - actual) > 1) {
////                    System.out.println(l1);
////                    System.out.println(l2);
////                    System.out.println(expect);
////                    System.out.println(actual);
////                }
//            }
//        }
//    }

    public static void main(String[] args) throws Exception {
        String[] negativePath = new String[]{
                "./C++/dataset",
        };
        TrajectoryReader[] negativeReaders = new TrajectoryReader[negativePath.length];
        for (int i = 0; i < negativePath.length; i++) {
            negativeReaders[i] = new TrajectoryReader(negativePath[i]);
        }
        Trajectory[] negativeTrajectories = new Trajectory[negativePath.length];
        for (int i = 0; i < negativePath.length; i++) {
            negativeTrajectories[i] = negativeReaders[i].load();
        }
        double maxError = 0;
        for (int i = 0; i < negativeTrajectories.length; i++) {
            List<TimeLocationData> tlds = negativeTrajectories[i].getTLDs();
            for(int j = 0; j < tlds.size() - 1; j++) {
                Location l1 = tlds.get(j).getLoc();
                Location l2 = tlds.get(j + 1).getLoc();
                double expect = l1.distance(l2);
                if (expect > 2 * Constant.RADIUS) {
                    continue;
                }
                double actual = l1.encodeDistance(l2);
                System.out.println(expect);
                System.out.println(actual);
            }
        }
    }
}
