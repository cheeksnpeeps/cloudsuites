package com.cloudsuites.framework.modules.property.features.repository;

import com.cloudsuites.framework.services.property.features.entities.ManagementCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagementCompanyRepository extends JpaRepository<ManagementCompany, String> {

}

