/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NEGOCIO;

import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author jccm1
 */
public class formatfech_hora {

    public String obtenerfecha() {
        //Obtener unicamente la fecha del sistema
        DateFormat yearp = new SimpleDateFormat("yyyy/MM/dd");
        String fechasalida = yearp.format(new Date());
        return fechasalida;
    }

    public String obtenerhora() {
        //Obtener unicamente la hora del sistema
        DateFormat df2p = new SimpleDateFormat("HH:mm:ss");
        String horasalida = df2p.format(new Date());
        return horasalida;
    }

    public Date conver_time() {
        String hora = obtenerhora();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(hora);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //String format = sdf.format(date);
        // System.out.println("hora:  --" + format);
        return date;
    }

    public Date compare_entrada(String hora) {
        // String hora = "12:00:00"; // opcional para definir valor personalizado desde bd
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = sdf.parse(hora);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String format = sdf.format(date);
        System.out.println("hora:  --" + format);
        return date;
    }

    public Date compare_salida_desc(String hora) {
        //String hora = "08:30:00"; // opcional para definir valor personalizado desde bd
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = sdf.parse(hora);
            return date;
        } catch (ParseException e) {
        }
        return date;
    }

    public Date compare_entrada_desc(String hora) {
        //String hora = "08:30:00"; // opcional para definir valor personalizado desde bd
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = sdf.parse(hora);
            return date;
        } catch (ParseException e) {
        }
        return date;
    }

    public Date compare_salida(String hora) {
        //String hora = "08:30:00"; // opcional para definir valor personalizado desde bd
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = sdf.parse(hora);
            return date;
        } catch (ParseException e) {
        }
        return date;
    }
}
