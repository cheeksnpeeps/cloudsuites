package com.cloudsuites.framework.modules.property;

import com.cloudsuites.framework.services.common.entities.user.ContactInfo;
import com.cloudsuites.framework.services.property.ContactInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class ContactInfoServiceImpl implements ContactInfoService {

    private final ContactInfoRepository contactInfoRepository;

    @Autowired
    public ContactInfoServiceImpl(ContactInfoRepository contactInfoRepository) {
        this.contactInfoRepository = contactInfoRepository;
    }

    @Override
    public ContactInfo getContactInfoById(Long contactInfoId) {
        return contactInfoRepository.findById(contactInfoId).orElse(null);
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

