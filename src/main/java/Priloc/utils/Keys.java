package Priloc.utils;

import sg.smu.securecom.keys.KeyGen;
import sg.smu.securecom.keys.PaillierKey;
import sg.smu.securecom.keys.PaillierPrivateKey;
import sg.smu.securecom.keys.PaillierThdPrivateKey;
import sg.smu.securecom.protocol.Paillier;
import sg.smu.securecom.protocol.PaillierThdDec;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

public class Keys implements Serializable {

    protected static final int f = 1;

    protected static Random rnd = new Random();

    // secure level 2^80 bits
    protected static int sigma = 80;

    protected PaillierPrivateKey prikey = null;
    protected Paillier pai = null;
    protected PaillierThdPrivateKey[] ThdKey = null;
    protected PaillierThdDec cp = null;
    protected PaillierThdDec csp = null;

    public Keys(int len) {
        BigInteger p = new BigInteger(len, 64, rnd);
        BigInteger q = new BigInteger(len, 64, rnd);

        BigInteger n = p.multiply(q);
        BigInteger lambda = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        prikey = new PaillierPrivateKey(n, lambda);
        pai = new Paillier(new PaillierKey(n));

        ThdKey = KeyGen.genThdKey(prikey.getLambda(), prikey.getN(), prikey.getNsquare(), rnd);
        cp = new PaillierThdDec(ThdKey[0]);
        csp = new PaillierThdDec(ThdKey[1]);
    }
}
