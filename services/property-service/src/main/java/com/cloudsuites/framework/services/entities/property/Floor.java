package com.cloudsuites.framework.services.entities.property;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "floor")
public class Floor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "floor_id")
    private Long floorId;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;

    @OneToMany(mappedBy = "units", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Unit> units;

    // Other floor attributes
    @Column(name = "floor_number")
    private Integer floorNumber;

}
