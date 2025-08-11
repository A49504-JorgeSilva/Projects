package isel.sisinf.ui;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "CLIENT")
public class Client {

    @Id
    @OneToOne
    @JoinColumn(name = "person")
    private Person person;

    @Column(name = "dtregister", nullable = false)
    private LocalDateTime dtRegister;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Card> cards = new ArrayList<>();

    //Setters

    void setPerson(Person person){
        this.person = person;
    }

    void setDtRegister(LocalDateTime dtRegister){
        this.dtRegister = dtRegister;
    }
    
    //Getters

    Person getPerson(){
        return person;
    }

    LocalDateTime getDtRegister(){
        return dtRegister;
    }

    List<Card> getCards() {
        return cards;
    }
}


