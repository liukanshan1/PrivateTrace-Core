package Priloc.data;

import Priloc.area.basic.Circle;
import Priloc.protocol.CCircleTree;
import Priloc.utils.Constant;
import Priloc.utils.User;
import Priloc.utils.Utils;
import sg.smu.securecom.keys.PaillierKey;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * 第一个节点不包含数据
 */
public class Trajectory implements Callable<EncTrajectory>, Serializable {
    private List<TimeLocationData> TLDs;
    private String name;

    public Trajectory(List<TimeLocationData> tlds, String name) {
        this.TLDs = tlds;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Trajectory{" +
                "name='" + name + '\'' +
                "size='" + TLDs.size() + '\'' +
                '}';
    }

    public List<TimeLocationData> getTLDs() {
        return TLDs;
    }

    public Date getStartDate() {
        return this.TLDs.get(0).getDate();
    }

    public Date getEndDate() {
        return this.TLDs.get(TLDs.size() - 1).getDate();
    }

    public EncTrajectory encrypt() {
        return new EncTrajectory(this);
    }

    public static boolean isIntersect(Trajectory t1, Trajectory t2) {
        // 判断时间重合
        Map<Date, List<Circle>> positiveCircles = new HashMap<>();
        for (TimeLocationData tld : t1.TLDs) {
            Date startDate = tld.getDate();
            List<Circle> circles = positiveCircles.get(startDate);
            if (circles == null) {
                circles = new ArrayList<>();
            }
            circles.add(tld.getCircle());
            positiveCircles.put(startDate, circles);
        }
        for (TimeLocationData tld : t2.TLDs) {
            Date startDate = tld.getDate();
            List<Circle> circles = positiveCircles.get(startDate);
            if (circles == null) {
                continue;
            }
            if (tld.getCircle().isIntersect(circles)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public EncTrajectory call() {
        return encrypt();
    }

//    /**
//     * 自定义序列化 搭配transist使用
//     */
//    private void writeObject(ObjectOutputStream out) throws IOException {
//        //只序列化以下3个成员变量
//        out.writeObject(this.TLDs);
//        out.writeObject(this.name);
//    }
//
//    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//        //注意：read()的顺序要和write()的顺序一致。
//        this.TLDs = (List<TimeLocationData>) in.readObject();
//        this.name = (String) in.readObject();
//        TimeLocationData prev = null;
//        for (TimeLocationData curr : TLDs) {
//            curr.setPrevious(prev);
//            if (prev != null) {
//                prev.setNext(curr);
//            }
//            prev = curr;
//        }
//    }
}
