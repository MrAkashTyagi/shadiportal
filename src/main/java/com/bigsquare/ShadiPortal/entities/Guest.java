package com.bigsquare.ShadiPortal.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "guest")
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "guest_id")
    private Integer id;
    private String name;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "family_id")
    @JsonIgnoreProperties("guestList")
    private Family family;



    public Guest(Integer id, String name, String phoneNumber, String whatsapp_Number, String email, String guestCategory, String gender, String adultOrchild, Family family) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.whatsapp_Number = whatsapp_Number;
        this.email = email;
        this.guestCategory = guestCategory;
        this.gender = gender;
        this.adultOrchild = adultOrchild;
        this.family = family;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    private String phoneNumber;
    private String whatsapp_Number;
    private String email;
    private String guestCategory;
    private String gender;
    private String adultOrchild;




    @ManyToOne
    User user;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAdultOrchild() {
        return adultOrchild;
    }

    public void setAdultOrchild(String adultOrchild) {
        this.adultOrchild = adultOrchild;
    }

//    @ManyToOne
//    @JoinColumn(name = "family_id")
//    private Family family;

    public String getGuestCategory() {
        return guestCategory;
    }

    public void setGuestCategory(String guestCategory) {
        this.guestCategory = guestCategory;
    }

    public Guest() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWhatsapp_Number() {
        return whatsapp_Number;
    }

    public void setWhatsapp_Number(String whatsapp_Number) {
        this.whatsapp_Number = whatsapp_Number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Guest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", whatsapp_Number='" + whatsapp_Number + '\'' +
                ", email='" + email + '\'' +
                ", guestCategory='" + guestCategory + '\'' +

                '}';
    }
}
