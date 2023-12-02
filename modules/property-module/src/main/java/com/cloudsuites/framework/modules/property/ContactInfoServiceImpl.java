package com.cloudsuites.framework.modules.property;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.modules.property.repository.ContactInfoRepository;
import com.cloudsuites.framework.services.common.entities.user.ContactInfo;
import com.cloudsuites.framework.services.property.ContactInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Component
@Transactional
public class ContactInfoServiceImpl implements ContactInfoService {

    private final ContactInfoRepository contactInfoRepository;

    @Autowired
    public ContactInfoServiceImpl(ContactInfoRepository contactInfoRepository) {
        this.contactInfoRepository = contactInfoRepository;
    }

    @Override
    public ContactInfo getContactInfoById(Long contactInfoId) throws NotFoundResponseException {
        return contactInfoRepository.findById(contactInfoId).orElseThrow(() -> new NotFoundResponseException("ContactInfo not found: "+contactInfoId));
    }

    @Override
    public List<ContactInfo> getAllContactInfos() {
        return contactInfoRepository.findAll();
    }

    @Override
    public ContactInfo saveContactInfo(ContactInfo contactInfo) {
        return contactInfoRepository.save(contactInfo);
    }

    @Override
    public void deleteContactInfoById(Long contactInfoId) {
        contactInfoRepository.deleteById(contactInfoId);
    }
}

