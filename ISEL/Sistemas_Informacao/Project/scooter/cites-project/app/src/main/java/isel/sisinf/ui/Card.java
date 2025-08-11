package isel.sisinf.ui;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "CARD")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(precision = 4, scale = 2, nullable = false)
    private BigDecimal credit;

    @ManyToOne
    @JoinColumn(name = "typeofcard")
    private TypeOfCard typeOfCard;

    @ManyToOne
    @JoinColumn(name = "client")
    private Client client;

    //Setters

    void setCredit(BigDecimal initialCredit){
        this.credit = initialCredit;
    }

    void setTypeOfCard(TypeOfCard type){
        this.typeOfCard = type;
    }

    void setClient(Client client){
        this.client = client;
    }

    //Getters

    Integer getId(){
        return id;
    }

    BigDecimal getCredit(){
        return credit;
    }

    TypeOfCard getTypeOfCard(){
        return typeOfCard;
    }

    Client getClient(){
        return client;
    }
}
