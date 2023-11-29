package com.cloudsuites.framework.modules.property.repository;

import com.cloudsuites.framework.services.entities.property.PropertyManagementCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyManagementCompanyRepository extends JpaRepository<PropertyManagementCompany, Long> {
    // Add custom query methods if needed

    public PropertyManagementCompany findByCompanyId(Long companyId);

    public PropertyManagementCompany findByCompanyName(String companyName);

    public List<PropertyManagementCompany> findByAddress_City(String city);

}

