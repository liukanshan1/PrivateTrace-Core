package Priloc.utils;

import sg.smu.securecom.protocol.Paillier;

import java.io.*;
import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;

public class Utils {

    public static BigInteger encryptDouble(double d) {
        return User.pai.encrypt(BigInteger.valueOf(Math.round(d * Math.pow(10, Constant.FIXED_POINT))));
    }

    public static BigInteger encryptDouble(double d, int scale) {
        return User.pai.encrypt(BigInteger.valueOf(Math.round(d * Math.pow(10, scale))));
    }

    public static BigInteger encryptDouble(double d, int scale, Boolean floorOrCeil) {
        if(floorOrCeil) {
            return User.pai.encrypt(BigInteger.valueOf((long) Math.floor(d * Math.pow(10, scale))));
        } else {
            return User.pai.encrypt(BigInteger.valueOf((long) Math.ceil(d * Math.pow(10, scale))));
        }
    }

    public static Date getStart(Date d) {
        d.setSeconds(0);
        d.setMinutes(d.getMinutes() - d.getMinutes() % Constant.INTERVAL);
        return d;
    }

    public static void writeObject(Object o, String path) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
        //调用我们自定义的writeObject()方法
        out.writeObject(o);
        out.close();
    }

    public static Object readObject(String path) throws IOException, ClassNotFoundException {
        ObjectInputStream in=new ObjectInputStream(new FileInputStream(path));
        Object o = in.readObject();
        in.close();
        return o;
    }
}
