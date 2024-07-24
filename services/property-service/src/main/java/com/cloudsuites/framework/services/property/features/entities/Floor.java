package com.cloudsuites.framework.services.property.features.entities;

import com.cloudsuites.framework.modules.common.utils.IdGenerator;
import jakarta.persistence.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Data
@Entity
@Table(name = "floor")
public class Floor {

    private static final Logger logger = LoggerFactory.getLogger(Floor.class);

    @Id
    @Column(name = "floor_id", unique = true, nullable = false)
    private String floorId;

    @Column(name = "floor_name")
    private String floorName;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;

    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Unit> units;

    @Column(name = "floor_number")
    private Integer floorNumber;

    @PrePersist
    protected void onCreate() {
        this.floorId = IdGenerator.generateULID("FL-");
        logger.debug("Generated floorId: {}", this.floorId);
    }

}
