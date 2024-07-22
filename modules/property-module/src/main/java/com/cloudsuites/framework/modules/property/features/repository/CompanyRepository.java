package com.cloudsuites.framework.modules.property.features.repository;

import com.cloudsuites.framework.services.property.features.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {

}

