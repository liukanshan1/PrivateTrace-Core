package Priloc.area.basic;

import Priloc.protocol.EncSquareDistance;
import Priloc.utils.Constant;
import Priloc.utils.Pair;
import Priloc.utils.User;
import Priloc.utils.Utils;
import sg.smu.securecom.keys.PaillierKey;
import sg.smu.securecom.protocol.Paillier;
import sg.smu.securecom.protocol.PaillierThdDec;
import sg.smu.securecom.protocol.SecCmp;

import java.io.Serializable;
import java.math.BigInteger;

public class EncryptedCircle implements Serializable {
    private EncryptedPoint encryptedPoint;
    private double radius;

    public EncryptedCircle(Circle circle) {
        this.encryptedPoint = new EncryptedPoint(circle.getCenter());
        this.radius = circle.getRadius();
    }

    public EncryptedCircle(EncryptedPoint encryptedPoint, double radius) {
        this.encryptedPoint = encryptedPoint;
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    public BigInteger squareDistance(EncryptedCircle other) {
        return encryptedPoint.squareDistance(other.encryptedPoint);
    }

    public EncSquareDistance encSquareDistance(EncryptedCircle other) {
        return new EncSquareDistance(squareDistance(other));
    }

    public int isIntersect(double maxSquareDistance, BigInteger squareDistance) {
        BigInteger maxSquareDistanceEnc = Utils.encryptDouble(maxSquareDistance);
        return SecCmp.secCmp(squareDistance, maxSquareDistanceEnc, User.pai, User.cp, User.csp);
    }

    /**
     * -1 相交
     * 0 不允许 prune
     * n 允许 prune 2n+1 个节点
     */
    public Pair<Integer, BigInteger> howFarAway(EncryptedCircle other) {
        double maxSquareDistance = (radius + other.radius) * (radius + other.radius);
        BigInteger squareDistance = squareDistance(other);
        int result = isIntersect(maxSquareDistance, squareDistance);
        if (result == 0) {
            for (int i = 0; i < Constant.COMPARE_DISTANCE.length; i++) {
                result = isIntersect(Constant.COMPARE_DISTANCE[i], squareDistance);
                if (result != 0) {
                    // System.out.println(("修剪" + Constant.PRUNE_NUM[i]));
                    return new Pair<>(Constant.PRUNE_NUM[i], squareDistance);
                }
            }
            // System.out.println(("修剪" + Constant.PRUNE_NUM[Constant.PRUNE_NUM.length - 1]));
            return new Pair<>(Constant.PRUNE_NUM[Constant.PRUNE_NUM.length - 1], squareDistance);
        } else {
            return new Pair<>(-1, squareDistance);
        }
    }

    public Pair<Integer, BigInteger> noHowFarAway(EncryptedCircle other) {
        double maxSquareDistance = (radius + other.radius) * (radius + other.radius);
        BigInteger squareDistance = squareDistance(other);
        int result = isIntersect(maxSquareDistance, squareDistance);
        if (result == 0) {
            return new Pair<>(0, squareDistance);
        } else {
            return new Pair<>(-1, squareDistance);
        }
    }

    public boolean isIntersect(EncryptedCircle other) {
        double maxSquareDistance = (radius + other.radius) * (radius + other.radius);
        BigInteger squareDistance = squareDistance(other);
        BigInteger maxSquareDistanceEnc = Utils.encryptDouble(maxSquareDistance);
        int result = SecCmp.secCmp(squareDistance, maxSquareDistanceEnc, User.pai, User.cp, User.csp);
        if (result == 0) {
            return false;
        } else {
            return true;
        }
    }

    public Circle decrypt() {
        return new Circle(this.encryptedPoint.decrypt(), this.radius);
    }
}
