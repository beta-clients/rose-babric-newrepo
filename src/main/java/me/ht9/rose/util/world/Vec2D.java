package me.ht9.rose.util.world;

public final class Vec2D
{
    private double x;
    private double y;

    public Vec2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double x() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double y() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vec2D vec)) return false;

        return this.x() == vec.x() && this.y() == vec.y();
    }

    @Override
    public String toString() {
        return "[X: " + this.x() + ", Y: " + this.y() + "]";
    }
}
