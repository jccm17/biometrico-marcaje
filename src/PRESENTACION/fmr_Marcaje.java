/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PRESENTACION;

import DATOS.conexionBD;
import NEGOCIO.N_Marcaje;
import NEGOCIO.formatfech_hora;
import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPErrorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPErrorEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.capture.event.DPFPSensorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPSensorEvent;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 *
 * @author jccm1
 */
public class fmr_Marcaje extends javax.swing.JFrame {

    private static final long serialVersionUID = 1L;
    N_Marcaje marcar = new N_Marcaje();
    formatfech_hora ft = new formatfech_hora();
    conexionBD con = new conexionBD();
    private javax.swing.Timer timer1;
    private Clock _clock;                        // Our clock component.

    /**
     * Creates new form Marcaje
     */
    public fmr_Marcaje() {

        this.setIconImage(new ImageIcon(getClass().getResource("/imag/BIO.png")).getImage());
        // setLocationRelativeTo(null);
        _clock = new Clock();
        initComponents();
        initOthersComponets();
        lb_check.setVisible(false);
        //this.setSize(750, 600);
        D_marcaje_manual.setSize(504, 313);
    }

    class Clock extends JComponent {

        //============================================================ constants
        private static final double TWO_PI = 2.0 * Math.PI;

        private static final int UPDATE_INTERVAL = 100;  // Millisecs
        private static final long serialVersionUID = 1L;

        //=============================================================== fields
        private Calendar _now = Calendar.getInstance();  // Current time.

        private int _diameter;                 // Height and width of clock face
        private int _centerX;                  // x coord of middle of clock
        private int _centerY;                  // y coord of middle of clock
        private BufferedImage _clockImage;     // Saved image of the clock face.

        private javax.swing.Timer _timer;      // Fires to update clock.

