package com.cloudsuites.framework.modules.property;

import com.cloudsuites.framework.modules.property.repository.ContactInfoRepository;
import com.cloudsuites.framework.services.common.entities.user.ContactInfo;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.ContactInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class ContactInfoServiceImpl implements ContactInfoService {

    private final ContactInfoRepository contactInfoRepository;

    private static final Logger logger = LoggerFactory.getLogger(ContactInfoServiceImpl.class);

    @Autowired
    public ContactInfoServiceImpl(ContactInfoRepository contactInfoRepository) {
        this.contactInfoRepository = contactInfoRepository;
    }

    @Override
    public ContactInfo getContactInfoById(Long contactInfoId) throws NotFoundResponseException {
        logger.debug("Entering getContactInfoById with contactInfoId: {}", contactInfoId);
        ContactInfo contactInfo = contactInfoRepository.findById(contactInfoId)
                .orElseThrow(() -> {
                    logger.error("ContactInfo not found for ID: {}", contactInfoId);
                    return new NotFoundResponseException("ContactInfo not found: " + contactInfoId);
                });
        logger.debug("ContactInfo found: {}", contactInfo);
        return contactInfo;
    }

    @Override
    public List<ContactInfo> getAllContactInfos() {
        List<ContactInfo> contactInfos = contactInfoRepository.findAll();
        logger.debug("Retrieved {} contactInfos", contactInfos.size());
        return contactInfos;
    }
    @Override
    public ContactInfo saveContactInfo(ContactInfo contactInfo) {
        ContactInfo savedContactInfo = contactInfoRepository.save(contactInfo);
        logger.debug("ContactInfo saved: {}", savedContactInfo.getContactInfoId());
        return savedContactInfo;
    }
    @Override
    public void deleteContactInfoById(Long contactInfoId) {
        logger.debug("Entering deleteContactInfoById with contactInfoId: {}", contactInfoId);
        contactInfoRepository.deleteById(contactInfoId);
        logger.debug("ContactInfo deleted: {}", contactInfoId);
    }
}

