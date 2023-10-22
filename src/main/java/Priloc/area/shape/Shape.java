package Priloc.area.shape;

import Priloc.area.basic.Circle;
import Priloc.area.basic.EncryptedCircle;
import Priloc.utils.Constant;
import sg.smu.securecom.keys.PaillierKey;

public interface Shape {
    void init();
    Circle[] fitByCircle(int num, Circle.circleFilter filter, double strict);
    Circle[] fitByCircle(int num, Circle.circleFilter filter);
    default EncryptedCircle[] encrypt(PaillierKey pk, Circle[] circles) {
        return Circle.encrypt(circles, pk);
    }
    default Circle[] fit() {
        return fitByCircle(Constant.TRIANGLE_NUM, Constant.FILTER);
    }
    double checkCoverage(int num, Circle[] circles);
}
