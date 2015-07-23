package de.kritzelbit.orebit.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CustomUncaughtExceptionHandler implements UncaughtExceptionHandler {

    public void uncaughtException(Thread t, Throwable e) {
        FileWriter fw;
        PrintWriter pw;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date(System.currentTimeMillis());
        File f = new File("crash-report-" + sdf.format(date) + ".txt");
        System.out.println("[GAME]\twriting crash report to: " + f.getAbsolutePath());
        try {
            fw = new FileWriter(f, true);
            pw = new PrintWriter(fw);
            e.printStackTrace(pw);
            pw.close();
            fw.close();
        } catch (IOException ex) {
        }
    }
    
}