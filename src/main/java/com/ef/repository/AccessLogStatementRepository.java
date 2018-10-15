package com.ef.repository;

import com.ef.model.AccessLogStatement;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface AccessLogStatementRepository extends CrudRepository<AccessLogStatement, Integer> {

    @Query("SELECT ip from AccessLogStatement " +
           "WHERE datetime >= ?1 and datetime < ?2 " +
           "GROUP BY ip HAVING COUNT(1) >= ?3")
    List<String> findIpAddressWithDateRangeAndThreshold(Date datetimeStart, Date datetimeEnd, long threshold);

}
