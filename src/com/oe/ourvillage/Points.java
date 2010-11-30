package com.oe.ourvillage;


public class Points {

    double cp1, cp2;
    double p1, p2, a, b;

    public boolean PointInTriangle(double p, double a, double b, double c) {
        if (SameSide(p, a, b, c) && SameSide(p, b, a, c) && SameSide(p, c, a, b)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean SameSide(double p1, double p2, double a, double b) {
        cp1 = CrossProduct(b - a, p1 - a);
        cp2 = CrossProduct(b - a, p2 - a);
        if (DotProduct(cp1, cp2) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    private double CrossProduct(double d, double e) {
        // TODO Auto-generated method stub
        return 0;
    }

    private int DotProduct(double cp12, double cp22) {
        // TODO Auto-generated method stub
        return 0;
    }
}
