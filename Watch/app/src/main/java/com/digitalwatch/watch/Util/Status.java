package com.digitalwatch.watch.Util;

// 색상 설정
public class Status {
    public Status() {

    }

    public static int[][] setColor() {
        int[][] colorRGB = new int[14][3];

        // 빨강색
        colorRGB[0][0] = 255;
        colorRGB[0][1] = 0;
        colorRGB[0][2] = 0;

        // 다홍색
        colorRGB[1][0] = 255;
        colorRGB[1][1] = 94;
        colorRGB[1][2] = 0;

        // 주황색
        colorRGB[2][0] = 255;
        colorRGB[2][1] = 187;
        colorRGB[2][2] = 0;

        // 노랑색
        colorRGB[3][0] = 255;
        colorRGB[3][1] = 228;
        colorRGB[3][2] = 0;

        // 연두색
        colorRGB[4][0] = 171;
        colorRGB[4][1] = 242;
        colorRGB[4][2] = 0;

        // 초록색
        colorRGB[5][0] = 29;
        colorRGB[5][1] = 219;
        colorRGB[5][2] = 22;

        // 하늘색
        colorRGB[6][0] = 0;
        colorRGB[6][1] = 216;
        colorRGB[6][2] = 255;

        // 파랑색
        colorRGB[7][0] = 0;
        colorRGB[7][1] = 84;
        colorRGB[7][2] = 255;

        // 남색
        colorRGB[8][0] = 1;
        colorRGB[8][1] = 0;
        colorRGB[8][2] = 255;

        // 보라색
        colorRGB[9][0] = 95;
        colorRGB[9][1] = 0;
        colorRGB[9][2] = 255;

        // 이상한 핑크색
        colorRGB[10][0] = 255;
        colorRGB[10][1] = 0;
        colorRGB[10][2] = 221;

        // 빨강핑크색
        colorRGB[11][0] = 255;
        colorRGB[11][1] = 0;
        colorRGB[11][2] = 127;

        // 흰색
        colorRGB[12][0] = 255;
        colorRGB[12][1] = 255;
        colorRGB[12][2] = 255;

        // 검정색
        colorRGB[13][0] = 0;
        colorRGB[13][1] = 0;
        colorRGB[13][2] = 0;

        return colorRGB;
    }
}