        //==================================================== Clock constructor
        public Clock() {
            setPreferredSize(new Dimension(240, 240));

            _timer = new javax.swing.Timer(UPDATE_INTERVAL, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateTime();
                    repaint();
                }
            });
        }

        //================================================================ start
        /**
         * Start the timer.
         */
        public void start() {
            _timer.start();
        }

        //================================================================= stop
        /**
         * Stop the timer.
         */
        public void stop() {
            _timer.stop();
        }

        //=========================================================== updateTime
        private void updateTime() {
            //... Avoid creating new objects.
            _now.setTimeInMillis(System.currentTimeMillis());
        }

        //======================================================= paintComponent
        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            //... The panel may have been resized, get current dimensions
            int w = getWidth();
            int h = getHeight();
            _diameter = ((w < h) ? w : h) - 1;
            _centerX = _diameter / 2;
            _centerY = _diameter / 2;

            //... Create the clock face background image if this is the first time,
            //    or if the size of the panel has changed
            if (_clockImage == null
                    || _clockImage.getWidth() != w
                    || _clockImage.getHeight() != h) {
                _clockImage = (BufferedImage) (this.createImage(w, h));

                //... Get a graphics context from this image
                Graphics2D g2a = _clockImage.createGraphics();
                g2a.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                drawClockFace(g2a);
            }

            //... Draw the clock face from the precomputed image
            g2.drawImage(_clockImage, null, 0, 0);

            //... Draw the clock hands dynamically each time.
            drawClockHands(g2);
        }

        //====================================== convenience method drawClockHands
        private void drawClockHands(Graphics2D g2) {
            //... Get the various time elements from the Calendar object.
            int hours = _now.get(Calendar.HOUR);
            int minutes = _now.get(Calendar.MINUTE);
            int seconds = _now.get(Calendar.SECOND);
            int millis = _now.get(Calendar.MILLISECOND);

            //... second hand
            int handMin = _diameter / 8;    // Second hand doesn't start in middle.
            int handMax = _diameter / 2;    // Second hand extends to outer rim.
            double fseconds = (seconds + (double) millis / 1000) / 60.0;
            drawhandseconds(g2, fseconds, 0, handMax);

            //... minute hand
            handMin = 0;                    // Minute hand starts in middle.
            handMax = _diameter / 3;
            double fminutes = (minutes + fseconds) / 60.0;
            drawhandminutes(g2, fminutes, 0, handMax);

            //... hour hand
            handMin = 0;
            handMax = _diameter / 4;
            drawhandhours(g2, (hours + fminutes) / 12.0, 0, handMax);
        }

        //======================================= convenience method drawClockFace
        private void drawClockFace(Graphics2D g2) {
            int hora = 1;
            String hr[] = {"0", "12", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
            // ... Draw the clock face.  Probably into a buffer.        
            g2.setPaint(new GradientPaint(5, 30, Color.YELLOW, 35, 100, Color.GREEN, true));
            g2.fillOval(0, 0, _diameter, _diameter);
            g2.setColor(Color.BLACK);
            g2.drawOval(0, 0, _diameter, _diameter);

            int radius = _diameter / 2;
            double radiohr = _diameter / 2.2;

            //... Draw the tick marks around the circumference.
            for (int sec = 0; sec < 60; sec++) {
                int tickStart;
                if (sec % 5 == 0) {
                    tickStart = (int) radiohr - 5;  // Draw long tick mark every 5.
                    //for(int hora = 1; hora < 13; hora ++){
                    drawNumbers(hr[hora], g2, sec / 60.0, tickStart);
                    hora++;
                    //}
                } else {
                    tickStart = radius - 5;   // Short tick mark.
                }
                drawRadius(g2, sec / 60.0, tickStart, radius);
            }
        }

        //==================================== convenience method drawRadius
        // This draw lines along a radius from the clock face center.
        // By changing the parameters, it can be used to draw tick marks,
        // as well as the hands.
        private void drawRadius(Graphics2D g2, double percent, int minRadius, int maxRadius) {
            //... percent parameter is the fraction (0.0 - 1.0) of the way
            //    clockwise from 12.   Because the Graphics2D methods use radians
            //    counterclockwise from 3, a little conversion is necessary.
            //    It took a little experimentation to get this right.
            double radians = (0.5 - percent) * TWO_PI;
            double sine = Math.sin(radians);
            double cosine = Math.cos(radians);

            int dxmin = _centerX + (int) (minRadius * sine);
            int dymin = _centerY + (int) (minRadius * cosine);

            int dxmax = _centerX + (int) (maxRadius * sine);
            int dymax = _centerY + (int) (maxRadius * cosine);
            g2.drawLine(dxmin, dymin, dxmax, dymax);
        }

        private void drawhandseconds(Graphics2D g2, double percent, int minRadius, int maxRadius) {
            //... percent parameter is the fraction (0.0 - 1.0) of the way
            //    clockwise from 12.   Because the Graphics2D methods use radians
            //    counterclockwise from 3, a little conversion is necessary.
            //    It took a little experimentation to get this right.
            double radians = (0.5 - percent) * TWO_PI;
            double sine = Math.sin(radians);
            double cosine = Math.cos(radians);

            int dxmin = _centerX + (int) (minRadius * sine);
            int dymin = _centerY + (int) (minRadius * cosine);

            int dxmax = _centerX + (int) (maxRadius * sine);
            int dymax = _centerY + (int) (maxRadius * cosine);
            g2.setColor(Color.red);
            g2.drawLine(dxmin, dymin, dxmax, dymax);
        }

        private void drawhandminutes(Graphics2D g2, double percent, int minRadius, int maxRadius) {
            //... percent parameter is the fraction (0.0 - 1.0) of the way
            //    clockwise from 12.   Because the Graphics2D methods use radians
            //    counterclockwise from 3, a little conversion is necessary.
            //    It took a little experimentation to get this right.
            double radians = (0.5 - percent) * TWO_PI;
            double sine = Math.sin(radians);
            double cosine = Math.cos(radians);

            int dxmin = _centerX + (int) (minRadius * sine);
            int dymin = _centerY + (int) (minRadius * cosine);

            int dxmax = _centerX + (int) (maxRadius * sine);
            int dymax = _centerY + (int) (maxRadius * cosine);
            g2.setColor(Color.BLUE);
            g2.drawLine(dxmin, dymin, dxmax, dymax);
        }

        private void drawhandhours(Graphics2D g2, double percent, int minRadius, int maxRadius) {
            //... percent parameter is the fraction (0.0 - 1.0) of the way
            //    clockwise from 12.   Because the Graphics2D methods use radians
            //    counterclockwise from 3, a little conversion is necessary.
            //    It took a little experimentation to get this right.
            double radians = (0.5 - percent) * TWO_PI;
            double sine = Math.sin(radians);
            double cosine = Math.cos(radians);

            int dxmin = _centerX + (int) (minRadius * sine);
            int dymin = _centerY + (int) (minRadius * cosine);

            int dxmax = _centerX + (int) (maxRadius * sine);
            int dymax = _centerY + (int) (maxRadius * cosine);
            g2.setColor(Color.BLACK);
            g2.drawLine(dxmin, dymin, dxmax, dymax);
        }

        private void drawNumbers(String hr, Graphics2D g2, double percent, int minRadius) {
            //... percent parameter is the fraction (0.0 - 1.0) of the way
            //    clockwise from 12.   Because the Graphics2D methods use radians
            //    counterclockwise from 3, a little conversion is necessary.
            //    It took a little experimentation to get this right.
            double radians = (0.5 - percent) * TWO_PI;
            double sine = Math.sin(radians);
            double cosine = Math.cos(radians);

            int dxmin = _centerX + (int) ((minRadius * sine) - 6);
            int dymin = _centerY + (int) ((minRadius * cosine) + 4);
            g2.setFont(new Font("Monospaced", Font.BOLD, 18));
            g2.drawString(hr, dxmin, dymin);
        }
    }

    public void initOthersComponets() {
        java.awt.event.ActionListener al
                = new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onTimer();
            }
        };
        //Crear un temporizador e iniciarlo
        timer1 = new javax.swing.Timer(1000, al);
        timer1.start();
        _clock.start();
    }

    private void onTimer() {
        java.util.Date hora = new java.util.Date();
        String patron = "hh:mm:ss";
        java.text.SimpleDateFormat formato = new java.text.SimpleDateFormat(patron);
        jlbHoraActual.setText(formato.format(hora));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        D_marcaje_manual = new javax.swing.JDialog();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txt_cod_ci = new elaprendiz.gui.textField.TextFieldRoundIcon();
        btn_marcar = new elaprendiz.gui.button.ButtonAction();
        txt_name_user = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txt_usuario = new javax.swing.JLabel();
        clockDigital1 = new elaprendiz.gui.varios.ClockDigital();
        dcFecha = new com.toedter.calendar.JDateChooser();
        lblImagenHuella = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lb_check = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtArea = new javax.swing.JTextArea();
        jlbHoraActual = new javax.swing.JLabel();
        btnMarcajeManual = new elaprendiz.gui.button.ButtonRect();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPrelojanalogico = new javax.swing.JPanel();

        D_marcaje_manual.setTitle("FORMULARIO DE MARCAJE MANUAL TELEDATA S.A");
        D_marcaje_manual.setBackground(new java.awt.Color(255, 255, 255));
        D_marcaje_manual.setResizable(false);
        D_marcaje_manual.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Garamond", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 204, 51));
        jLabel6.setText("Marcaje Manual");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(171, 12, -1, -1));

        txt_cod_ci.setBackground(new java.awt.Color(153, 153, 255));
        txt_cod_ci.setForeground(new java.awt.Color(0, 0, 0));
        txt_cod_ci.setToolTipText("Numero de Identifiicacion");
        txt_cod_ci.setFont(new java.awt.Font("Leelawadee UI Semilight", 1, 18)); // NOI18N
        jPanel2.add(txt_cod_ci, new org.netbeans.lib.awtextra.AbsoluteConstraints(171, 122, 176, 38));

        btn_marcar.setText("MARCAR");
        btn_marcar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_marcarActionPerformed(evt);
            }
        });
        jPanel2.add(btn_marcar, new org.netbeans.lib.awtextra.AbsoluteConstraints(194, 198, -1, -1));

        txt_name_user.setBackground(new java.awt.Color(51, 51, 255));
        txt_name_user.setFont(new java.awt.Font("DigifaceWide", 1, 18)); // NOI18N
        txt_name_user.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txt_name_user.setText("Usuario");
        jPanel2.add(txt_name_user, new org.netbeans.lib.awtextra.AbsoluteConstraints(115, 73, 273, -1));

        D_marcaje_manual.getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 504, 313));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CONTROL DE ASISTENCIA TELEDATA S.A");
        setBackground(new java.awt.Color(255, 255, 255));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 153, 255));
        jLabel1.setText("Fecha:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 153, 255));
        jLabel4.setText("Usuario:");

        txt_usuario.setText("usuario");

        clockDigital1.setForeground(new java.awt.Color(255, 255, 255));
        clockDigital1.setPreferredSize(new java.awt.Dimension(120, 45));

        dcFecha.setDate(Calendar.getInstance().getTime());
        dcFecha.setEnabled(false);
        dcFecha.setPreferredSize(new java.awt.Dimension(120, 20));

        lblImagenHuella.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(0, 0, 255), null, null));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 153, 255));
        jLabel2.setText("Huella");

        lb_check.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_check.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lb_check.setFocusCycleRoot(true);

        txtArea.setColumns(20);
        txtArea.setRows(5);
        txtArea.setOpaque(false);
        jScrollPane1.setViewportView(txtArea);

        jlbHoraActual.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jlbHoraActual.setForeground(new java.awt.Color(0, 0, 204));

        btnMarcajeManual.setBackground(new java.awt.Color(51, 153, 255));
        btnMarcajeManual.setText("Marcaje Manual");
        btnMarcajeManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarcajeManualActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Gill Sans Ultra Bold Condensed", 0, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 102, 255));
        jLabel5.setText("CONTROL DE ASISTENCIA");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 153, 255));
        jLabel3.setText("Hora:");

        jPrelojanalogico.setBackground(new java.awt.Color(255, 255, 255));
        jPrelojanalogico.add(_clock);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(clockDigital1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(134, 134, 134)
                        .addComponent(btnMarcajeManual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlbHoraActual, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPrelojanalogico, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(79, 79, 79))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(lblImagenHuella, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(275, 275, 275)
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(225, 225, 225)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 441, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(lb_check, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblImagenHuella, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(44, 44, 44))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3)
                                    .addComponent(jlbHoraActual, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dcFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(txt_usuario)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(clockDigital1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(14, 14, 14))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPrelojanalogico, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                                .addComponent(btnMarcajeManual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lb_check, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMarcajeManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarcajeManualActionPerformed
        D_marcaje_manual.setVisible(true);
    }//GEN-LAST:event_btnMarcajeManualActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        Iniciar();
        start();
        EstadoHuellas();
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        stop();
    }//GEN-LAST:event_formWindowClosing

    private void btn_marcarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_marcarActionPerformed
        identificarCodigo();
        D_marcaje_manual.dispose();
        txt_cod_ci.setText(null);
    }//GEN-LAST:event_btn_marcarActionPerformed
    //Varible que permite iniciar el dispositivo de lector de huella conectado
