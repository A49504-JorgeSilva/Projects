package isel.sisinf.ui;

import jakarta.persistence.*;

@Entity
public class Dock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int number;

    @ManyToOne(optional = false)
    @JoinColumn(name = "station")
    private Station station;

    @Column(nullable = false)
    private String state;

    @ManyToOne
    @JoinColumn(name = "scooter")
    private Scooter scooter;

    private java.sql.Timestamp version;

    // Getters and Setters
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Scooter getScooter() {
        return scooter;
    }

    public void setScooter(Scooter scooter) {
        this.scooter = scooter;
    }

    public java.sql.Timestamp getVersion() {
        return version;
    }

    public void setVersion(java.sql.Timestamp version) {
        this.version = version;
    }
}
