/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Transactions;

import grifos.Principal;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author jhony
 */
public class FabricaTransacciones {
    
    
    public static Transaction estableceTxn(String codTxn){
        
        Transaction txn=null;
        String txnName = readTransactions(codTxn);
        if(txnName!=null)
        {
            if(txnName.equals("login"))
                txn = new TransactionLogin();
            if(txnName.equals("consulta"))
                txn = new TransactionConsulta();
            if(txnName.equals("pago"))
                txn = new TransactionPago();
            
        }
        
        return txn;
    }
    
    private static String readTransactions(String code) {
        String res = null;

        try {
            String configFile = System.getProperty("user.dir") + "/config/";

            if (new File(configFile).exists()) {
                File file = new File(configFile);
                URL[] urls = new URL[]{file.toURI().toURL()};
                ClassLoader loader = new URLClassLoader(urls);
                
                ResourceBundle rb = ResourceBundle.getBundle("transactions", Locale.getDefault(), loader);

                String propertyValue = rb.getString(code);

                if (rb != null) {
                    //if (propertyValue.equals("true")) {
                    res = propertyValue;
                    //}
                }
            }
            else{
                Principal.grifosLogger.escribeLogError("error de config SQL transactions");
            }
        } catch (Exception ex) {
            //Funcion utilizada para escribir en el servidor en pantalla
            Principal.grifosLogger.escribeLogError("error parametros de config SQL" + ex.getMessage());

        }

        return res;
    }
    
}
