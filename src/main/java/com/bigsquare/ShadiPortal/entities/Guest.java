package com.bigsquare.ShadiPortal.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.lang.model.element.NestingKind;

@Entity
@Table(name = "guest")
@Getter
@Setter
@AllArgsConstructor
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_id")
    private Integer id;
    private String name;
    private String phoneNumber;
    private String whatsapp_Number;
    private String email;
    private String guestCategory;
    private String gender;
    private String adultOrchild;
    private String gift;
    private String stay;
    private String cash;
    private Boolean invitationSent = false;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "family_id")
    @JsonIgnoreProperties("guestList")
    private Family family;

    @ManyToOne
    User user;

    public Guest(Integer id, String name, Family family, String phoneNumber, String whatsapp_Number, String email, String guestCategory, String gender, String adultOrchild, String gift, String stay, User user) {
        this.id = id;
        this.name = name;
        this.family = family;
        this.phoneNumber = phoneNumber;
        this.whatsapp_Number = whatsapp_Number;
        this.email = email;
        this.guestCategory = guestCategory;
        this.gender = gender;
        this.adultOrchild = adultOrchild;
        this.gift = gift;
        this.stay = stay;
        this.user = user;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public String getGift() {
        return gift;
    }

    public void setGift(String gift) {
        this.gift = gift;
    }

    public String getStay() {
        return stay;
    }

    public void setStay(String stay) {
        this.stay = stay;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


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
                ", family=" + family +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", whatsapp_Number='" + whatsapp_Number + '\'' +
                ", email='" + email + '\'' +
                ", guestCategory='" + guestCategory + '\'' +
                ", gender='" + gender + '\'' +
                ", adultOrchild='" + adultOrchild + '\'' +
                ", gift='" + gift + '\'' +
                ", stay='" + stay + '\'' +
                ", user=" + user +
                '}';
    }
}
