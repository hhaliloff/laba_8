package CollectionClasses;

import java.io.Serializable;

public class Address implements Serializable {
    private String street; //Поле может быть null
    private Location town; //Поле не может быть null

    public Address(String street, Location town) {
        this.street = street;
        setTown(town);
    }

    public String getStreet() {
        return street;
    }

    public Location getTown() {
        return town;
    }

    @Override
    public String toString() {
        return (street != null ? street : "нет улицы") + ", " + town;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setTown(Location town) {
        if (town == null) {
            throw new IllegalArgumentException("Поле town не может быть null");
        }
        this.town = town;
    }
}