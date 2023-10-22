package Priloc.protocol;

import Priloc.data.Trajectory;
import Priloc.data.TrajectoryReader;
import Priloc.utils.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Tmain2 {
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
            Utils.writeObject(positiveTrajectories[0], "./xx");
            Object o = Utils.readObject("./xx");
            System.out.println(o);
        }
    }
}