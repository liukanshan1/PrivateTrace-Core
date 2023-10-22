package Priloc.data;

import Priloc.area.basic.Circle;
import Priloc.area.basic.Point;
import Priloc.geo.Location;
import Priloc.utils.Constant;

import java.io.Serializable;
import java.util.Date;

public class TimeLocationData implements Serializable {
    private Location loc;
    private Date date;
    private double accuracy = Constant.RADIUS;
    private TimeLocationData nTLD = null;
    private TimeLocationData pTLD = null;

    public TimeLocationData(Location loc, Date date) {
        this.loc = loc;
        this.date = date;
    }

    public TimeLocationData(Location loc, Date date, TimeLocationData nTLD) {
        this.loc = loc;
        this.date = date;
        this.nTLD = nTLD;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setNext(TimeLocationData nTLD) {
        this.nTLD = nTLD;
    }

    public void setPrevious(TimeLocationData pTLD) {
        this.pTLD = pTLD;
    }

    @Override
    public String toString() {
        return "TrajectoryData{" +
                "loc=" + loc +
                ", date=" + date +
                '}';
    }

    public Location getLoc() {
        return loc;
    }

    public Date getDate() {
        return date;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public boolean hasNext() {
        return nTLD != null;
    }

    public TimeLocationData next() {
        return nTLD;
    }

    public boolean hasPrevious() {
        return pTLD != null;
    }

    public TimeLocationData previous() {
        return pTLD;
    }

    public EncTmLocData encrypt(){
        return new EncTmLocData(this);
    }

    public Circle getCircle() {
        return new Circle(new Point(loc), accuracy);
    }
}
