package Hilos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Interfaz gráfica para aplicar operaciones morfológicas (Erosión/Dilatación) sobre imágenes PNG.
 */
public class InterfazGrafica extends JFrame {

    private JTextField txtRutaImagen;
    private JComboBox<String> comboOperacion;
    private JComboBox<String> comboMetodo;
    private JComboBox<String> comboEstructurante;
    private JButton btnSeleccionarImagen, btnEjecutar;
    private JLabel lblPreview, lblResultado;

    private String rutaImagenSeleccionada = null;
    private BufferedImage imagenCargada = null;

    public InterfazGrafica() {
        super("Procesamiento Morfológico (Erosión / Dilatación) - PNG");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // === PANEL SUPERIOR ===
        JPanel panelSuperior = new JPanel(new GridLayout(5, 2, 10, 10));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtRutaImagen = new JTextField();
        txtRutaImagen.setEditable(false);
        btnSeleccionarImagen = new JButton("Seleccionar Imagen...");
        btnSeleccionarImagen.addActionListener(this::seleccionarImagen);

        comboOperacion = new JComboBox<>(new String[]{"Erosión", "Dilatación"});
        comboMetodo = new JComboBox<>(new String[]{"Secuencial", "Paralelo"});
        comboEstructurante = new JComboBox<>(new String[]{
                "1 - L invertida hacia abajo",
                "2 - L invertida hacia arriba",
                "3 - Línea horizontal",
                "4 - Línea vertical",
                "5 - Diagonal (X)",
                "6 - Cruz clásica (+)"
        });

        btnEjecutar = new JButton("Ejecutar Operación");
        btnEjecutar.addActionListener(this::ejecutarOperacion);

        panelSuperior.add(new JLabel("Imagen PNG:"));
        panelSuperior.add(txtRutaImagen);
        panelSuperior.add(new JLabel(""));
        panelSuperior.add(btnSeleccionarImagen);
        panelSuperior.add(new JLabel("Operación:"));
        panelSuperior.add(comboOperacion);
        panelSuperior.add(new JLabel("Método:"));
        panelSuperior.add(comboMetodo);
        panelSuperior.add(new JLabel("Elemento Estructurante:"));
        panelSuperior.add(comboEstructurante);

        // === PANEL CENTRAL ===
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 10, 10));
        lblPreview = new JLabel("Vista previa original", SwingConstants.CENTER);
        lblResultado = new JLabel("Resultado", SwingConstants.CENTER);
        panelCentral.add(lblPreview);
        panelCentral.add(lblResultado);

        // === PANEL INFERIOR ===
        JPanel panelInferior = new JPanel();
        panelInferior.add(btnEjecutar);

        add(panelSuperior, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }

    /** Seleccionar imagen PNG **/
    private void seleccionarImagen(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccione una imagen PNG");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG files", "png"));
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                rutaImagenSeleccionada = file.getAbsolutePath();
                txtRutaImagen.setText(rutaImagenSeleccionada);
                imagenCargada = ImageIO.read(file);

                if (imagenCargada == null) throw new Exception("Archivo no válido o no es PNG.");

                ImageIcon icon = new ImageIcon(imagenCargada.getScaledInstance(350, 350, Image.SCALE_SMOOTH));
                lblPreview.setIcon(icon);
                lblPreview.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al cargar la imagen: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Ejecutar operación **/
    private void ejecutarOperacion(ActionEvent e) {
        if (rutaImagenSeleccionada == null || imagenCargada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una imagen PNG primero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int operacion = comboOperacion.getSelectedIndex() + 1; // 1=Erosión, 2=Dilatación
        int metodo = comboMetodo.getSelectedIndex() + 1;       // 1=Secuencial, 2=Paralelo
        int estructurante = comboEstructurante.getSelectedIndex() + 1;

        try {
            long inicio = System.currentTimeMillis();

            BufferedImage resultado;
            if (operacion == 1)
                resultado = main.ejecutarErosionPNG(imagenCargada, metodo, estructurante);
            else
                resultado = main.ejecutarDilatacionPNG(imagenCargada, metodo, estructurante);

            long fin = System.currentTimeMillis();
            double segundos = (fin - inicio) / 1000.0;

            // Mostrar resultado
            ImageIcon icon = new ImageIcon(resultado.getScaledInstance(350, 350, Image.SCALE_SMOOTH));
            lblResultado.setIcon(icon);
            lblResultado.setText("");

            JOptionPane.showMessageDialog(this,
                    "Operación completada en " + segundos + " segundos.\nResultado guardado en la carpeta del proyecto.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error durante la operación: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InterfazGrafica().setVisible(true));
    }
}
