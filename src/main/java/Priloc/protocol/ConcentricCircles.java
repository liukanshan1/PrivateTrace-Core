package Priloc.protocol;

import Priloc.area.basic.EncryptedCircle;
import Priloc.data.EncTmLocData;
import Priloc.utils.Pair;
import Priloc.utils.User;
import Priloc.utils.Utils;
import sg.smu.securecom.protocol.Paillier;
import sg.smu.securecom.protocol.PaillierThdDec;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

public class ConcentricCircles implements Serializable {
    private final ArrayList<EncTmLocData> eTLDs;
    private final HashMap<EncTmLocData, Integer> eTLDsIndex;
    private TreeMap<EncSquareDistance, Integer> circleMap;
    private final int rootIndex = 0;
    private final EncryptedCircle rootCircle;
    private double maxRadius = 0;
    private CCircleTree cCircleTree;
    //private boolean[] prune;
//    private Date startDate;
//    private Date endDate;


    public ConcentricCircles(ArrayList<EncTmLocData> eTLDs, CCircleTree cCircleTree) {
        this.cCircleTree = cCircleTree;
        this.eTLDs = eTLDs;
        this.eTLDsIndex = new HashMap<>();
        for (int i = 0; i < eTLDs.size(); i++) {
            EncTmLocData eTLD = eTLDs.get(i);
            eTLDsIndex.put(eTLD, i);
            eTLD.setcCircles(this);
        }
        this.rootCircle = eTLDs.get(rootIndex).getCircle();
    }

    public ConcentricCircles(EncTmLocData eTLD, CCircleTree cCircleTree) {
        this(new ArrayList<>(Arrays.asList(eTLD)), cCircleTree);
    }

    public void add(EncTmLocData eTLD) {
        eTLD.setcCircles(this);
        eTLDs.add(eTLD);
        eTLDsIndex.put(eTLD, eTLDs.size() - 1);
    }

//    public void setDates(Date startDate, Date endDate) {
//        this.startDate = startDate;
//        this.endDate = endDate;
//    }

    public TreeMap<EncSquareDistance, Integer> getCircleMap() {
        return circleMap;
    }

    public void setCircleMap(TreeMap<EncSquareDistance, Integer> circleMap) {
        this.circleMap = circleMap;
    }

    public double getMaxRadius() {
        return maxRadius;
    }

    public void setMaxRadius(double maxRadius) {
        this.maxRadius = maxRadius;
    }

    public void init() {
        this.circleMap = new TreeMap<>();
        for (int i = 1; i < eTLDs.size(); i++) {
            circleMap.put(rootCircle.encSquareDistance(eTLDs.get(i).getCircle()), i);
            maxRadius = Math.max(maxRadius, eTLDs.get(i).getCircle().getRadius());
        }
    }

    public int size() {
        return eTLDs.size();
    }

    private void prune(int i) {
        this.cCircleTree.prune(this, i);
    }

    public void prune(EncTmLocData eTld) {
        prune(eTLDsIndex.get(eTld));
    }

    public boolean compare(EncryptedCircle circle) {
        // 先和Root比较
        Pair<Integer, BigInteger> pair = rootCircle.howFarAway(circle);
        int compare = pair.first;
        if (compare == -1) {
            return false;
        } else if (compare > 0) {
            // 双向prune
            EncTmLocData previous = eTLDs.get(rootIndex);
            EncTmLocData next = previous;
            for(int j = 0; j < compare; j++) {
                if (previous.hasPrevious()) {
                    previous = previous.previous();
                    previous.prune();
                }
                if (next.hasNext()) {
                    next = next.next();
                    next.prune();
                }
            }
        }
        // 再和其他比较
        BigInteger squareDistance = pair.second;
        double r = maxRadius + circle.getRadius();
        BigInteger radiusSquare = Utils.encryptDouble(r * r);
//        BigInteger doubleRadius = Utils.encryptDouble(2 * r, pai, 0, false);
//        BigInteger doubleSquareDistanceRadius = SecMul.secMul(squareDistance, doubleRadius, pai, cp, csp);
//        BigInteger floorDistance = pai.sub(pai.add(squareDistance, radiusSquare), doubleSquareDistanceRadius);
//        BigInteger ceilDistance = pai.add(pai.add(squareDistance, radiusSquare), doubleSquareDistanceRadius);
        BigInteger floorDistance = radiusSquare;
        BigInteger ceilDistance = User.pai.add(radiusSquare, User.pai.multiply(squareDistance, 2));
        // greatest key less than the specified key
        EncSquareDistance floorKey = circleMap.floorKey(new EncSquareDistance(floorDistance));
        // least key greater than the specified key
        EncSquareDistance ceilingKey = circleMap.ceilingKey(new EncSquareDistance(ceilDistance));

        Map<EncSquareDistance, Integer> subCirclesMap;
        if (floorKey != null && ceilingKey != null) {
            subCirclesMap = circleMap.subMap(floorKey, false, ceilingKey, false);
        } else if (floorKey == null && ceilingKey == null) {
            subCirclesMap = circleMap;
        } else if (floorKey == null) {
            subCirclesMap = circleMap.headMap(ceilingKey, false);
        } else {
            subCirclesMap = circleMap.tailMap(floorKey, false);
        }
        if (subCirclesMap.size() == 0) {
            return true;
        }
        return compare(circle, subCirclesMap);
    }

