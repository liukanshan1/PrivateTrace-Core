package Priloc.data;

import Priloc.area.basic.EncryptedCircle;
import Priloc.utils.Constant;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 第一个节点不包含数据
 */
public class EncTrajectory implements Serializable {
    private List<EncTmLocData> eTLDs;
    private String name;

    public EncTrajectory(Trajectory trajectory) {
        this.name = trajectory.getName();
        List<TimeLocationData> tlds = trajectory.getTLDs();
        this.eTLDs = new ArrayList<>();
        EncTmLocData pETLD = null;
        for (int i = 0; i < tlds.size(); i++) {
            TimeLocationData tld = tlds.get(i);
            EncTmLocData cETLD = tld.encrypt();
            if (pETLD != null) {
                pETLD.setNext(cETLD);
            }
            cETLD.setPrevious(pETLD);
            eTLDs.add(cETLD);
            pETLD = cETLD;
        }
    }

    public EncTrajectory(EncryptedCircle encryptedCircle) {
        this.name = "范围轨迹";
        eTLDs = new ArrayList<>();
        EncTmLocData pETLD = null;
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 60; j += Constant.INTERVAL) {
                Date date = new Date(2008, Calendar.JUNE, 24, i, j);
                EncTmLocData cETLD = new EncTmLocData(encryptedCircle, date);
                if (pETLD != null) {
                    pETLD.setNext(cETLD);
                }
                cETLD.setPrevious(pETLD);
                eTLDs.add(cETLD);
                pETLD = cETLD;
            }
        }
    }

    @Override
    public String toString() {
        return "EncTrajectory{" +
                "name=" + name +
                '}';
    }

    public List<EncTmLocData> geteTLDs() {
        return eTLDs;
    }

    public Date getStartDate() {
        return this.eTLDs.get(0).getDate();
    }

    public Date getEndDate() {
        return this.eTLDs.get(eTLDs.size() - 1).getDate();
    }

//    private void writeObject(ObjectOutputStream out) throws IOException {
//        //只序列化以下3个成员变量
//        out.writeObject(this.eTLDs);
//        out.writeObject(this.name);
//    }
//
//    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//        //注意：read()的顺序要和write()的顺序一致。
//        this.eTLDs = (List<EncTmLocData>) in.readObject();
//        this.name = (String) in.readObject();
//        EncTmLocData prev = null;
//        for (EncTmLocData curr : eTLDs) {
//            curr.setPrevious(prev);
//            if (prev != null) {
//                prev.setNext(curr);
//            }
//            prev = curr;
//        }
//    }
}
