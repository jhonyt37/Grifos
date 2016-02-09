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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author jhony
 */
public class TransactionPago implements Transaction {
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
        String caja = null, movil = null, datos=null,total=null;
        while (st.hasMoreTokens()) {
            String tmp;
            Principal.grifosLogger.escribeLogTrace(tmp = st.nextToken());
            i++;
            if (i == 3) {
                caja = tmp;
            } else if (i == 5) {
                movil = tmp;
            }
            else if (i == 7) {
                total = tmp;
            }
            else if (i == 9) {
                datos = tmp;
            }

        }

        if (caja != null && movil != null && datos != null && total!=null) {
            ResultSet result = clienteSQL.consumirTransaccion("SELECT pagar_cargera(" + caja + ","+movil+ ","+total+ ",'"+ datos +"')");
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
                        registros += result.getString(1);
                        if(registros.length()<1){
                            msgOut = "99|Pago no realizado";
                            break;
                        }
                        Principal.grifosLogger.escribeLogTrace("responde: "+result.getString(1));
                        
                    }
                    msgOut = "0|Pago exitoso|"+registros;
                } else {
                    msgOut = "99|Pago errado";
                }
            } catch (Exception ex) {
                Logger.getLogger(TransactionLogin.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            msgOut = "99|Error en mensaje de entrada Pago";
        }

        clienteSQL.close();

        return msgOut;
    }

}

