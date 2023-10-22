package Priloc.area.basic;

import Priloc.utils.Constant;
import Priloc.utils.User;
import Priloc.utils.Utils;
import sg.smu.securecom.keys.PaillierKey;
import sg.smu.securecom.protocol.Paillier;
import sg.smu.securecom.protocol.PaillierThdDec;
import sg.smu.securecom.protocol.SecMul;

import java.io.Serializable;
import java.math.BigInteger;

public class EncryptedPoint implements Serializable {
//    private BigInteger encX, encY, encZ;
    private BigInteger encX4, encY4, encZ4;
    private BigInteger encXX, encYY, encZZ;

    public EncryptedPoint(double x, double y, double z) {
        encrypt(x, y, z);
    }

    public EncryptedPoint(Point point) {
        this(point.getX(), point.getY(), point.getZ());
    }

    private void encrypt(double x, double y, double z) {
//        this.encX = Utils.encryptDouble(x, paillier);
//        this.encY = Utils.encryptDouble(y, paillier);
//        this.encZ = Utils.encryptDouble(z, paillier);
        this.encX4 = Utils.encryptDouble(x, Constant.FIXED_POINT / 2);
        this.encY4 = Utils.encryptDouble(y, Constant.FIXED_POINT / 2);
        this.encZ4 = Utils.encryptDouble(z, Constant.FIXED_POINT / 2);
        this.encXX = Utils.encryptDouble(x * x);
        this.encYY = Utils.encryptDouble(y * y);
        this.encZZ = Utils.encryptDouble(z * z);
    }

    public BigInteger squareDistance(EncryptedPoint other) {
        BigInteger x2_x2 = User.pai.add(encXX, other.encXX);
        BigInteger y2_y2 = User.pai.add(encYY, other.encYY);
        BigInteger z2_z2 = User.pai.add(encZZ, other.encZZ);
        BigInteger x2_x2_y2_y2 = User.pai.add(x2_x2, y2_y2);
        BigInteger x2_x2_y2_y2_z2_z2 = User.pai.add(x2_x2_y2_y2, z2_z2);
        BigInteger x1_x2 = SecMul.secMul(encX4, other.encX4, User.pai, User.cp, User.csp);
        BigInteger y1_y2 = SecMul.secMul(encY4, other.encY4, User.pai, User.cp, User.csp);
        BigInteger z1_z2 = SecMul.secMul(encZ4, other.encZ4, User.pai, User.cp, User.csp);
        BigInteger x1_x2_2 = User.pai.multiply(x1_x2, 2);
        BigInteger y1_y2_2 = User.pai.multiply(y1_y2, 2);
        BigInteger z1_z2_2 = User.pai.multiply(z1_z2, 2);
        BigInteger x1_x2_2_y1_y2_2 = User.pai.add(x1_x2_2, y1_y2_2);
        BigInteger x1_x2_2_y1_y2_2_z1_z2_2 = User.pai.add(x1_x2_2_y1_y2_2, z1_z2_2);
        return User.pai.sub(x2_x2_y2_y2_z2_z2, x1_x2_2_y1_y2_2_z1_z2_2);
    }

    public Point decrypt() {
        double x = Math.sqrt(User.pai.decrypt(encXX).longValue() / Math.pow(10, Constant.FIXED_POINT));
        double y = Math.sqrt(User.pai.decrypt(encYY).longValue() / Math.pow(10, Constant.FIXED_POINT));
        double z = Math.sqrt(User.pai.decrypt(encZZ).longValue() / Math.pow(10, Constant.FIXED_POINT));
        return new Point(x, y, z);
    }
}