// con sus distintos metodos.
    private DPFPCapture Lector = DPFPGlobal.getCaptureFactory().createCapture();

//Varible que permite establecer las capturas de la huellas, para determina sus caracteristicas
// y poder estimar la creacion de un template de la huella para luego poder guardarla
    private DPFPEnrollment Reclutador = DPFPGlobal.getEnrollmentFactory().createEnrollment();

//Esta variable tambien captura una huella del lector y crea sus caracteristcas para auntetificarla
// o verificarla con alguna guardada en la BD
    private DPFPVerification Verificador = DPFPGlobal.getVerificationFactory().createVerification();
    private DPFPVerification Verificador_2 = DPFPGlobal.getVerificationFactory().createVerification();
//Variable que para crear el template de la huella luego de que se hallan creado las caracteriticas
// necesarias de la huella si no ha ocurrido ningun problema
    private DPFPTemplate template;
    private DPFPTemplate template_2;
    public static String TEMPLATE_PROPERTY = "template";

    protected void Iniciar() {
        Lector.addDataListener(new DPFPDataAdapter() {
            @Override
            public void dataAcquired(final DPFPDataEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        EnviarTexto("La Huella Digital ha sido Capturada");
                        ProcesarCaptura(e.getSample());
                        txt_usuario.setText("");
                        try {
                            identificarHuella();
                            Reclutador.clear();
                        } catch (IOException ex) {
                            Logger.getLogger(fmr_Marcaje.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            }
        });

        Lector.addReaderStatusListener(new DPFPReaderStatusAdapter() {
            @Override
            public void readerConnected(final DPFPReaderStatusEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        EnviarTexto("El Sensor de Huella Digital esta Activado o Conectado");
                    }
                });
            }

            @Override
            public void readerDisconnected(final DPFPReaderStatusEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        EnviarTexto("El Sensor de Huella Digital esta Desactivado o no Conectado");
                    }
                });
            }
        });

        Lector.addSensorListener(new DPFPSensorAdapter() {
            @Override
            public void fingerTouched(final DPFPSensorEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        EnviarTexto("El dedo ha sido colocado sobre el Lector de Huella");

                    }
                });
            }

            @Override
            public void fingerGone(final DPFPSensorEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        EnviarTexto("El dedo ha sido quitado del Lector de Huella");
                        lb_check.setVisible(false);
                        txt_usuario.setText("Identificar");
                        lblImagenHuella.setIcon(null);
                    }
                });
            }
        });

        Lector.addErrorListener(new DPFPErrorAdapter() {
            public void errorReader(final DPFPErrorEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        EnviarTexto("Error: " + e.getError());
                    }
                });
            }
        });
    }
    public DPFPFeatureSet featuresinscripcion;
    public DPFPFeatureSet featuresverificacion;
    public DPFPFeatureSet featuresverificacion_2;

    public void ProcesarCaptura(DPFPSample sample) {
        // Procesar la muestra de la huella y crear un conjunto de características con el propósito de inscripción.
        featuresinscripcion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

        // Procesar la muestra de la huella y crear un conjunto de características con el propósito de verificacion.
        featuresverificacion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);

        // Comprobar la calidad de la muestra de la huella y lo añade a su reclutador si es bueno
        if (featuresinscripcion != null) {
            try {
                System.out.println("Las Caracteristicas de la Huella han sido creada");
                Reclutador.addFeatures(featuresinscripcion);// Agregar las caracteristicas de la huella a la plantilla a crear

                // Dibuja la huella dactilar capturada.
                Image image = CrearImagenHuella(sample);
                DibujarHuella(image);

            } catch (DPFPImageQualityException ex) {
                System.err.println("Error: " + ex.getMessage());
            } finally {
                EstadoHuellas();
                // Comprueba si la plantilla se ha creado.
                switch (Reclutador.getTemplateStatus()) {
                    case TEMPLATE_STATUS_READY:	// informe de éxito y detiene  la captura de huellas
                        stop();
                        setTemplate(Reclutador.getTemplate());

                        EnviarTexto("La Plantilla de la Huella ha Sido Creada, ya puede Verificarla o Identificarla");
                        break;

                    case TEMPLATE_STATUS_FAILED: // informe de fallas y reiniciar la captura de huellas
                        Reclutador.clear();
                        stop();
                        EstadoHuellas();
                        setTemplate(null);
//                        JOptionPane.showMessageDialog(CapturaHuella.this, "La Plantilla de la Huella no pudo ser creada, Repita el Proceso", "Inscripcion de Huellas Dactilares", JOptionPane.ERROR_MESSAGE);
                        start();
                        break;
                }
            }
        }
    }

    public DPFPFeatureSet extraerCaracteristicas(DPFPSample sample, DPFPDataPurpose purpose) {
        DPFPFeatureExtraction extractor = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
        try {
            return extractor.createFeatureSet(sample, purpose);
        } catch (DPFPImageQualityException e) {
            return null;
        }
    }

    public Image CrearImagenHuella(DPFPSample sample) {
        return DPFPGlobal.getSampleConversionFactory().createImage(sample);
    }

    public void DibujarHuella(Image image) {
        lblImagenHuella.setIcon(new ImageIcon(
                image.getScaledInstance(lblImagenHuella.getWidth(), lblImagenHuella.getHeight(), Image.SCALE_DEFAULT)));
        repaint();
    }

    public void EstadoHuellas() {
        // EnviarTexto("Muestra de Huellas Necesarias para Guardar Template " + Reclutador.getFeaturesNeeded());
    }

    public void EnviarTexto(String string) {
        txtArea.append(string + "\n");
    }

    public void start() {
        Lector.startCapture();
        EnviarTexto("Utilizando el Lector de Huella Dactilar ");
    }

    public void stop() {
        Lector.stopCapture();
        EnviarTexto("No se está usando el Lector de Huella Dactilar ");
    }

    public DPFPTemplate getTemplate() {
        return template;
    }

    public void setTemplate(DPFPTemplate template) {
        DPFPTemplate old = this.template;
        this.template = template;
        firePropertyChange(TEMPLATE_PROPERTY, old, template);
    }

    public DPFPTemplate getTemplate_2() {
        return template_2;
    }

    public void setTemplate_2(DPFPTemplate template_2) {
        DPFPTemplate old = this.template_2;
        this.template_2 = template_2;
        firePropertyChange(TEMPLATE_PROPERTY, old, template_2);
    }

    public void identificarHuella() throws IOException {
        try {
            //Establece los valores para la sentencia SQL
            Connection c = con.Conexion();
            //Obtiene todas las huellas de la bd
            PreparedStatement identificarStmt = c.prepareStatement("SELECT s.id_trabajador, s.huella_1, s.huella_2, t.nombre_completo FROM huella s JOIN registro_trabajador t on s.id_trabajador = t.id_trabajador");
            ResultSet rs = identificarStmt.executeQuery();
            //Si se encuentra el nombre en la base de datos
            while (rs.next()) {
                //Lee la plantilla de la base de datos
                byte templateBuffer[] = rs.getBytes("huella_1");
                String nombre = rs.getString("t.nombre_completo");
                int cod_emp = rs.getInt("s.id_trabajador");
                //int turno = rs.getInt("s.id_turno");
                //Crea una nueva plantilla a partir de la guardada en la base de datos
                DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
                //Envia la plantilla creada al objeto contendor de Template del componente de huella digital
                setTemplate(referenceTemplate);
                // Compara las caracteriticas de la huella recientemente capturda con la
                // alguna plantilla guardada en la base de datos que coincide con ese tipo
                DPFPVerificationResult result_1 = Verificador.verify(featuresverificacion, getTemplate());
                //compara las plantilas (actual vs bd)
                //Si encuentra correspondencia dibuja el mapa
                //e indica el nombre de la persona que coincidió.
                if (result_1.isVerified()) {
                    //crea la imagen de los datos guardado de las huellas guardadas en la base de datos
                    String hora = ft.obtenerhora();
                    String fecha = ft.obtenerfecha();
                    System.out.println(" hora : " + hora + " fecha: " + fecha);
                    //Date time = ft.conver_time();
                    //Date compare = ft.compare();
                    marcar.setId_emp(cod_emp);
                    marcar.setHora(hora);
                    marcar.setFecha(fecha);
                    marcar.setTipo_marcaje("Automatico");
                    Boolean check = marcar.marcar();
                    System.out.println("marcar en la mañana");
                    if (check == true) {
                        txt_usuario.setText(nombre);
                        lb_check.setVisible(true);
                        lb_check.setIcon(new ImageIcon(getClass().getResource("/util/success.png")));
                        repaccesocorrecto();
                        return;
                    }
                }
            }

            txt_usuario.setText("Usuario No Identificado");
            lb_check.setVisible(true);
            lb_check.setIcon(new ImageIcon(getClass().getResource("/util/error.png")));
            repnocoincide();
            setTemplate(null);
        } catch (SQLException e) {
            //Si ocurre un error lo indica en la consola
            System.err.println("Error al identificar huella dactilar." + e.getMessage());
            nosepuedeconectarbd();
        } finally {
            con.desconectar();
        }
    }

    public void identificarCodigo() {
        try {//Establece los valores para la sentencia SQL
            Connection c = con.Conexion();
            //Obtiene todas las huellas de la bd
            PreparedStatement identificarStmt = c.prepareStatement("SELECT t.id_trabajador, t.c_i, t.nombre_completo, h.hora_entrada, h.hora_descanso, h.hora_retorno_de_descanso, h.hora_salida FROM registro_trabajador t JOIN horario h ON t.nombre_de_turno = h.id_horario");
            ResultSet rs = identificarStmt.executeQuery();
            //Si se encuentra el nombre en la base de datos
            while (rs.next()) {
                //Lee la plantilla de la base de datos
                String nombre = rs.getString("t.nombre_completo");
                int cod_emp = rs.getInt("t.id_trabajador");
                int cod_ci = rs.getInt("t.c_i");
                Date horaE = ft.compare_entrada(rs.getString("h.hora_entrada"));
                Date horaSD = ft.compare_salida_desc(rs.getString("h.hora_descanso"));
                Date horaET = ft.compare_entrada_desc(rs.getString("h.hora_retorno_de_descanso"));
                Date horaS = ft.compare_salida(rs.getString("h.hora_salida"));
                //Crea una nueva plantilla a partir de la guardada en la base de datos  
                if (cod_ci == Integer.parseInt(txt_cod_ci.getText())) {
                    //crea la imagen de los datos guardado de las huellas guardadas en la base de datos
                    String hora = ft.obtenerhora();
                    String fecha = ft.obtenerfecha();
                    if (ft.conver_time().before(horaE) || ft.conver_time().after(horaE) && ft.conver_time().before(horaSD)) {
                        marcar.setId_emp(cod_emp);
                        marcar.setHora(hora);
                        marcar.setFecha(fecha);
                        marcar.setTipo_marcaje("Manual");
                        Boolean check = marcar.marcar();
                        System.out.println("marcaje Manual");
                        if (check == true) {
                            txt_usuario.setText(nombre);
                            lb_check.setVisible(true);
                            lb_check.setIcon(new ImageIcon(getClass().getResource("/util/success.png")));
                            repaccesocorrecto();
                            return;
                        }
                    }
                    if (ft.conver_time().after(horaET) || ft.conver_time().before(horaS) && ft.conver_time().after(horaSD)) {
                        marcar.setId_emp(cod_emp);
                        marcar.setHora(hora);
                        marcar.setFecha(fecha);
                        marcar.setTipo_marcaje("Manual");
                        Boolean check = marcar.marcar_s();
                        System.out.println("marcaje Manual");
                        if (check == true) {
                            txt_usuario.setText(nombre);
                            lb_check.setVisible(true);
                            lb_check.setIcon(new ImageIcon(getClass().getResource("/util/success.png")));
                            repaccesocorrecto();
                            return;
                        }
                    }
                }
            }

            txt_usuario.setText("Usuario No Identificado");
            lb_check.setVisible(true);
            lb_check.setIcon(new ImageIcon(getClass().getResource("/util/error.png")));
            repnocoincide();
        } catch (SQLException e) {
            //Si ocurre un error lo indica en la consola
            System.err.println("Error al identificar huella dactilar." + e.getMessage());
            nosepuedeconectarbd();
        } finally {
            con.desconectar();
        }
    }

    public void repaccesocorrecto() {
        try {
            InputStream in = getClass().getResourceAsStream("/util/accesocorrecto.wav");
            AudioStream audio = new AudioStream(in);
            AudioPlayer.player.start(audio);
        } catch (IOException e) {
            Toolkit.getDefaultToolkit().beep();
            //write error to log
            // writeLog(e.getMessage());
        }
    }

    public void repnocoincide() {
        try {
            InputStream in = getClass().getResourceAsStream("/util/nocoincide.wav");
            AudioStream audio = new AudioStream(in);
            AudioPlayer.player.start(audio);
        } catch (IOException e) {
            Toolkit.getDefaultToolkit().beep();
            //write error to log
            // writeLog(e.getMessage());
        }
    }

    public void nosepuedeconectarbd() {
        try {
            InputStream in = getClass().getResourceAsStream("/audioschecador/nocpdeconectar.wav");
            AudioStream audio = new AudioStream(in);
            AudioPlayer.player.start(audio);
        } catch (IOException e) {
            Toolkit.getDefaultToolkit().beep();
            //write error to log
            //writeLog(e.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(fmr_Marcaje.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(fmr_Marcaje.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(fmr_Marcaje.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(fmr_Marcaje.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new fmr_Marcaje().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog D_marcaje_manual;
    private elaprendiz.gui.button.ButtonRect btnMarcajeManual;
    private elaprendiz.gui.button.ButtonAction btn_marcar;
    private elaprendiz.gui.varios.ClockDigital clockDigital1;
    private com.toedter.calendar.JDateChooser dcFecha;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPrelojanalogico;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jlbHoraActual;
    private javax.swing.JLabel lb_check;
    private javax.swing.JLabel lblImagenHuella;
    private javax.swing.JTextArea txtArea;
    private elaprendiz.gui.textField.TextFieldRoundIcon txt_cod_ci;
    private javax.swing.JLabel txt_name_user;
    private javax.swing.JLabel txt_usuario;
    // End of variables declaration//GEN-END:variables
}
