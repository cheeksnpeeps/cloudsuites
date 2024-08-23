package com.cloudsuites.framework.services.property.features.entities;

import com.cloudsuites.framework.modules.common.utils.IdGenerator;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "lease", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"owner_id", "unit_id" })
})
public class Lease {
    @Id
    @Column(name = "lease_id", unique = true, nullable = false)
    private String leaseId;

    @Column(name = "owner_id")
    private String ownerId;

    @Column(name = "unit_id")
    private String unitId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "original_start_date")
    private LocalDate originalStartDate;

    @Column(name = "original_end_date")
    private LocalDate originalEndDate;

    @Column(name = "rental_amount")
    private Double rentalAmount;

    @Enumerated(EnumType.STRING)
    private LeaseStatus status;

    @Column(name = "renewal_count")
    private int renewalCount;

    @PrePersist
    protected void onCreate() {
        this.leaseId = IdGenerator.generateULID("LS-");
        this.originalStartDate = this.startDate;
        this.originalEndDate = this.endDate;
        this.renewalCount = 0;
    }

    public void renewLease(LocalDate newEndDate) {
        this.endDate = newEndDate;
        this.renewalCount++;
    }

    public void updateLease(Lease lease) {
        if (lease.getStartDate() != null) {
            this.startDate = lease.getStartDate();
        }
        if (lease.getEndDate() != null) {
            this.endDate = lease.getEndDate();
        }
        if (lease.getRentalAmount() != null) {
            this.rentalAmount = lease.getRentalAmount();
        }
        if (lease.getStatus() != null) {
            this.status = lease.getStatus();
        }
    }
}