package com.ef;

import com.ef.model.Duration;
import com.ef.model.Parameters;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

public class ParametersParserTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testGetParameters_IncorrectParameters() throws Exception {

        exception.expect(Exception.class);
        ParametersParser.INSTANCE.getParameters(new String[]{""});

        exception.expect(Exception.class);
        ParametersParser.INSTANCE.getParameters(new String[]{"--accesslog=access.log", "--startDate=2017-01-01.13:00:00", "--duration=hourly"});

        exception.expect(Exception.class);
        ParametersParser.INSTANCE.getParameters(new String[]{"--accesslog=access.log", "--startDate=2017-01-01.13:00:00", "--threshold=100"});

        exception.expect(Exception.class);
        ParametersParser.INSTANCE.getParameters(new String[]{"--accesslog=access.log", "--duration=hourly", "--threshold=100"});

        exception.expect(Exception.class);
        ParametersParser.INSTANCE.getParameters(new String[]{"--accesslog=access.log", "--startDate=2017-01-01.13:00:00", "--duration=hourly", "--threshold=100"});

    }

    @Test
    public void testGetParameters_ValidParameters() throws Exception {

        final String accessFilePath = "testGetParameters_ValidParameters.log";
        final String startDate = "2017-01-01.13:00:00";
        final FileOutputStream fileOutputStream = new FileOutputStream(accessFilePath);
        fileOutputStream.write("file".getBytes());
        fileOutputStream.close();

        final Parameters parameters = ParametersParser.INSTANCE.getParameters(
                new String[]{"--accesslog=" + accessFilePath, "--startDate=" + startDate, "--duration=hourly", "--threshold=100"}
        );

        final File file = new File(accessFilePath);
        file.delete();

        assert accessFilePath.equals(parameters.getAccessLogPath());
        assert Duration.HOURLY.equals(parameters.getDuration());
        assert 100 == parameters.getThreshold();
        assert new SimpleDateFormat(ParametersParser.START_DATE_FORMAT).parse(startDate).equals(parameters.getStartDate());

    }



}