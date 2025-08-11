package isel.sisinf.ui;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "SERVICECOST")
public class ServiceCost {

    @Id
    private int id = 1;

    @Column(name = "unlock", precision = 3, scale = 2)
    private java.math.BigDecimal unlock;

    @Column(name = "usable", precision = 3, scale = 2)
    private java.math.BigDecimal usable;

    public ServiceCost() {
        // Default constructor
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public java.math.BigDecimal getUnlock() {
        return unlock;
    }

    public void setUnlock(java.math.BigDecimal unlock) {
        this.unlock = unlock;
    }

    public java.math.BigDecimal getUsable() {
        return usable;
    }

    public void setUsable(java.math.BigDecimal usable) {
        this.usable = usable;
    }
}
