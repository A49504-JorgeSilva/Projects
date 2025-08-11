package isel.sisinf.ui;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "TYPEOFCARD")
public class TypeOfCard {

    @Id
    @Column(length = 10)
    private String reference;

    @Column(name = "nodays")
    private Integer nodays;

    @Column(precision = 4, scale = 2)
    private BigDecimal price;

    //Setters

    //Getters
}
