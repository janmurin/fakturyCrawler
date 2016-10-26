package utils;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import utils.SingleLineFormatter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author janmu
 */
public class LoggerThread implements Runnable {
    
    private final BlockingQueue<String> logQueue;
    static Logger logger = Logger.getLogger("FileDownloader");
    static FileHandler fh;
    
    public LoggerThread(BlockingQueue<String> log, String logname) {
        this.logQueue = log;
        try {
            fh = new FileHandler(logname, true);
        } catch (IOException ex) {
            Logger.getLogger(LoggerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(LoggerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        logger.addHandler(fh);
        //SimpleFormatter formatter = new SimpleFormatter();
        SingleLineFormatter formatter=new SingleLineFormatter();
        fh.setFormatter(formatter);
    }
    
    @Override
    public void run() {
        try {
            String message = logQueue.take();
            while (message != null) {
                if (message.equals("poison.pill")) {
                    break;
                }
                logger.log(Level.INFO, message);
                message = logQueue.take();
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            
        }
    }
    
}
