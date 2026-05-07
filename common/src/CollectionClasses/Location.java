package CollectionClasses;

import java.io.Serializable;

public class Location implements Serializable {
    private Float x; //Поле не может быть null
    private int y;
    private double z;
    private String name; //Поле может быть null

    public Location(Float x, int y, double z, String name) {
        setX(x);
        this.y = y;
        this.z = z;
        this.name = name;
    }

    public Float getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return (name != null ? name : "без названия") +
                " (" + x + ", " + y + ", " + z + ")";
    }

    public void setX(Float x) {
        if (x == null) {
            throw new IllegalArgumentException("Поле x не может быть null");
        }
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setName(String name) {
        this.name = name;
    }
}