package com.cloudsuites.framework.services.entities.property;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;

    @JsonManagedReference
    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Unit> units;

    // Other floor attributes
    @Column(name = "floor_number")
    private Integer floorNumber;

}
