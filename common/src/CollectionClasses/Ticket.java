package CollectionClasses;

import java.io.Serializable;
import java.time.LocalDate;

public class Ticket implements Serializable {
    private Integer id = -1; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private float price; //Значение поля должно быть больше 0
    private String comment; //Строка не может быть пустой, Поле не может быть null
    private boolean refundable;
    private TicketType type; //Поле не может быть null
    private Venue venue;

    public Ticket(String name, Coordinates coordinates, java.time.LocalDate creationDate, float price, String comment, boolean refundable, TicketType type, Venue venue) {
        this.id = id + 1;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.price = price;
        this.comment = comment;
        this.refundable = refundable;
        this.type = type;
        this.venue = venue;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public java.time.LocalDate getCreationDate() {
        return creationDate;
    }

    public float getPrice() {
        return price;
    }

    public String getComment() {
        return comment;
    }

    public boolean getRefundable() {
        return refundable;
    }

    public TicketType getType() {
        return type;
    }

    public Venue getVenue() {
        return venue;
    }

    @Override
    public String toString() {
        return "\n--- Билет #" + id + " ---" +
                "\n  Название:    " + name +
                "\n  Цена:        " + price +
                "\n  Тип:         " + type +
                "\n  Дата:        " + creationDate +
                "\n  Комментарий: " + comment +
                "\n  Возврат:     " + refundable +
                "\n  Координаты:  " + coordinates +
                "\n  Место:       " + (venue != null ? venue : "нет");
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Поле name не может быть null или пустым");
        }
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Поле coordinates не может быть null");
        }
        this.coordinates = coordinates;
    }

    public void setCreationDate() {
        this.creationDate = LocalDate.now();
    }

    public void setPrice(float price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Значение поля price должно быть больше 0");
        }
        this.price = price;
    }

    public void setComment(String comment) {
        if (comment == null || comment.isEmpty()) {
            throw new IllegalArgumentException("Поле comment не может быть null или пустым");
        }
        this.comment = comment;
    }

    public void setRefundable(boolean refundable) {
        this.refundable = refundable;
    }

    public void setType(TicketType type) {
        if (type == null) {
            throw new IllegalArgumentException("Поле type не может быть null");
        }
        this.type = type;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }
}
