package com.bigsquare.ShadiPortal.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.AnyDiscriminatorImplicitValues;
import org.springframework.boot.autoconfigure.web.WebProperties;

import java.util.List;

@Entity
@Table(name = "family")
public class Family {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "family_id")
    private int id;
    private String familyName;

//    @OneToMany(mappedBy = "family")
//    private List<Guest> guestList;

    public Family(int id, String familyName, List<Guest> guestList) {
        this.id = id;
        this.familyName = familyName;
//        this.guestList = guestList;
    }

    public Family() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }


//    public List<Guest> getGuestList() {
//        return guestList;
//    }

//    public void setGuestList(List<Guest> guestList) {
//        this.guestList = guestList;
//    }

    @Override
    public String toString() {
        return "Family{" +
                "id=" + id +
                ", familyName='" + familyName + '\'' +

                '}';
    }
}
