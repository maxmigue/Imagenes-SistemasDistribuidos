package Hilos;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class main {

    // =====================  GUI =====================

    public static BufferedImage ejecutarErosionPNG(BufferedImage imagen, int modo, int estructurante) throws IOException {
        return procesarImagen(imagen, "resultado_erosion.png", /*esErosion*/ true, modo, estructurante);
    }

    public static BufferedImage ejecutarDilatacionPNG(BufferedImage imagen, int modo, int estructurante) throws IOException {
        return procesarImagen(imagen, "resultado_dilatacion.png", /*esErosion*/ false, modo, estructurante);
    }

    // =====================  general =====================

    private static BufferedImage procesarImagen(BufferedImage img,
                                                String nombreSalida,
                                                boolean esErosion,
                                                int modo,
                                                int estructurante) throws IOException {
        BufferedImage fuente = asegurarRGB(img); // evitamos sorpresas si viene ARGB/Indexed
        int ancho = fuente.getWidth();
        int alto  = fuente.getHeight();

        // salida con mismas dimensiones y tipo
        BufferedImage salida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        final int numThreads = (modo == 1) ? 1 : Math.max(1, Runtime.getRuntime().availableProcessors());
        long inicio = System.currentTimeMillis();

        aplicarOperacionRGB(fuente, salida, esErosion, numThreads /*, estructurante*/);

        long fin = System.currentTimeMillis();

        ImageIO.write(salida, "png", new File(nombreSalida));
        System.out.println("Operación " + (esErosion ? "Erosión" : "Dilatación")
                + " completada en " + ((fin - inicio) / 1000.0) + " s");

        return salida;
    }

    // ===================== Núcleo de procesamiento =====================

    /**
     * Aplica la operación morfológica canal por canal (R,G,B) con vecindario 3x3.
     * Se particiona por bandas horizontales equilibradas.
     */
    private static void aplicarOperacionRGB(BufferedImage src,
                                            BufferedImage dst,
                                            boolean esErosion,
                                            int numThreads) {
        final int width = src.getWidth();
        final int height = src.getHeight();

        if (numThreads <= 1 || height < numThreads) {
            // Camino secuencial
            procesarRangoFilas(src, dst, esErosion, 0, height, width, height);
            return;
        }

        ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        List<Callable<Void>> tareas = new ArrayList<>(numThreads);

        int base = height / numThreads;
        int resto = height % numThreads;
        int startY = 0;

        for (int i = 0; i < numThreads; i++) {
            int filas = base + (i < resto ? 1 : 0);
            int endY = startY + filas;

            final int sY = startY;
            final int eY = endY;
            tareas.add(() -> {
                procesarRangoFilas(src, dst, esErosion, sY, eY, width, height);
                return null;
            });

            startY = endY;
        }

        try {
            List<Future<Void>> futures = pool.invokeAll(tareas);
            for (Future<Void> f : futures) {
                // Propagar cualquier excepción de las tareas
                f.get();
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Procesamiento interrumpido", ie);
        } catch (ExecutionException ee) {
            throw new RuntimeException("Error en tarea de procesamiento", ee.getCause());
        } finally {
            pool.shutdownNow();
        }
    }

    /**
     * Procesa una banda horizontal [startY, endY) de la imagen.
     * Aplica mínimo (erosión) o máximo (dilatación) para cada canal RGB en ventana 3x3.
     */
    private static void procesarRangoFilas(BufferedImage src,
                                           BufferedImage dst,
                                           boolean esErosion,
                                           int startY,
                                           int endY,
                                           int width,
                                           int height) {

        for (int y = startY; y < endY; y++) {
            for (int x = 0; x < width; x++) {

                int rAgregado = esErosion ? 255 : 0;
                int gAgregado = esErosion ? 255 : 0;
                int bAgregado = esErosion ? 255 : 0;

                // Vecindario 3x3: Se recorren los píxeles vecinos en un área de 3x3
                for (int dy = -1; dy <= 1; dy++) {
                    int ny = y + dy; // Calcula la posición vertical del vecino
                    if (ny < 0 || ny >= height) continue; // Verifica que el vecino esté dentro de los límites verticales

                    for (int dx = -1; dx <= 1; dx++) {
                        int nx = x + dx; // Calcula la posición horizontal del vecino
                        if (nx < 0 || nx >= width) continue; // Verifica que el vecino esté dentro de los límites horizontales

                        // Obtiene el valor RGB del píxel vecino
                        int rgb = src.getRGB(nx, ny);
                        int r = (rgb >> 16) & 0xFF; // Extrae el componente rojo
                        int g = (rgb >> 8) & 0xFF;  // Extrae el componente verde
                        int b = (rgb) & 0xFF;       // Extrae el componente azul

                        // Aplica la operación morfológica (erosión o dilatación)
                        if (esErosion) {
                            // En erosión, se toma el mínimo valor de cada canal
                            if (r < rAgregado) rAgregado = r;
                            if (g < gAgregado) gAgregado = g;
                            if (b < bAgregado) bAgregado = b;
                        } else {
                            // En dilatación, se toma el máximo valor de cada canal
                            if (r > rAgregado) rAgregado = r;
                            if (g > gAgregado) gAgregado = g;
                            if (b > bAgregado) bAgregado = b;
                        }
                    }
                }

                int nuevo = (rAgregado << 16) | (gAgregado << 8) | bAgregado;
                dst.setRGB(x, y, nuevo);
            }
        }
    }

    // ===================== Utilidades =====================

    /**
     * Si la imagen no es TYPE_INT_RGB, crea una copia en ese formato.
     * (Evita costes por conversión implícita en getRGB/setRGB y asegura consistencia).
     */
    private static BufferedImage asegurarRGB(BufferedImage img) {
        if (img.getType() == BufferedImage.TYPE_INT_RGB) return img;

        BufferedImage copia = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        // Copia por bloques para mantener el mismo resultado que getRGB/setRGB
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                copia.setRGB(x, y, img.getRGB(x, y));
            }
        }
        return copia;
    }
}
