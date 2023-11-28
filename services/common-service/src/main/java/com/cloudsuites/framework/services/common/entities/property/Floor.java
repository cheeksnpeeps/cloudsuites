package com.cloudsuites.framework.services.common.entities.property;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL)
    private List<Unit> units;

    // Other floor attributes
    @Column(name = "floor_number")
    private Integer floorNumber;

}
