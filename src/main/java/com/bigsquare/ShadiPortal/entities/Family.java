package com.bigsquare.ShadiPortal.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.AnyDiscriminatorImplicitValues;
import org.springframework.boot.autoconfigure.web.WebProperties;

import java.util.List;

@Entity
@Table(name = "family")
public class Family {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String familyName;

//    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(mappedBy = "family")
    @JsonIgnoreProperties("family")
    private List<Guest> guestList;

    @JsonCreator
    public Family(@JsonProperty("id") Integer id,@JsonProperty("familyName") String familyName,@JsonProperty("guestList") List<Guest> guestList) {
        this.id = id;
        this.familyName = familyName;
        this.guestList = guestList;
    }

    public Family() {
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }


    public List<Guest> getGuestList() {
        return guestList;
    }

    public void setGuestList(List<Guest> guestList) {
        this.guestList = guestList;
    }

    @Override
    public String toString() {
        return "Family{" +
                "id=" + id +
                ", familyName='" + familyName + '\'' +

                '}';
    }
}
