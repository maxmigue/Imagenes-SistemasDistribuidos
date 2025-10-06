package Hilos;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Clase adaptada para trabajar con imágenes PNG RGB.
 * Aplica erosión y dilatación con soporte secuencial y paralelo.
 */
public class lecturaimagen {

    // === Métodos públicos usados por la GUI ===

    public static BufferedImage ejecutarErosionPNG(BufferedImage imagen, int modo, int estructurante) throws IOException {
        return procesarImagen(imagen, "resultado_erosion.png", true, modo, estructurante);
    }

    public static BufferedImage ejecutarDilatacionPNG(BufferedImage imagen, int modo, int estructurante) throws IOException {
        return procesarImagen(imagen, "resultado_dilatacion.png", false, modo, estructurante);
    }

    // === Método general ===

    private static BufferedImage procesarImagen(BufferedImage img, String salida, boolean esErosion, int modo, int estructurante)
            throws IOException {

        int ancho = img.getWidth();
        int alto = img.getHeight();
        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        int numThreads = (modo == 1) ? 1 : Runtime.getRuntime().availableProcessors();

        long inicio = System.currentTimeMillis();
        if (esErosion)
            resultado = erosionConHilos(img, numThreads, estructurante);
        else
            resultado = dilatacionConHilos(img, numThreads, estructurante);
        long fin = System.currentTimeMillis();

        ImageIO.write(resultado, "png", new File(salida));
        System.out.println("Operación " + (esErosion ? "Erosión" : "Dilatación") +
                " completada en " + ((fin - inicio) / 1000.0) + " s");
        return resultado;
    }

    // === Erosión ===
    public static BufferedImage erosionConHilos(BufferedImage img, int numThreads, int estructurante) {
        BufferedImage salida = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            int startY = i * (img.getHeight() / numThreads);
            int endY = (i == numThreads - 1) ? img.getHeight() : (i + 1) * (img.getHeight() / numThreads);

            threads[i] = new Thread(() -> {
                for (int y = startY; y < endY; y++) {
                    for (int x = 0; x < img.getWidth(); x++) {
                        int minR = 255, minG = 255, minB = 255;
                        for (int dy = -1; dy <= 1; dy++) {
                            for (int dx = -1; dx <= 1; dx++) {
                                int nx = x + dx, ny = y + dy;
                                if (nx >= 0 && ny >= 0 && nx < img.getWidth() && ny < img.getHeight()) {
                                    int rgb = img.getRGB(nx, ny);
                                    int r = (rgb >> 16) & 0xff;
                                    int g = (rgb >> 8) & 0xff;
                                    int b = rgb & 0xff;
                                    if (r < minR) minR = r;
                                    if (g < minG) minG = g;
                                    if (b < minB) minB = b;
                                }
                            }
                        }
                        int nuevo = (minR << 16) | (minG << 8) | minB;
                        salida.setRGB(x, y, nuevo);
                    }
                }
            });
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) try { t.join(); } catch (InterruptedException ignored) {}
        return salida;
    }

    // === Dilatación ===
    public static BufferedImage dilatacionConHilos(BufferedImage img, int numThreads, int estructurante) {
        BufferedImage salida = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            int startY = i * (img.getHeight() / numThreads);
            int endY = (i == numThreads - 1) ? img.getHeight() : (i + 1) * (img.getHeight() / numThreads);

            threads[i] = new Thread(() -> {
                for (int y = startY; y < endY; y++) {
                    for (int x = 0; x < img.getWidth(); x++) {
                        int maxR = 0, maxG = 0, maxB = 0;
                        for (int dy = -1; dy <= 1; dy++) {
                            for (int dx = -1; dx <= 1; dx++) {
                                int nx = x + dx, ny = y + dy;
                                if (nx >= 0 && ny >= 0 && nx < img.getWidth() && ny < img.getHeight()) {
                                    int rgb = img.getRGB(nx, ny);
                                    int r = (rgb >> 16) & 0xff;
                                    int g = (rgb >> 8) & 0xff;
                                    int b = rgb & 0xff;
                                    if (r > maxR) maxR = r;
                                    if (g > maxG) maxG = g;
                                    if (b > maxB) maxB = b;
                                }
                            }
                        }
                        int nuevo = (maxR << 16) | (maxG << 8) | maxB;
                        salida.setRGB(x, y, nuevo);
                    }
                }
            });
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) try { t.join(); } catch (InterruptedException ignored) {}
        return salida;
    }
}
