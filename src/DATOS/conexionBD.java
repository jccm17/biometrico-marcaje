/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DATOS;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Wilfredo Ayanome
 */
public class conexionBD {

    Connection cn;
    Statement st;

    public Connection Conexion() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            cn = DriverManager.getConnection("jdbc:mysql://localhost/biometrico2018", "root", "root");
            System.out.print("Conexion establecida...");
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return cn;
    }

    public void desconectar() {
        cn = null;
        System.out.println("Desconexion a base de datos listo...");
    }

}
