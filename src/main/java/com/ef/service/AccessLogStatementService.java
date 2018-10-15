package com.ef.service;

import com.ef.model.Duration;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ef.model.AccessLogStatement;
import com.ef.repository.AccessLogStatementRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class AccessLogStatementService {

    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessLogStatementService.class);
    private static final int PREPARED_STATEMENT_BATCH_SIZE = 200;
    private static final String INSERT_PREPARED_STATEMENT =
            "INSERT INTO " + AccessLogStatement.getTableName() +
            " (datetime, ip, request, status, agent, id) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    @Autowired
    AccessLogStatementRepository accessLogStatementRepository;

    @Autowired
    DataSource dataSource;

    /**
     * While {@link #importAccessLogStatements(String)} uses Spring's batch insert features leveraging
     * {@link org.springframework.data.repository.CrudRepository#saveAll(Iterable)},
     * {@link #importAccessLogStatementsAsBatch(String)} is simpler and much faster. Additionally, a method using
     * MySQL's "LOAD DATA INFILE..." might be faster still, but would lose the ability to proceed with an import
     * if a single line of the infile is invalid. {@link #importAccessLogStatementsAsBatch(String)} performs
     * 5x faster than {@link #importAccessLogStatements(String)} and 10x faster than a for-loop executing
     * individual {@link org.springframework.data.repository.CrudRepository#save(Object)} for each record.
     */
    public void importAccessLogStatementsAsBatch(String accessLogPath) throws Exception {

        final Reader reader = getReader(accessLogPath);
        final Iterable<CSVRecord> records = CSVFormat.DEFAULT.withDelimiter('|').withQuote('"').withIgnoreEmptyLines().parse(reader);

        int i = 0;
        final PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(INSERT_PREPARED_STATEMENT);
        for (CSVRecord record: records) {
            try {
                final Date datetime = new SimpleDateFormat(DATETIME_FORMAT).parse(record.get(0));
                final String ip = record.get(1);
                final String request = record.get(2);
                final String status = record.get(3);
                final String agent = record.get(4);
                preparedStatement.setTimestamp(1, new java.sql.Timestamp(datetime.getTime()));
                preparedStatement.setString(2, ip);
                preparedStatement.setString(3, request);
                preparedStatement.setString(4, status);
                preparedStatement.setString(5, agent);
                preparedStatement.setInt(6, ++i);
                preparedStatement.addBatch();
                if (i % PREPARED_STATEMENT_BATCH_SIZE == 0) {
                    preparedStatement.executeBatch();
                }
            } catch (Exception e) {
                LOGGER.error("Unable to process row in access log. Skipping row and proceeding through file.");
            }
        }
        preparedStatement.executeBatch();

    }

    public void importAccessLogStatements(String accessLogPath) throws Exception {

        final Reader reader = getReader(accessLogPath);
        final Iterable<CSVRecord> records = CSVFormat.DEFAULT.withDelimiter('|').withQuote('"').withIgnoreEmptyLines().parse(reader);
        Iterable<AccessLogStatement> statements = () -> StreamSupport.stream(records.spliterator(), true)
            .map(record -> {
                try {
                    final Date datetime = new SimpleDateFormat(DATETIME_FORMAT).parse(record.get(0));
                    final String ip = record.get(1);
                    final String request = record.get(2);
                    final String status = record.get(3);
                    final String agent = record.get(4);
                    return AccessLogStatement.getInstance(datetime, ip, request, status, agent);
                } catch (Exception e) {
                    LOGGER.error("Unable to process row in access log. Skipping row and proceeding through file.");
                }
                return null;
            })
            .filter(record -> record != null)
            .iterator();

        accessLogStatementRepository.saveAll(statements);

    }

    public List<String> getIpAddressesForDateRangeAndThreshold(Date datetimeStart, Duration duration, int threshold) throws Exception {
        final Date datetimeEnd = getEndDateFromDuration(datetimeStart, duration);
        return accessLogStatementRepository.findIpAddressWithDateRangeAndThreshold(
                datetimeStart,
                datetimeEnd,
                threshold
        );
    }

    Reader getReader(String accessLogPath) throws FileNotFoundException {
        return new FileReader(accessLogPath);
    }

    static Date getEndDateFromDuration(Date datetimeStart, Duration duration) {
        return Duration.DAILY.equals(duration) ? DateUtils.addDays(datetimeStart, 1) : DateUtils.addHours(datetimeStart, 1);
    }

}
