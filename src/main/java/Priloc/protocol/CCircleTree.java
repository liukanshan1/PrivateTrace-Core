package Priloc.protocol;

import Priloc.data.EncTmLocData;
import Priloc.data.EncTrajectory;
import Priloc.utils.Constant;
import Priloc.utils.Utils;
import sg.smu.securecom.protocol.Paillier;
import sg.smu.securecom.protocol.PaillierThdDec;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;

public class CCircleTree implements Serializable {
    private HashMap<Date, ConcentricCircles> ccTree;
    private transient ThreadLocal<Map<ConcentricCircles, boolean[]>> localPrune = new ThreadLocal<>();
    private static final List<EncTrajectory> workLoad = new LinkedList<>();

    public CCircleTree() {
        this.ccTree = new HashMap<>();
    }

    public void add(EncTrajectory eTrajectory) {
        List<EncTmLocData> eTLDs = eTrajectory.geteTLDs();
        for (int i = 0; i < eTLDs.size(); i++) {
            EncTmLocData eTLD = eTLDs.get(i);
            Date startDate = eTLD.getDate();
            ConcentricCircles ccs = ccTree.get(startDate);
            if (ccs == null) {
                ccs = new ConcentricCircles(eTLD, this);
                //ccs.setDates(startDate, new Date(startDate.getTime() + INTERVAL * 60 * 1000));
                ccTree.put(startDate, ccs);
            } else {
                ccs.add(eTLD);
            }
        }
    }

    public void init() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(Constant.THREAD);
        for (ConcentricCircles concentricCircles : ccTree.values()) {
            pool.submit(concentricCircles::init);
        }
        pool.shutdown();
        pool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    }

    public void init(boolean isSame) throws InterruptedException {
//        Collection<ConcentricCircles> ccs = ccTree.values();
//        Iterator<ConcentricCircles> it = ccs.iterator();
//        ConcentricCircles c1 = it.next();
//        c1.init();
//        ConcentricCircles cc;
//        while (it.hasNext()) {
//            cc = it.next();
//            cc.setCircleMap(c1.getCircleMap());
//            cc.setMaxRadius(c1.getMaxRadius());
//        }
    }

    private void refresh() {
        for (ConcentricCircles concentricCircles : ccTree.values()) {
            this.localPrune.get().put(concentricCircles, new boolean[concentricCircles.size()]);
        }
    }

    public void prune(ConcentricCircles concentricCircles, int i) {
        this.localPrune.get().get(concentricCircles)[i] = true;
    }

    public boolean getPrune(ConcentricCircles concentricCircles, int i) {
        return this.localPrune.get().get(concentricCircles)[i];
    }

    public synchronized void addWork(EncTrajectory encTrajectory){
        workLoad.add(encTrajectory);
    }

    public synchronized void addWork(EncTrajectory[] encTrajectory){
        workLoad.addAll(Arrays.asList(encTrajectory));
    }

    public synchronized EncTrajectory popWork() {
        if(workLoad.size() == 0) return null;
        EncTrajectory res = workLoad.get(0);
        workLoad.remove(0);
        return res;
    }

    /**
     * false是与阳性有交集
     */
    public boolean compare(EncTrajectory eTrajectory) {
        // 时间裁剪
        List<EncTmLocData> eTLDs = eTrajectory.geteTLDs();
        for (int i = 0; i < eTLDs.size(); i++) {
            EncTmLocData eTLD = eTLDs.get(i);
            Date startDate = eTLD.getDate();
            ConcentricCircles ccs = ccTree.get(startDate);
            if (ccs == null) {
                continue;
            }
            if (!ccs.compare(eTLD.getCircle())) {
                return false;
            }
        }
        return true;
    }

    public void run() {
        if (this.localPrune == null) {
            this.localPrune = new ThreadLocal<>();
        }
        this.localPrune.set(new HashMap<>());
        while (true) {
            EncTrajectory encTrajectory = popWork();
            if (encTrajectory == null) {
                break;
            }
            refresh();
            boolean res = compare(encTrajectory);
            if (!res) {
                System.out.println(encTrajectory);
            }
        }
    }
}
