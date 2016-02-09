/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grifos;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author jhony
 */
public class MySQLCliente {

    Connection conexion;

    public boolean conectar() {
        boolean res = false;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            // Establecemos la conexión con la base de datos. 
            String host = readProperties("host");
            
            String database = readProperties("database");
            String user = readProperties("user");
            String password = readProperties("password");
            Principal.grifosLogger.escribeLogTrace("host: "+host+" DB: "+database+" User: "+user+" password: "+password);
            DriverManager.setLoginTimeout(Integer.parseInt(readProperties("timeout")));
            
            conexion = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, user, password);
            if (conexion != null) {
                res = true;
            }

        } catch (Exception e) {
            Principal.grifosLogger.escribeLogError("Error de conexion SQL");
            Principal.grifosLogger.escribeLogError(e.getMessage());
        }
        return res;
    }

    public ResultSet consumirTransaccion(String query) {

        try {
            // Preparamos la consulta
            Statement s = conexion.createStatement();
            ResultSet rsRes = null;
            Principal.grifosLogger.escribeLogTrace("query: " + query);
            ResultSet rs = rsRes = s.executeQuery(query);
            rs.last();
            int numRows = rs.getRow();
            rs.beforeFirst(); // esto te lo deja como al principio 
            Principal.grifosLogger.escribeLogTrace("filas " + numRows);
            while (rs.next()) {
                Principal.grifosLogger.escribeLogTrace(rs.getString(1) + " ");
            }

            return rsRes;
        } catch (Exception ex) {
            Principal.grifosLogger.escribeLogError(ex.getMessage());
        }
        return null;

    }

    public void close() {

        try {
            // Cerramos la conexion a la base de datos.
            conexion.close();
        } catch (Exception ex) {
            Principal.grifosLogger.escribeLogError(ex.getMessage());
        }

    }

    private static String readProperties(String key) {
        String res = null;

        try {
            String configFile = System.getProperty("user.dir") + "/config/";

            if (new File(configFile).exists()) {
                File file = new File(configFile);
                URL[] urls = new URL[]{file.toURI().toURL()};
                ClassLoader loader = new URLClassLoader(urls);
                
                ResourceBundle rb = ResourceBundle.getBundle("general", Locale.getDefault(), loader);

                String propertyValue = rb.getString(key);

                if (rb != null) {
                    //if (propertyValue.equals("true")) {
                    res = propertyValue;
                    //}
                }
            }
            else{
                Principal.grifosLogger.escribeLogError("error de config SQL");
            }
        } catch (Exception ex) {
            //Funcion utilizada para escribir en el servidor en pantalla
            Principal.grifosLogger.escribeLogError("error parametros de config SQL" + ex.getMessage());

        }

        return res;
    }

}
