package com.cloudsuites.framework.modules.property.repository;

import com.cloudsuites.framework.services.entities.property.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {

    List<Building> findByManagementCompany_ManagementCompanyId(Long managementCompanyId);

    public Building findByNameContaining(String name);

    public List<Building> findByAddress_StreetNumber(String streetNumber);

    public List<Building> findByAddress_City(String city);

    public List<Building> findByAddress_Province(String province);

    public List<Building> findByAddress_State(String state);

    public List<Building> findByAddress_Country(String country);
}