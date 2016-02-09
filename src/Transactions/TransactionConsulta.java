/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Transactions;

import grifos.MySQLCliente;
import grifos.Principal;
import java.util.StringTokenizer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jhony
 */
public class TransactionConsulta implements Transaction {

    @Override
    public String execute(String msgIn) {
        String msgOut = null;
        MySQLCliente clienteSQL = new MySQLCliente();

        if (false == clienteSQL.conectar()) {
            msgOut = "99|Error de conexion con el Sistema";
            return msgOut;

        }
        
        //obtener user y password de entrada
        StringTokenizer st = new StringTokenizer(msgIn, "|", true);
        Principal.grifosLogger.escribeLogTrace("Hay un total de: " + st.countTokens() + " tokens.");

        int i = 0;
        String movil = null, caja = null;
        while (st.hasMoreTokens()) {
            String tmp;
            Principal.grifosLogger.escribeLogTrace(tmp = st.nextToken());
            i++;
            if (i == 3) {
                movil = tmp;
            } else if (i == 5) {
                caja = tmp;
            }

        }

        if (movil != null && caja != null) {
           
            ResultSet result = clienteSQL.consumirTransaccion("select Cartera_Consulta("+movil+","+caja+")");
            try {
                result.last();
                int numRows = result.getRow();
                result.beforeFirst(); // esto te lo deja como al principio 
                Principal.grifosLogger.escribeLogTrace("filas " + numRows);
                if (numRows > 0) {
                    String registros = "";
                    int nBox = 0;
                    while (result.next()) {
                        nBox++;
                        if (nBox > 1) {
                            registros += "¶";
                        }
                        //registros += result.getString(1);
                        String str = result.getString(1);
                        str = str.replace(".", "");
                        
                        registros += str.substring(0, str.length()-1); 
                        Principal.grifosLogger.escribeLogTrace("respondio: "+registros + " " +  " ");
                    }
                    msgOut = "0|consulta exitosa|"+registros;
                } else {
                    msgOut = "99|consulta sin datos";
                }
            } catch (Exception ex) {
                Logger.getLogger(TransactionLogin.class.getName()).log(Level.SEVERE, null, ex);
                msgOut = "99|consulta sin datos";
            }
        } else {
            msgOut = "99|Error en mensaje de entrada Consulta";
        }

        clienteSQL.close();

        return msgOut;
    }

}
