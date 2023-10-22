package Priloc.data;

import Priloc.area.basic.EncryptedCircle;
import Priloc.area.basic.EncryptedPoint;
import Priloc.protocol.ConcentricCircles;
import sg.smu.securecom.keys.PaillierKey;
import sg.smu.securecom.protocol.SecMul;

import java.io.Serializable;
import java.util.Date;

public class EncTmLocData implements Serializable {
    private EncryptedCircle eCircle;
    private Date date;
    private EncTmLocData nETLD = null;
    private EncTmLocData pETLD = null;
    private ConcentricCircles cCircles = null;

    public EncTmLocData(TimeLocationData tld) {
        this.date = tld.getDate();
        EncryptedPoint ep = tld.getLoc().encrypt();
        this.eCircle = new EncryptedCircle(ep, tld.getAccuracy());
    }

    public EncTmLocData(EncryptedCircle eCircle, Date date) {
        this.eCircle = eCircle;
        this.date = date;
    }

    public EncryptedCircle getCircle() {
        return eCircle;
    }

    public Date getDate() {
        return date;
    }


    public void setcCircles(ConcentricCircles cCircles) {
        this.cCircles = cCircles;
    }

    public void prune() {
        this.cCircles.prune(this);
    }

    public void setNext(EncTmLocData nETLD) {
        this.nETLD = nETLD;
    }

    public void setPrevious(EncTmLocData pETLD) {
        this.pETLD = pETLD;
    }

    public boolean hasNext() {
        return nETLD != null;
    }

    public EncTmLocData next() {
        return nETLD;
    }

    public boolean hasPrevious() {
        return pETLD != null;
    }

    public EncTmLocData previous() {
        return pETLD;
    }
}
