package com.dustngroh.parkinglotapi.entity;

import jakarta.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class ParkingLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String address = "Unknown";

    @Column(nullable = false)
    private int totalSpaces;

    @Column(nullable = false)
    private int reservedSpaces;

    @OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations;

    @ManyToMany
    @JoinTable(
            name = "parkinglot_staff",
            joinColumns = @JoinColumn(name = "parkinglot_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> staff;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTotalSpaces() {
        return totalSpaces;
    }

    public void setTotalSpaces(int totalSpaces) {
        this.totalSpaces = totalSpaces;
    }

    public int getReservedSpaces() {
        return reservedSpaces;
    }

    public void setReservedSpaces(int reservedSpaces) {
        this.reservedSpaces = reservedSpaces;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public Set<User> getStaff() {
        return staff;
    }

    public void setStaff(Set<User> staff) {
        this.staff = staff;
    }
}