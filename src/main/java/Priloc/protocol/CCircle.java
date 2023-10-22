package Priloc.protocol;

import Priloc.area.basic.EncryptedCircle;
import Priloc.data.EncTmLocData;
import Priloc.data.EncTrajectory;
import Priloc.utils.User;
import Priloc.utils.Utils;
import sg.smu.securecom.protocol.Paillier;
import sg.smu.securecom.protocol.PaillierThdDec;

import java.math.BigInteger;
import java.util.*;

@Deprecated
public class CCircle {
    private List<EncryptedCircle> circles;
    private TreeMap<EncSquareDistance, Integer> circleMap;
    private int rootIndex = 0;
    private EncryptedCircle rootCircle;
    private double maxRadius = 0;
    private static final List<EncTrajectory> workLoad = new LinkedList<>();

    public CCircle(List<EncryptedCircle> circles, Paillier pai, PaillierThdDec cp, PaillierThdDec csp) {
        this.circles = circles;
        this.rootCircle = circles.get(rootIndex);
        circleMap = new TreeMap<>();
    }

    public void add(EncryptedCircle[] encryptedCircles) {
        for (EncryptedCircle encryptedCircle : encryptedCircles) {
            circles.add(encryptedCircle);
        }
    }

    public void init() {
        for (int i = 1; i < circles.size(); i++) {
            circleMap.put(rootCircle.encSquareDistance(circles.get(i)), i);
            maxRadius = Math.max(maxRadius, circles.get(i).getRadius());
        }
    }

    public synchronized void addWork(EncTrajectory encTrajectory){
        workLoad.add(encTrajectory);
    }

    public synchronized void addWork(EncTrajectory[] encTrajectory){
        workLoad.addAll(Arrays.asList(encTrajectory));
    }

    private synchronized EncTrajectory popWork() {
        if(workLoad.size() == 0) return null;
        EncTrajectory res = workLoad.get(0);
        workLoad.remove(0);
        return res;
    }

    private int compare(EncryptedCircle circle) {
        // 先和Root比较
        if (rootCircle.isIntersect(circle)) {
            return -1;
        }
        // 再和其他比较
        BigInteger squareDistance = rootCircle.squareDistance(circle);
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
            return 0;
        }
        return compare(circle, subCirclesMap);
    }

    private int compare(EncryptedCircle circle, Map<EncSquareDistance, Integer> subCirclesMap) {
        // System.out.println("比较数量" + subCirclesMap.size());
        int pruneNum = 0;
        for (Map.Entry<EncSquareDistance, Integer> entry : subCirclesMap.entrySet()) {
            int i = entry.getValue();
            // int res = circles.get(i).noHowFarAway(circle, pai, cp, csp).first;
            int res = circles.get(i).howFarAway(circle).first;
            if (res == -1) {
                return -1;
            }
            pruneNum = Math.min(pruneNum, res);
        }
        return pruneNum;
    }

    private boolean compare(EncTrajectory eTrajectory) {
        // 时间裁剪
        List<EncTmLocData> eTLDs = eTrajectory.geteTLDs();
        for (int i = 0; i < eTLDs.size(); i++) {
            EncTmLocData eTLD = eTLDs.get(i);
            int res = compare(eTLD.getCircle());
            if (res == -1) {
                return false;
            }
            i += res;
        }
        return true;
    }

    public void run() {
        while (true) {
            EncTrajectory encTrajectory = popWork();
            if (encTrajectory == null) {
                break;
            }
            boolean res = compare(encTrajectory);
            if (res) {
                System.out.println(encTrajectory + "安全");
            } else {
                System.out.println(encTrajectory + "与范围相交！！！");
            }
        }
    }
}
