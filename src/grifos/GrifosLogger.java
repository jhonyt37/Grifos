/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grifos;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JTextArea;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author jhony
 */
public class GrifosLogger {

    JTextArea textLog;
    Logger log;
    private final String formatoFecha = "yyyy-MM-dd HH:mm:ss";

    public GrifosLogger(JTextArea txt) {
        textLog = txt;        
        configFile();
    }
    
    public final void configFile (){
        log = Logger.getLogger("");

        String log4jConfPath = System.getProperty("user.dir") + "/config/log.properties";

        if (new File(log4jConfPath).exists()) {

            try {
                PropertyConfigurator.configure(log4jConfPath);
            } catch (Exception ex) {
                escribeLogErrorInterno(ex.getMessage());
            }
        } else {
            escribeLogErrorInterno("error obteniendo archivo config");
        }

        BasicConfigurator.configure();
    }

    public void escribeLogInfo(String mensaje) {
        String timeStamp = new SimpleDateFormat(formatoFecha).format(Calendar.getInstance().getTime());
        System.out.println(timeStamp + ": "+ mensaje);
        textLog.append(timeStamp + ": "+ mensaje + "\n");
        log.debug(mensaje);
    }
    
    public void escribeLogTrace(String mensaje) {
        String timeStamp = new SimpleDateFormat(formatoFecha).format(Calendar.getInstance().getTime());
        System.out.println(timeStamp + ": "+ mensaje);
        configFile();
        log.trace(mensaje);
    }
    
    public void escribeLogError(String mensaje) {
        String timeStamp = new SimpleDateFormat(formatoFecha).format(Calendar.getInstance().getTime());
        System.out.println(timeStamp + ": "+ mensaje);
        textLog.append(timeStamp + ": "+ mensaje + "\n");
        configFile();
        log.error(mensaje);
    }
    
    public void escribeLogErrorInterno(String mensaje) {
        String timeStamp = new SimpleDateFormat(formatoFecha).format(Calendar.getInstance().getTime());
        System.out.println(timeStamp + ": "+ mensaje);
        textLog.append(timeStamp + ": "+ mensaje + "\n");
        log.error(mensaje);
    }
    
    public void escribeLogFatal(String mensaje) {
        String timeStamp = new SimpleDateFormat(formatoFecha).format(Calendar.getInstance().getTime());
        System.out.println(mensaje);
        textLog.append(timeStamp + ": "+ mensaje + "\n");
        configFile();
        log.fatal(mensaje);
    }

}
