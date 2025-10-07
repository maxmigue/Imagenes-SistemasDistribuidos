package Hilos;


public class Erosion extends Thread {

    private static final int PIXEL_MAX = 255;

    private final int[][] matriz;
    private final int[][] result;
    private final int startX;
    private final int startY;
    private final int endX;
    private final int endY;
    private final int opcion;

    public Erosion(
            int[][] matriz,
            int[][] result,
            int startX,
            int startY,
            int endX,
            int endY,
            int opcion
    ) {
        if (matriz == null || result == null) {
            throw new IllegalArgumentException("matriz y result no pueden ser null");
        }
        if (matriz.length == 0 || matriz[0].length == 0) {
            throw new IllegalArgumentException("matriz no puede ser vacía");
        }
        if (result.length != matriz.length || result[0].length != matriz[0].length) {
            throw new IllegalArgumentException("result debe tener mismas dimensiones que matriz");
        }
        if (startX < 0 || startY < 0 || endX > matriz[0].length || endY > matriz.length || startX > endX || startY > endY) {
            throw new IllegalArgumentException("Rango inválido: (" + startX + "," + startY + ")..(" + endX + "," + endY + ")");
        }

        this.matriz = matriz;
        this.result = result;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.opcion = opcion;
    }

    @Override
    public void run() {
        switch (opcion) {
            case 1:
                procesarVecindad(this::minOpcion1);
                break;
            case 2:
                procesarVecindad(this::minOpcion2);
                break;
            case 3:
                procesarVecindad(this::minOpcion3);
                break;
            case 4:
                procesarVecindad(this::minOpcion4);
                break;
            case 5:
                procesarVecindad(this::minOpcion5);
                break;
            default:
                procesarVecindad(this::minOpcion6);
                break;
        }
    }

    /* ===================== Núcleo genérico ===================== */

    private void procesarVecindad(PixelMinCalculator calc) {
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                result[y][x] = calc.compute(y, x);
            }
        }
    }

    /* ===================== Helpers de límites ===================== */

    /**
     * Devuelve matriz[y][x] si está dentro de límites; si no, 0.
     * Esto replica el comportamiento original: vecinos no asignados (out-of-bounds)
     * equivalen a 0 y empujan el mínimo hacia 0 en los bordes.
     */
    private int safeGetZero(int y, int x) {
        if (y < 0 || y >= matriz.length) return 0;
        if (x < 0 || x >= matriz[0].length) return 0;
        return matriz[y][x];
    }

    /* ===================== Estrategias por opción ===================== */

    // opcion == 1: izquierda, centro, abajo
    private int minOpcion1(int y, int x) {
        int a = safeGetZero(y, x - 1);
        int b = safeGetZero(y, x);
        int c = safeGetZero(y + 1, x);
        return min3(a, b, c);
    }

    // opcion == 2: izquierda, centro, arriba
    private int minOpcion2(int y, int x) {
        int a = safeGetZero(y, x - 1);
        int b = safeGetZero(y, x);
        int c = safeGetZero(y - 1, x);
        return min3(a, b, c);
    }

    // opcion == 3: izquierda, centro, derecha
    private int minOpcion3(int y, int x) {
        int a = safeGetZero(y, x - 1);
        int b = safeGetZero(y, x);
        int c = safeGetZero(y, x + 1);
        return min3(a, b, c);
    }

    // opcion == 4: centro, abajo
    private int minOpcion4(int y, int x) {
        int a = safeGetZero(y, x);
        int b = safeGetZero(y + 1, x);
        return Math.min(a, b);
    }

    // opcion == 5: diagonales + centro
    private int minOpcion5(int y, int x) {
        int a = safeGetZero(y - 1, x - 1); // sup-izq
        int b = safeGetZero(y - 1, x + 1); // sup-der
        int c = safeGetZero(y, x);         // centro
        int d = safeGetZero(y + 1, x + 1); // inf-der
        int e = safeGetZero(y + 1, x - 1); // inf-izq
        return min5(a, b, c, d, e);
    }

    // opcion por defecto (6): cruz (izq, arr, centro, der, aba)
    private int minOpcion6(int y, int x) {
        int a = safeGetZero(y, x - 1);
        int b = safeGetZero(y - 1, x);
        int c = safeGetZero(y, x);
        int d = safeGetZero(y, x + 1);
        int e = safeGetZero(y + 1, x);
        return min5(a, b, c, d, e);
    }

    /* ===================== Utilidades de mínimo ===================== */

    private static int min3(int a, int b, int c) {
        int min = PIXEL_MAX;
        if (a < min) min = a;
        if (b < min) min = b;
        if (c < min) min = c;
        return min;
    }

    private static int min5(int a, int b, int c, int d, int e) {
        int min = PIXEL_MAX;
        if (a < min) min = a;
        if (b < min) min = b;
        if (c < min) min = c;
        if (d < min) min = d;
        if (e < min) min = e;
        return min;
    }

    /* ===================== Funcional ===================== */

    @FunctionalInterface
    private interface PixelMinCalculator {
        int compute(int y, int x);
    }
}
