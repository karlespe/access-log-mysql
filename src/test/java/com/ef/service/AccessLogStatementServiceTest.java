package com.ef.service;

import com.ef.model.Duration;
import com.ef.repository.AccessLogStatementRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.StreamSupport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class AccessLogStatementServiceTest {

    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final static String ACCESS_LOG = "testImportAccessLogStatements.log";

    @Before
    public void setUp() throws Exception {
        final FileOutputStream fileOutputStream = new FileOutputStream(ACCESS_LOG);
        fileOutputStream.write(LOG.getBytes());
        fileOutputStream.close();
    }

    @After
    public void tearDown() throws Exception {
        File file = new File(ACCESS_LOG);
        file.delete();
    }

    @Test
    public void testGetEndDateFromDuration() throws Exception {
        final Date date = new SimpleDateFormat(DATE_FORMAT).parse("2018-10-09 13:00:00");
        final Date dateOneHourLater = new SimpleDateFormat(DATE_FORMAT).parse("2018-10-09 14:00:00");
        final Date dateOneDayLater = new SimpleDateFormat(DATE_FORMAT).parse("2018-10-10 13:00:00");
        assert AccessLogStatementService.getEndDateFromDuration(date, Duration.HOURLY).equals(dateOneHourLater);
        assert AccessLogStatementService.getEndDateFromDuration(date, Duration.DAILY).equals(dateOneDayLater);
    }

    @Test
    public void testImportAccessLogStatements() throws Exception {

        final AccessLogStatementService service = new AccessLogStatementService();
        service.accessLogStatementRepository = Mockito.mock(AccessLogStatementRepository.class);

        service.importAccessLogStatements(ACCESS_LOG);

        verify(service.accessLogStatementRepository, times(1)).saveAll(any());

        final ArgumentCaptor<Iterable> argument = ArgumentCaptor.forClass(Iterable.class);
        verify(service.accessLogStatementRepository).saveAll(argument.capture());
        assert 10 == StreamSupport.stream(argument.getValue().spliterator(), false).count();

    }

    private final static String LOG = "" +
            "2017-01-01 12:55:11.763|192.168.234.81|GET / HTTP/1.1|200|agent\n" +
            "2017-01-01 13:13:11.763|192.168.234.82|GET / HTTP/1.1|200|agent\n" +
            "2017-01-01 13:33:15.763|192.168.234.82|GET / HTTP/1.1|200|agent\n" +
            "2017-01-01 13:44:15.763|192.168.234.82|GET / HTTP/1.1|200|agent\n" +
            "2017-01-01 13:49:15.763|192.168.234.82|GET / HTTP/1.1|200|agent\n" +
            "2017-01-01 14:03:22.763|192.168.234.83|GET / HTTP/1.1|200|agent\n" +
            "2017-01-01 18:12:33.763|192.168.234.83|GET / HTTP/1.1|200|agent\n" +
            "2017-01-01 22:52:44.763|192.168.234.83|GET / HTTP/1.1|200|agent\n" +
            "2017-01-01 23:01:55.763|192.168.234.83|GET / HTTP/1.1|200|agent\n" +
            "2017-01-02 04:52:11.763|192.168.234.83|GET / HTTP/1.1|200|agent\n";

}