package CollectionClasses;

import java.io.Serializable;

public class Venue implements Serializable {
    private static Integer id = 1; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private int capacity; //Значение поля должно быть больше 0
    private VenueType type; //Поле не может быть null
    private Address address; //Поле может быть null


    public Venue(String name, int capacity, VenueType type, Address address) {
        this.id = id + 1;
        setName(name);
        setCapacity(capacity);
        setType(type);
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public VenueType getType() {
        return type;
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return name + " | вместимость: " + capacity +
                " | тип: " + type +
                (address != null ? " | адрес: " + address : "");
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Поле name не может быть null или пустым");
        }
        this.name = name;
    }

    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Значение поля capacity должно быть больше 0");
        }
        this.capacity = capacity;
    }

    public void setType(VenueType type) {
        if (type == null) {
            throw new IllegalArgumentException("Поле type не может быть null");
        }
        this.type = type;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}