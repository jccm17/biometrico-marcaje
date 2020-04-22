/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NEGOCIO;

import DATOS.conexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author jccm1
 */
public class N_Marcaje {

    conexionBD con = new conexionBD();
    private int id_emp;
    private String Hora;
    private String Fecha;
    private String Tipo_marcaje;

    public N_Marcaje() {
        this.Fecha = "01/01/2019";
        this.Hora = "00:00:00";
        this.id_emp = 0;
        this.Tipo_marcaje = "Test";
    }

    public N_Marcaje(int id_emp, String Hora, String Fecha, String Tipo_Marcaje) {
        this.id_emp = id_emp;
        this.Hora = Hora;
        this.Fecha = Fecha;
        this.Tipo_marcaje = Tipo_Marcaje;
    }

    public Boolean marcar() {
        try { //Establece los valores para la sentencia SQL
            Connection c = con.Conexion(); //establece la conexion con la BD 
            PreparedStatement guardarStmt = c.prepareStatement("INSERT INTO registro_de_marcaje(fecha, hora_1, id_trabajador, tipo_marcaje) values(?,?,?,?)");
            guardarStmt.setString(1, getFecha());
            guardarStmt.setString(2, getHora());
            guardarStmt.setInt(3, getId_emp());
            guardarStmt.setString(4, getTipo_marcaje());
            guardarStmt.executeUpdate();
            guardarStmt.close();
            System.err.println("Marcaje correcto");
            con.desconectar();
            return true;
        } catch (SQLException ex) {
            //Si ocurre un error lo indica en la consola
            System.out.println("Error al guardar los datos de la huella." + ex);
        } finally {
            con.desconectar();
        }
        return null;
    }

    public Boolean marcar_s() {
        try { //Establece los valores para la sentencia SQL
            Connection c = con.Conexion(); //establece la conexion con la BD 
            PreparedStatement guardarStmt = c.prepareStatement("UPDATE registro_de_marcaje SET hora_2=? where fecha=? AND id_trabajador=?");
            guardarStmt.setString(1, getHora());
            guardarStmt.setString(2, getFecha());
            guardarStmt.setInt(3, getId_emp());
            //guardarStmt.setString(4, getTipo_marcaje());
            guardarStmt.executeUpdate();
            guardarStmt.close();
            System.err.println("Marcaje correcto");
            con.desconectar();
            return true;
        } catch (SQLException ex) {
            //Si ocurre un error lo indica en la consola
            System.out.println("Error al guardar los datos de la huella." + ex);
        } finally {
            con.desconectar();
        }
        return null;
    }

    public Boolean verificar_ingreso(int id_emp, String Fecha) {
        try { //Establece los valores para la sentencia SQL
            Connection c = con.Conexion(); //establece la conexion con la BD 
            PreparedStatement guardarStmt = c.prepareStatement("SELECT hora_1 FROM marcaje where id_emp = ? and fecha = ?");
            guardarStmt.setInt(1, id_emp);
            guardarStmt.setString(2, Fecha);
            ResultSet rs = guardarStmt.executeQuery();
            while (rs.next()) {
                String marcado = rs.getString("hora_1");
                if (marcado.isEmpty()) {
                    return true;
                }
            }
            guardarStmt.close();
            System.out.println("Consulta null");
            con.desconectar();
        } catch (SQLException ex) {
            //Si ocurre un error lo indica en la consola
            System.err.println("Error al guardar los datos de la huella." + ex);
        } finally {
            con.desconectar();
        }
        return null;
    }

    public int getId_emp() {
        return id_emp;
    }

    public void setId_emp(int id_emp) {
        this.id_emp = id_emp;
    }

    public String getHora() {
        return Hora;
    }

    public void setHora(String Hora) {
        this.Hora = Hora;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String Fecha) {
        this.Fecha = Fecha;
    }

    public String getTipo_marcaje() {
        return Tipo_marcaje;
    }

    public void setTipo_marcaje(String Tipo_marcaje) {
        this.Tipo_marcaje = Tipo_marcaje;
    }
}
