package com.cloudsuites.framework.modules.property.repository;

import com.cloudsuites.framework.services.entities.property.ManagementCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagementCompanyRepository extends JpaRepository<ManagementCompany, Long> {
    // Add custom query methods if needed

    public ManagementCompany findByName(String name);

    public List<ManagementCompany> findByAddress_CityContaining(String city);

    public List<ManagementCompany> findByAddress_StateContaining(String state);

    public List<ManagementCompany> findByAddress_ProvinceContaining(String province);

    public List<ManagementCompany> findByAddress_CountryContaining(String country);


}

