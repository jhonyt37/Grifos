/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grifos;

import Transactions.FabricaTransacciones;
import Transactions.Transaction;
import java.net.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author jhony
 */
public class Servidor extends Thread {

    private Socket cliente;
    private int NumHilo;

    //Constructor de la clase
    Servidor(Socket PCliente, int Philo) {
        //Asigno objeto socket
        this.cliente = PCliente;
        this.NumHilo = Philo; //Asigno el numero de hilo paras diferenciar mensajes
    }

    //Ejecucion del Hilo
    public void run() {
        double milisegundosDesde;
        double milisegundosHasta;
        String TxtFromClient = "";
        String WebHtmlR = "";
        String linea = "";
        String UrlFozadaUser = "";

        try {
            Principal.grifosLogger.escribeLogTrace("Start Cliente: " + this.NumHilo);
            Principal.grifosLogger.escribeLogTrace("IP Cliente conectado:" + this.cliente.getInetAddress().getHostAddress());

            //Milisegundos actuales
            milisegundosDesde = System.nanoTime();

            // De entrada del cliente al servidor
            Scanner Cliente_Entrada = new Scanner(this.cliente.getInputStream());
            //PrintWriter Cliente_Salida = new PrintWriter(this.cliente.getOutputStream(), true);
            DataOutputStream outToClient = new DataOutputStream(this.cliente.getOutputStream());

            //mensaje recibido
            TxtFromClient = Cliente_Entrada.next();
            //fin de trama
            if (TxtFromClient.charAt(TxtFromClient.length() - 1) != '}') {
                TxtFromClient += Cliente_Entrada.next();
            }
            
            Principal.grifosLogger.escribeLogTrace("mensaje de entrada de "+this.NumHilo+": "+TxtFromClient);

            if (TxtFromClient.length() > 0) {

                int pos = TxtFromClient.indexOf("|");

                Transaction txn = FabricaTransacciones.estableceTxn(TxtFromClient.substring(0,pos));
                String dataOut = null;
                if (txn != null) {
                    TxtFromClient = TxtFromClient.substring(0, TxtFromClient.length()-1); 
                    dataOut = txn.execute(TxtFromClient);
                    if (dataOut != null) {

                        if (dataOut.length() >= 2048) {

                            dataOut = "99|Error en longitud de respuesta " + dataOut.length();
                        }

                    } else {
                        dataOut = "99|Error en Consumo de Transacción " + TxtFromClient.substring(pos);
                    }
                } else {
                    dataOut = "99|Error de Transacción " + TxtFromClient.substring(pos);
                }

                dataOut += '}';

                milisegundosHasta = System.nanoTime();
                outToClient.writeBytes(dataOut);
                Principal.grifosLogger.escribeLogTrace("mensaje de salida para "+this.NumHilo+": "+dataOut);
                Principal.grifosLogger.escribeLogTrace("Cliente termino:" + ((milisegundosHasta - milisegundosDesde) / 1000000) + "ms");

            }
            //Cierro el cliente
            cliente.close();
            Principal.grifosLogger.escribeLogTrace("End Cliente: " + this.NumHilo);
            
        } //del while
        catch (Exception e) {
            Principal.grifosLogger.escribeLogError("Error: " + e.getMessage());
        }
    }

}
