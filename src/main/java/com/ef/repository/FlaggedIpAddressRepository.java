package com.ef.repository;

import com.ef.model.FlaggedIpAddress;
import org.springframework.data.repository.CrudRepository;

public interface FlaggedIpAddressRepository extends CrudRepository<FlaggedIpAddress, Integer> {

}
