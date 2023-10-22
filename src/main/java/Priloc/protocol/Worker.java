package Priloc.protocol;

import Priloc.area.basic.EncryptedCircle;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class Worker extends Thread {

    private int startIndex;
    private int endIndex;
    private ConcentricCircles concentricCircles;
    private ArrayList<EncryptedCircle> eCircles2;
    private List<Boolean> pResult;

    public Worker(int startIndex, int endIndex, ConcentricCircles concentricCircles, ArrayList<EncryptedCircle> eCircles2, List<Boolean> pResult) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.concentricCircles = concentricCircles;
        this.eCircles2 = eCircles2;
        this.pResult = pResult;
    }

    @Override
    public void run() {
        for (int i = startIndex; i < endIndex; i++) {
            boolean eResult = concentricCircles.compare(eCircles2.get(i));
            boolean result = pResult.get(i);
            System.out.println(this.getId() + "线程 " + i + " 密文结果：" + eResult + " 明文结果：" + result);
        }
    }
}
