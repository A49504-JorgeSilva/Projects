package isel.sisinf.ui;

import jakarta.persistence.*;

@Entity
@Table(name = "PERSON")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 40, unique = true, nullable = false)
    private String email;

    @Column(name = "taxnumber", unique = true)
    private Integer taxNumber;

    @Column(length = 50, nullable = false)
    private String name;

    //Setters

    void setEmail(String email){
        this.email = email;
    }

    void setTaxNumber(Integer taxNumber){
        this.taxNumber = taxNumber;
    }
    
    void setName(String name){
        this.name = name;
    }

    //Getters

    Integer getId(){
        return id;
    }

    String getEmail(){
        return email;
    }

    Integer getTaxNumber(){
        return taxNumber;
    }

    String getName(){
        return name;
    }

}
