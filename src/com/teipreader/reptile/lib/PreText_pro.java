package com.teipreader.reptile.lib;

public class PreText_pro {
    public static String TextProgressBar(int max, int min) {
        float p = (float) min / max;
        double pf = p;
        StringBuilder a = new StringBuilder("[");
        for (int i = 0; i < 20; i++) {
            if (p * 100 / 5 > i) a.append("=");
            else a.append("-");
        }
        return a + "]";
    }
}
