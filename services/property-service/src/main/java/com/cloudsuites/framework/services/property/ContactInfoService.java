package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.common.entities.user.ContactInfo;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface ContactInfoService {

    public ContactInfo getContactInfoById(Long contactInfoId) throws NotFoundResponseException;

    public List<ContactInfo> getAllContactInfos();

    public ContactInfo saveContactInfo(ContactInfo contactInfo);

    public void deleteContactInfoById(Long contactInfoId);
}