    /**
     * false是与阳性有交集
     */
    private boolean compare(EncryptedCircle circle, Map<EncSquareDistance, Integer> subCirclesMap) {
        //System.out.println("比较数量" + subCirclesMap.size());
        for (Map.Entry<EncSquareDistance, Integer> entry : subCirclesMap.entrySet()) {
            int i = entry.getValue();
            if (this.cCircleTree.getPrune(this, i)) {
                continue;
            }
            int compare = eTLDs.get(i).getCircle().howFarAway(circle).first;
            if (compare == -1) {
                return false;
            } else if (compare > 0) {
                this.prune(i);
                // 双向prune
                EncTmLocData previous = eTLDs.get(i);
                EncTmLocData next = previous;
                for(int j = 0; j < compare; j++) {
                    if (previous.hasPrevious()) {
                        previous = previous.previous();
                        previous.prune();
                    }
                    if (next.hasNext()) {
                        next = next.next();
                        next.prune();
                    }
                }
            }
        }
        return true;
    }

//    public static void main(String[] args) throws InterruptedException {
//        System.out.println("false是阳性");
//        int key_len = 512;
//        int positiveNum = 200;
//        int peopleNum = 2000;
//        int maxRadius = 50;
//        User user = new User(key_len);
//        ArrayList<EncryptedCircle> eCircles = new ArrayList<>();
//        ArrayList<Circle> circles = new ArrayList<>();
//        ArrayList<EncryptedCircle> eCircles2 = new ArrayList<>();
//        ArrayList<Circle> circles2 = new ArrayList<>();
//        Random rnd = new Random();
//        Point point1 = new Point(rnd.nextDouble(5000), rnd.nextDouble(5000), rnd.nextDouble(5000));
//        Point point2 = new Point(rnd.nextDouble(5000), rnd.nextDouble(5000), rnd.nextDouble(5000));
//        Point point3 = new Point(rnd.nextDouble(5000), rnd.nextDouble(5000), rnd.nextDouble(5000));
//        Plane plane = new Plane(point1, point2, point3);
//        // 生成阳性圆
//        for (int i = 0; i < positiveNum; i++) {
//            point1 = plane.getPointFromXY(rnd.nextDouble(15000), rnd.nextDouble(15000));
//            Circle circle = new Circle(point1, rnd.nextDouble(maxRadius));
//            circles.add(circle);
//        }
//        Timestamp t1 = new Timestamp(System.currentTimeMillis());
//        for (int i = 0; i < positiveNum; i++) {
//            eCircles.add(circles.get(i).encrypt(user.pai.getPublicKey()));
//        }
//        Timestamp t2 = new Timestamp(System.currentTimeMillis());
//        System.out.println("加密时间：" + (t2.getTime() - t1.getTime()) / 1000);
//        // 初始化比较器
//        ConcentricCircles concentricCircles = new ConcentricCircles(eCircles, user.pai, user.cp, user.csp);
//        Timestamp t3 = new Timestamp(System.currentTimeMillis());
//        concentricCircles.init();
//        Timestamp t4 = new Timestamp(System.currentTimeMillis());
//        System.out.println("初始化时间：" + (t4.getTime() - t3.getTime()) / 1000);
//        // 生成待测人员
//        for (int i = 0; i < peopleNum; i++) {
//            point1 = plane.getPointFromXY(rnd.nextDouble(15000), rnd.nextDouble(15000));
//            Circle circle = new Circle(point1, rnd.nextDouble(maxRadius));
//            circles2.add(circle);
//            eCircles2.add(circle.encrypt(user.pai.getPublicKey()));
//        }
//        // 比较
//        List<Boolean> pResult = new ArrayList<>();
//        for (int i = 0; i < peopleNum; i++) {
//            boolean result = !circles2.get(i).isIntersect(circles);
//            pResult.add(result);
//        }
//        int wrongNum = 0;
//        Timestamp t5 = new Timestamp(System.currentTimeMillis());
////        for (int i = 0; i < peopleNum; i++) {
////            boolean eResult = circleCollection.compare(eCircles2.get(i));
////            boolean result = pResult.get(i);
////            System.out.println(i + " 密文结果：" + eResult + " 明文结果：" + result);
////            if (eResult != result) {
////                wrongNum++;
////            }
////        }
//        int workerNum = 8;
//        int workLoad = peopleNum / workerNum;
//        ArrayList<Worker> workers = new ArrayList<>();
//        for (int i = 0; i < workerNum; i++) {
//            Worker worker = new Worker(i*workLoad, Math.min((i+1)*workLoad, peopleNum), concentricCircles, eCircles2, pResult);
//            workers.add(worker);
//            worker.start();
//        }
//        for (Worker worker : workers) {
//            worker.join();
//        }
//        Timestamp t6 = new Timestamp(System.currentTimeMillis());
//        System.out.println("错误案例 " + wrongNum);
//        System.out.println("加密时间：" + (t2.getTime() - t1.getTime()) / 1000.0);
//        System.out.println("初始化时间：" + (t4.getTime() - t3.getTime()) / 1000.0);
//        System.out.println("比较时间：" + (t6.getTime() - t5.getTime()) / 1000.0);
//    }
}
