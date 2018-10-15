package com.ef.service;

import com.ef.model.FlaggedIpAddress;
import com.ef.repository.FlaggedIpAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlaggedIpAddressService {

    @Autowired
    FlaggedIpAddressRepository flaggedIpAddressRepository;

    public void saveFlaggedIpAddress(String ipAddress, String note) {
        flaggedIpAddressRepository.save(FlaggedIpAddress.getInstance(ipAddress, note));
    }

}
