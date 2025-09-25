package com.cloudsuites.framework.services.user.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a physical address.
 * 
 * This entity stores address information for buildings, companies, and other entities
 * that require location data within the property management system.
 * 
 * @author CloudSuites Development Team
 * @since 1.0
 */
@Entity
@Table(name = "addresses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    /**
     * Unique identifier for the address.
     */
    @Id
    @Column(name = "address_id")
    private String addressId;

    /**
     * Street address line 1.
     */
    @NotBlank(message = "Street address is required")
    @Size(max = 255, message = "Street address cannot exceed 255 characters")
    @Column(name = "street", nullable = false, length = 255)
    private String street;

    /**
     * Street address line 2 (optional).
     */
    @Size(max = 255, message = "Street address 2 cannot exceed 255 characters")
    @Column(name = "street2", length = 255)
    private String street2;

    /**
     * City name.
     */
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City cannot exceed 100 characters")
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    /**
     * Province or state code.
     */
    @NotBlank(message = "Province is required")
    @Size(max = 50, message = "Province cannot exceed 50 characters")
    @Column(name = "province", nullable = false, length = 50)
    private String province;

    /**
     * Postal code or ZIP code.
     */
    @NotBlank(message = "Postal code is required")
    @Size(max = 20, message = "Postal code cannot exceed 20 characters")
    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    /**
     * Country name or code.
     */
    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country cannot exceed 100 characters")
    @Column(name = "country", nullable = false, length = 100)
    private String country;

    /**
     * Timestamp when the address was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the address was last modified.
     */
    @UpdateTimestamp
    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    /**
     * User ID who created this address.
     */
    @Column(name = "created_by", length = 255)
    private String createdBy;

    /**
     * User ID who last modified this address.
     */
    @Column(name = "last_modified_by", length = 255)
    private String lastModifiedBy;

    /**
     * Gets the full formatted address as a single string.
     * 
     * @return formatted address string
     */
    public String getFormattedAddress() {
        StringBuilder address = new StringBuilder();
        address.append(street);
        
        if (street2 != null && !street2.trim().isEmpty()) {
            address.append(", ").append(street2);
        }
        
        address.append(", ").append(city);
        address.append(", ").append(province);
        address.append(" ").append(postalCode);
        address.append(", ").append(country);
        
        return address.toString();
    }

    /**
     * Updates audit fields for address modifications.
     * 
     * @param modifiedBy the user ID who is modifying the address
     */
    public void updateAddress(String modifiedBy) {
        this.lastModifiedBy = modifiedBy;
        this.lastModifiedAt = LocalDateTime.now();
    }

    /**
     * Checks if this is a complete address with all required fields.
     * 
     * @return true if all required fields are present
     */
    public boolean isComplete() {
        return street != null && !street.trim().isEmpty() &&
               city != null && !city.trim().isEmpty() &&
               province != null && !province.trim().isEmpty() &&
               postalCode != null && !postalCode.trim().isEmpty() &&
               country != null && !country.trim().isEmpty();
    }
}
