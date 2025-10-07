package Hilos;


public class Dilatacion extends Thread {

    private final int[][] matriz;
    private final int[][] result;
    private final int startX;
    private final int startY;
    private final int endX;
    private final int endY;
    private final int opcion;

    public Dilatacion(
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
                procesarVecindad(this::maxOpcion1);
                break;
            case 2:
                procesarVecindad(this::maxOpcion2);
                break;
            case 3:
                procesarVecindad(this::maxOpcion3);
                break;
            case 4:
                procesarVecindad(this::maxOpcion4);
                break;
            case 5:
                procesarVecindad(this::maxOpcion5);
                break;
            default:
                procesarVecindad(this::maxOpcion6);
                break;
        }
    }

    /* ===================== Núcleo genérico ===================== */

    /**
     * Recorre el subrango y aplica la función de máximo por pixel.
     */
    private void procesarVecindad(PixelMaxCalculator calc) {
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                result[y][x] = calc.compute(y, x);
            }
        }
    }

    /* ===================== Helpers de límites ===================== */

    /**
     * Devuelve matriz[y][x] si está dentro de límites; de lo contrario 0.
     * Esto replica el comportamiento original donde, si un vecino no se asignaba,
     * permanecía en 0 y no afectaba el máximo.
     */
    private int safeGet(int y, int x) {
        if (y < 0 || y >= matriz.length) return 0;
        if (x < 0 || x >= matriz[0].length) return 0;
        return matriz[y][x];
    }

    /* ===================== Estrategias por opción ===================== */

    // opcion == 1: izquierda, centro, abajo
    private int maxOpcion1(int y, int x) {
        int a = safeGet(y, x - 1);
        int b = safeGet(y, x);
        int c = safeGet(y + 1, x);
        return max3(a, b, c);
    }

    // opcion == 2: izquierda, centro, arriba
    private int maxOpcion2(int y, int x) {
        int a = safeGet(y, x - 1);
        int b = safeGet(y, x);
        int c = safeGet(y - 1, x);
        return max3(a, b, c);
    }

    // opcion == 3: izquierda, centro, derecha
    private int maxOpcion3(int y, int x) {
        int a = safeGet(y, x - 1);
        int b = safeGet(y, x);
        int c = safeGet(y, x + 1);
        return max3(a, b, c);
    }

    // opcion == 4: centro, abajo
    private int maxOpcion4(int y, int x) {
        int a = safeGet(y, x);
        int b = safeGet(y + 1, x);
        return Math.max(a, b);
    }

    // opcion == 5: diagonales + centro (manejo seguro de bordes)
    private int maxOpcion5(int y, int x) {
        int a = safeGet(y - 1, x - 1); // sup-izq
        int b = safeGet(y - 1, x + 1); // sup-der
        int c = safeGet(y, x);         // centro
        int d = safeGet(y + 1, x + 1); // inf-der
        int e = safeGet(y + 1, x - 1); // inf-izq
        return max5(a, b, c, d, e);
    }

    // opcion por defecto (6): cruz (izq, arr, centro, der, aba)
    private int maxOpcion6(int y, int x) {
        int a = safeGet(y, x - 1);
        int b = safeGet(y - 1, x);
        int c = safeGet(y, x);
        int d = safeGet(y, x + 1);
        int e = safeGet(y + 1, x);
        return max5(a, b, c, d, e);
    }

    /* ===================== Utilidades de máximo ===================== */

    private static int max3(int a, int b, int c) {
        return Math.max(a, Math.max(b, c));
    }

    private static int max5(int a, int b, int c, int d, int e) {
        return Math.max(Math.max(a, b), Math.max(Math.max(c, d), e));
    }

    /* ===================== Funcional ===================== */

    @FunctionalInterface
    private interface PixelMaxCalculator {
        int compute(int y, int x);
    }
}
