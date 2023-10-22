package Priloc.protocol;

import Priloc.data.Trajectory;
import Priloc.data.TrajectoryReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Tmain {
    public static void main(String[] args) throws Exception {
        for ( int j= 100; j <= 181 ; j++) {
            String s = "./GeoLife Trajectories 1.3/Data/"+j+"/Trajectory/";
            File dir = new File(s);
            String[] positivePath = dir.list();
            TrajectoryReader[] positiveReaders = new TrajectoryReader[positivePath.length];
            for (int i = 0; i < positivePath.length; i++) {
                positiveReaders[i] = new TrajectoryReader(s + positivePath[i]);
            }
            Trajectory[] positiveTrajectories = new Trajectory[positivePath.length];
            for (int i = 0; i < positivePath.length; i++) {
                positiveTrajectories[i] = positiveReaders[i].load();
            }
            for (int i = 0; i < positivePath.length; i++) {
                if (!TrajectoryReader.check(positiveTrajectories[i])) {
                    System.out.println('"' + s + positiveTrajectories[i].getName() + '"'+ ",");
                    //toTxT('"' + s + positiveTrajectories[i].getName() + '"'+ ",");
                }
            }
        }
    }

    public static void toTxT(String nr) {
        FileWriter fw = null;
        try {
            //如果文件存在，则追加内容；如果文件不存在，则创建文件
            File f = new File("./dd.txt");
            fw = new FileWriter(f, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println(nr);
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main2() throws IOException {
        String[] positivePath = new String[]{

        };
        for (int i = 0; i < positivePath.length; i++) {
            Files.deleteIfExists(Paths.get(positivePath[i]));
        }
    }
}