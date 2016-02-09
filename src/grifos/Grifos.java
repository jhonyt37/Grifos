/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grifos;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author jhony
 */
public class Grifos extends Thread {

    JTextArea textLog;
    private volatile boolean isRunning = true;
    ServerSocket SvrSocket;
    private int portNumber;
    Socket clientes[] = new Socket[1000];

    public Grifos(JTextArea txt,int puerto) {
        textLog = txt;
        portNumber = puerto;
        
    }
    /**
     * @param args the command line arguments
     */
    public void kill() {
        try {
            Principal.grifosLogger.escribeLogInfo("Servidor Detenido");
            int i =1;
            while(clientes[i]!= null)
            {
                Principal.grifosLogger.escribeLogTrace("Cliente detenido "+i);
                clientes[i].close();
                i++;
            }
            SvrSocket.close();
            isRunning = false;
        } catch (Exception ex) {
            Principal.grifosLogger.escribeLogFatal("No pudo detener "+ex.getMessage());
        }
    }

    public void run() {
        //public static void startServer(String[] args) {
        // TODO code application logic here
        int Hilos = 1;

        try {
            //Puerto de escucha del servidor
            SvrSocket = new ServerSocket(portNumber);

            //pinto inicio
            Principal.grifosLogger.escribeLogInfo("Servidor Grifos Iniciado");

            while (isRunning) {
                //Escuchamos una peticion
                Socket cliente = clientes[Hilos] = SvrSocket.accept();

                //Una vez escuchado, instanciamos la clase que es un hilo y lo iniciamos
                Servidor ObjCliente = new Servidor(cliente, Hilos);
                ObjCliente.start();
                Hilos++;
            }
        }//del try
        catch (Exception e) {
            Principal.grifosLogger.escribeLogError(e.getMessage());
        }
    }

}
