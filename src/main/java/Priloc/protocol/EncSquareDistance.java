package Priloc.protocol;

import Priloc.utils.User;
import sg.smu.securecom.protocol.Paillier;
import sg.smu.securecom.protocol.PaillierThdDec;
import sg.smu.securecom.protocol.SecCmp;

import java.io.Serializable;
import java.math.BigInteger;

public class EncSquareDistance implements Comparable<EncSquareDistance>, Serializable {
    private BigInteger squareDistance;

    public EncSquareDistance(BigInteger squareDistance) {
        this.squareDistance = squareDistance;
    }

    /**
     * Compares this object with the specified object for order.
     * Returns a negative integer, zero, or a positive integer
     * as this object is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(EncSquareDistance o) {
        int result1 = SecCmp.secCmp(squareDistance, o.squareDistance, User.pai, User.cp, User.csp);
        if (result1 == 1) {
            return -1;
        }
        int result2 = SecCmp.secCmp(o.squareDistance, squareDistance, User.pai, User.cp, User.csp);
        if (result1 == result2) {
            return 0;
        } else {
            return 1;
        }
    }
}
