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
import javax.swing.JTextArea;

/**
 *
 * @author jhony
 */
public class TransactionLogin implements Transaction {
    JTextArea textLog;

    @Override
    public String execute(String msgIn) {
        String msgOut = null;
        MySQLCliente clienteSQL = new MySQLCliente();

        if (false == clienteSQL.conectar()) {
            msgOut = "99|Error de conexion con el Sistema";
            return msgOut;

        }

        StringTokenizer st = new StringTokenizer(msgIn, "|", true);
        Principal.grifosLogger.escribeLogTrace("Hay un total de: " + st.countTokens() + " tokens.");

        int i = 0;
        String user = null, pwd = null;
        while (st.hasMoreTokens()) {
            String tmp;
            Principal.grifosLogger.escribeLogTrace(tmp = st.nextToken());
            i++;
            if (i == 3) {
                user = tmp;
            } else if (i == 5) {
                pwd = tmp;
            }

        }

        if (user != null && pwd != null) {
            ResultSet result = clienteSQL.consumirTransaccion("select * from vst_login where usuario='" + user + "' and clave='" + pwd + "'");
            try {
                result.last();
                int numRows = result.getRow();
                result.beforeFirst(); // esto te lo deja como al principio 
                System.out.print("filas " + numRows);
                if (numRows > 0) {
                    String registros = "";
                    int nBox = 0;
                    while (result.next()) {
                        nBox++;
                        if (nBox > 1) {
                            registros += "¶";
                        }
                        registros += result.getString(1) + "@" + result.getString(4);
                        Principal.grifosLogger.escribeLogTrace(result.getString(1) + " " + result.getString(2) + " ");
                    }
                    msgOut = "0|Ingreso exitoso|"+registros;
                } else {
                    msgOut = "99|Usuario no existe";
                }
            } catch (Exception ex) {
                Logger.getLogger(TransactionLogin.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            msgOut = "99|Error en mensaje de entrada Login";
        }

        clienteSQL.close();

        return msgOut;
    }

}
