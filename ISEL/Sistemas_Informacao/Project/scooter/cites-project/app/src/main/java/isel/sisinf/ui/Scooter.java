package isel.sisinf.ui;

import jakarta.persistence.*;

@Entity
public class Scooter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double weight;
    private double maxvelocity;
    private int battery;

    @ManyToOne
    @JoinColumn(name = "model", nullable = false)
    private ScooterModel model;

    private java.sql.Timestamp version;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getMaxvelocity() {
        return maxvelocity;
    }

    public void setMaxvelocity(double maxvelocity) {
        this.maxvelocity = maxvelocity;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public ScooterModel getModel() {
        return model;
    }

    public void setModel(ScooterModel model) {
        this.model = model;
    }

    public java.sql.Timestamp getVersion() {
        return version;
    }

    public void setVersion(java.sql.Timestamp version) {
        this.version = version;
    }
}
