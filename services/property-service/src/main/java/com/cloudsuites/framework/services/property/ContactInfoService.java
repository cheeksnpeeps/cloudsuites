package com.cloudsuites.framework.services.property;

import com.cloudsuites.framework.services.common.entities.user.ContactInfo;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface ContactInfoService {

    public ContactInfo getContactInfoById(Long contactInfoId);

    public List<ContactInfo> getAllContactInfos();

    public ContactInfo saveContactInfo(ContactInfo contactInfo);

    public void deleteContactInfoById(Long contactInfoId);
}

