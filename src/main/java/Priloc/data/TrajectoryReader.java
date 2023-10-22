package Priloc.data;

import Priloc.area.basic.Circle;
import Priloc.area.basic.Point;
import Priloc.geo.Location;
import Priloc.utils.Constant;
import Priloc.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TrajectoryReader {
    private String path;
    private File pltFile;
    private Scanner scn;

    public TrajectoryReader(String path) throws FileNotFoundException {
        this.path = path;
        pltFile = new File(path);
        scn = new Scanner(pltFile);
        for (int i = 0; i < 6; i++) {
            scn.nextLine();
        }
    }

    public TrajectoryReader(File file) throws FileNotFoundException {
        pltFile = file;
        scn = new Scanner(pltFile);
        for (int i = 0; i < 6; i++) {
            scn.nextLine();
        }
    }

    public File getPltFile() {
        return pltFile;
    }

    private boolean hasNext() {
        return scn.hasNext();
    }

    private TimeLocationData next() throws ParseException {
        String[] tokens = scn.next().split(",");
        double lat = Double.parseDouble(tokens[0]);
        double lon = Double.parseDouble(tokens[1]);
        // 未引入高度
        double altitude = Double.parseDouble(tokens[3]); // Altitude in feet
        String time = tokens[5] + ' ' + tokens[6];
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = dateFormat.parse(time);
        if (Constant.IGNORE_DATE) {
            date.setYear(2008);
            date.setMonth(Calendar.JUNE);
            date.setDate(24);
        }
        return new TimeLocationData(new Location(lat, lon), date);
    }

    public Trajectory load() throws ParseException {
        List<TimeLocationData> tlds = new ArrayList<>();
        TimeLocationData pTLD = null;
        Date previousDate = null;
        double rad = 0;
        while (this.hasNext()) {
            TimeLocationData cTLD = this.next();
            Date currentDate = Utils.getStart(cTLD.getDate());
            if (currentDate.equals(previousDate)) {
                // distance
                rad = Math.max(rad, cTLD.getLoc().distance(pTLD.getLoc()));
                if (rad < Constant.RADIUS * 1.5) {
                    continue;
                }
            }
            if (rad > Constant.RADIUS * 2) {
                // TODO 补充rad/2r个园
                TimeLocationData mTLD = new TimeLocationData(new Location(
                        (cTLD.getLoc().getLatitude() + pTLD.getLoc().getLatitude()) / 2.0,
                        (cTLD.getLoc().getLongitude() + pTLD.getLoc().getLongitude()) / 2.0
                ), currentDate);
                // 连接双向链表
                mTLD.setPrevious(pTLD);
                mTLD.setNext(cTLD);
                // 将节点加入到轨迹中
                tlds.add(mTLD);
                // 设置当前节点为Previous
                pTLD = mTLD;
            }
            rad = 0;
            // 设置时间为标准间隔
            cTLD.setDate(currentDate);
            // 连接双向链表
            cTLD.setPrevious(pTLD);
            if (pTLD != null) {
                pTLD.setNext(cTLD);
            }
            // 将节点加入到轨迹中
            tlds.add(cTLD);
            // 设置当前节点为Previous
            pTLD = cTLD;
            previousDate = currentDate;
        }
        return new Trajectory(tlds, pltFile.getName());
    }

    public static boolean check(Trajectory t) {
        Circle pc = null;
        for (TimeLocationData tld : t.getTLDs()) {
            Circle cc = tld.getCircle();
            if (pc != null) {
                if (Point.distance(cc.getCenter(), pc.getCenter()) > Constant.RADIUS * 3) {
                    // System.out.println(Point.distance(cc.getCenter(), pc.getCenter()) + "" + tld.getDate());
                    return false;
                }
            }
            pc = cc;
        }
        return true;
    }

    public static void main(String[] args) {
        try {
            TrajectoryReader pltReader = new TrajectoryReader("./GeoLife Trajectories 1.3/data by person/000/Trajectory/20081023025304.plt");
            while (pltReader.hasNext()) {
                System.out.println(pltReader.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
