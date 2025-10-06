package Hilos;

public class Erosion_hilos  extends Thread{
    private int[][] matriz;
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    private final int[][] result;
    private int opcion;

    public Erosion_hilos(int[][] matriz, int[][]result, int startX, int startY, int endX, int endY,int opcion) {

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
        if(opcion == 1){
            for (int m = startY; m < endY; m++) {
                for (int n = startX; n < endX; n++) {
                    int min = 255;
                    int[] k = new int[3];
                    
                    if (n > 0) {
                        k[0] = matriz[m][n - 1];
                    
                    }
                    k[1] = matriz[m][n];
                    if (m < matriz.length - 1) {
                        k[2] = matriz[m + 1][n];
                    }
                    for (int l = 0; l < 3; l++) {
                        if (k[l] < min) {
                            min = k[l];
                        }
                    }
                    result[m][n] = min;
                }
            }
 
        }else if(opcion == 2){

            for (int m = startY; m < endY; m++) {
                for (int n = startX; n < endX; n++) {
                    int min = 255;
                    int[] k = new int[3];
                    
                    if (n > 0) {
                        k[0] = matriz[m][n - 1];
                    
                    }
                    k[1] = matriz[m][n];
                    if (m > 0) {
                        k[2] = matriz[m - 1][n];
                    }
                    for (int l = 0; l < 3; l++) {
                        if (k[l] < min) {
                            min = k[l];
                        }
                    }
                    result[m][n] = min;
                }
            }

        }else if( opcion ==3){

            for (int m = startY; m < endY; m++) {
                for (int n = startX; n < endX; n++) {
                    int min = 255;
                    int[] k = new int[3];
                    
                    if (n > 0) {
                        k[0] = matriz[m][n - 1];
                    
                    }
                
                    k[1] = matriz[m][n];
                    if (n < matriz[0].length - 1) {
                        k[2] = matriz[m][n + 1];
                    }

                    for (int l = 0; l < 3; l++) {
                        if (k[l] < min) {
                            min = k[l];
                        }
                    }
                    result[m][n] = min;
                }
            }


        }else if (opcion == 4){

            for (int m = startY; m < endY; m++) {
                for (int n = startX; n < endX; n++) {
                    int min = 255;
                    int[] k = new int[2];
              
                    k[0] = matriz[m][n];
                    if (m < matriz.length - 1) {
                        k[1] = matriz[m + 1][n];
                    }
                    for (int l = 0; l < 2; l++) {
                        if (k[l] < min) {
                            min = k[l];
                        }
                    }
                    result[m][n] = min;
                }
            }


        }else if(opcion == 5){
          
            for (int m = startY ; m < endY; m++) {
                for (int n = startX; n < endX; n++) {
                    int min = 255;
                    int[] k = new int[5];
                    
                    if(m == 0 ){

                    }else if (n > 0 ) {
                        k[0] = matriz[m - 1][n - 1];
                    
                    }
                    if (m > 0) {
                        k[1] = matriz[m - 1][n + 1];
                    }
                    k[2] = matriz[m][n];
                    if (n < matriz[0].length - 1 && m < matriz.length -1){
                        k[3] = matriz[m + 1][n + 1];
                    }
                    if (m < matriz.length - 1) {
                        k[4] = matriz[m + 1][n - 1];
                    }
                    for (int l = 0; l < 5; l++) {
                        if (k[l] < min) {
                            min = k[l];
                        }
                    }
                    result[m][n] = min;
                }
            }

        }else{
            for (int m = startY; m < endY; m++) {
                for (int n = startX; n < endX; n++) {
                    int min = 255;
                    int[] k = new int[5];
                    
                    if (n > 0) {
                        k[0] = matriz[m][n - 1];
                    
                    }
                    if (m > 0) {
                        k[1] = matriz[m - 1][n];
                    }
                    k[2] = matriz[m][n];
                    if (n < matriz[0].length - 1) {
                        k[3] = matriz[m][n + 1];
                    }
                    if (m < matriz.length - 1) {
                        k[4] = matriz[m + 1][n];
                    }
                    for (int l = 0; l < 5; l++) {
                        if (k[l] < min) {
                            min = k[l];
                        }
                    }
                    result[m][n] = min;
                }
            }
        }
        
       
    }
}