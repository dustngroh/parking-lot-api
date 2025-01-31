package com.dustngroh.parkinglotapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ParkingLotDTO {

    private Long id;

    @NotBlank(message = "Name is required.")
    private String name;

    @NotBlank(message = "Address is required.")
    private String address;

    @NotNull(message = "Total spaces must be provided.")
    @Min(value = 1, message = "Total spaces must be at least 1.")
    private Integer totalSpaces;

    @NotNull(message = "Reserved spaces must be provided.")
    @Min(value = 0, message = "Reserved spaces cannot be negative.")
    private Integer reservedSpaces;

    // Default Constructor (Needed for deserialization)
    public ParkingLotDTO() {}

    // Constructor for API Responses
    public ParkingLotDTO(Long id, String name, String address, Integer totalSpaces, Integer reservedSpaces) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.totalSpaces = totalSpaces;
        this.reservedSpaces = reservedSpaces;
    }

    // Constructor to Convert Entity to DTO
    public ParkingLotDTO(com.dustngroh.parkinglotapi.entity.ParkingLot parkingLot) {
        this.id = parkingLot.getId();
        this.name = parkingLot.getName();
        this.address = parkingLot.getAddress();
        this.totalSpaces = parkingLot.getTotalSpaces();
        this.reservedSpaces = parkingLot.getReservedSpaces();
    }

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

    public Integer getTotalSpaces() {
        return totalSpaces;
    }

    public void setTotalSpaces(Integer totalSpaces) {
        this.totalSpaces = totalSpaces;
    }

    public Integer getReservedSpaces() {
        return reservedSpaces;
    }

    public void setReservedSpaces(Integer reservedSpaces) {
        this.reservedSpaces = reservedSpaces;
    }
}
