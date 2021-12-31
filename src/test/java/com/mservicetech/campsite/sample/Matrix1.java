package com.mservicetech.campsite.sample;

public class Matrix1 {

    static void displayMatrix(
            int N, int mat[][])
    {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++)
                System.out.print(
                        " " + mat[i][j]);

            System.out.print("\n");
        }
        System.out.print("\n");
    }

    static int[][] rotateMatrix(int N, int mat[][]) {
        int[][] result = new int[N][N];
        for (int r=0; r<N; r++) {
            for (int c=0; c<N; c++) {
                result[Math.abs(c-N+1)][r] = mat[r][c];
            }
        }
        return result;
    }

    public static void rotate(int[][] matrix) {
        int n = matrix.length;
        for (int i = 0; i < n / 2; i++) {
            for (int j = 0; j < Math.ceil(((double) n) / 2.); j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[n-1-j][i];
                matrix[n-1-j][i] = matrix[n-1-i][n-1-j];
                matrix[n-1-i][n-1-j] = matrix[j][n-1-i];
                matrix[j][n-1-i] = temp;
            }
        }
    }

    static int[][] rotateMatrix180(int N, int mat[][]) {
        int[][] result = new int[N][N];
        for (int r=0; r<N; r++) {
            for (int c=0; c<N; c++) {
                result[Math.abs(r-N+1)][Math.abs(c-N+1)] = mat[r][c];
            }
        }
        return result;
    }

    static int[][] rotateMatrix90(int N, int mat[][]) {
        int[][] result = new int[N][N];
        for (int r=0; r<N; r++) {
            for (int c=0; c<N; c++) {
                result[c][Math.abs(r-N+1)] = mat[r][c];
            }
        }
        return result;
    }

    public static void main(String[] args)
    {
        int N = 4;

        // Test Case 1
        int mat[][] = {
                { 1, 2, 3, 4 },
                { 5, 6, 7, 8 },
                { 9, 10, 11, 12 },
                { 13, 14, 15, 16 }
        };
        displayMatrix(N, mat);

       // displayMatrix(N, rotateMatrix90(N, mat));
        rotate(mat);
        displayMatrix(N, mat);
    }
}
