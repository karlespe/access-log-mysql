package com.ef;

import com.ef.model.Duration;
import com.ef.model.Parameters;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

enum ParametersParser {

    // Effective Java Singleton Pattern - Item 3: Enforce the singleton property with a private constructor or an enum type.
    INSTANCE;

    private final static String OPTION_NAME_ACCESS_LOG = "a";
    private final static String OPTION_NAME_START_DATE = "s";
    private final static String OPTION_NAME_DURATION = "d";
    private final static String OPTION_NAME_THRESHOLD = "t";

    private final static String OPTION_LONG_NAME_ACCESS_LOG = "accesslog";
    private final static String OPTION_LONG_NAME_START_DATE = "startDate";
    private final static String OPTION_LONG_NAME_DURATION = "duration";
    private final static String OPTION_LONG_NAME_THRESHOLD = "threshold";

    protected final static String START_DATE_FORMAT = "yyyy-MM-dd.HH:mm:ss";

    protected final static Option OPTION_ACCESS_LOG = new Option(
        OPTION_NAME_ACCESS_LOG,
        OPTION_LONG_NAME_ACCESS_LOG,
        true,
        "the path to the access log"
    );
    protected final static Option OPTION_START_DATE = new Option(
            OPTION_NAME_START_DATE,
            OPTION_LONG_NAME_START_DATE,
            true,
            "the start datetime upon which to scan for multiple requests (format: yyyy-MM-dd.HH:mm:ss)"
    );
    protected final static Option OPTION_DURATION = new Option(
            OPTION_NAME_DURATION,
            OPTION_LONG_NAME_DURATION,
            true,
            "the time period upon which to scan for multiple requests (options: hourly or daily)"
    );
    protected final static Option OPTION_THRESHOLD = new Option(
            OPTION_NAME_THRESHOLD,
            OPTION_LONG_NAME_THRESHOLD,
            true,
            "the number of multiple requests for which a scan is attempted (integer)"
    );
    protected final static Options OPTIONS;

    static {
        OPTIONS = new Options();
        OPTIONS.addOption(OPTION_ACCESS_LOG);
        OPTIONS.addOption(OPTION_START_DATE);
        OPTIONS.addOption(OPTION_DURATION);
        OPTIONS.addOption(OPTION_THRESHOLD);
    }

    public Parameters getParameters(String[] commandLineArguments) throws Exception {

        final CommandLineParser parser = new GnuParser();
        try {
            final CommandLine cmd = parser.parse(OPTIONS, commandLineArguments);

            if (cmd.hasOption(OPTION_NAME_START_DATE) && cmd.hasOption(OPTION_NAME_DURATION) && cmd.hasOption(OPTION_NAME_THRESHOLD)) {

                final String accessLogPath = getAccessLogPath(cmd, OPTIONS);
                final Date startDate = getStartDate(cmd, OPTIONS);
                final Duration duration = getDuration(cmd, OPTIONS);
                final int threshold = getThreshold(cmd, OPTIONS);

                return new Parameters(accessLogPath, startDate, duration, threshold);

            } else {
                final String message = "Incorrect command line parameters.";
                printHelp(message, OPTIONS);
                throw new IOException(message);
            }

        } catch (ParseException e) {
            final String message = "Incorrect command line parameters.";
            printHelp(message, OPTIONS);
            throw e;
        }

    }

    protected static String getAccessLogPath(CommandLine commandLine, Options options) throws Exception {
        String accessLogPath;
        final String errorMessage = "Invalid access file path. The file specified by the given path cannot be found.";
        try {
            accessLogPath = commandLine.getOptionValue(OPTION_NAME_ACCESS_LOG);
            final File file = new File(accessLogPath);
            if (!file.exists() || !file.isFile()) {
                throw new IOException(errorMessage);
            }
        } catch (Exception e) {
            printHelp(errorMessage, options);
            throw e;
        }
        return accessLogPath;
    }

    protected static Date getStartDate(CommandLine commandLine, Options options) throws java.text.ParseException {
        Date startDate;
        try {
            startDate = new SimpleDateFormat(START_DATE_FORMAT).parse(commandLine.getOptionValue(OPTION_NAME_START_DATE));
        } catch (java.text.ParseException e) {
            printHelp("Incorrect startDate format. Please use the format " + START_DATE_FORMAT + ".", options);
            throw e;
        }
        return startDate;
    }

    protected static Duration getDuration(CommandLine commandLine, Options options) throws IOException {
        final String duration = commandLine.getOptionValue(OPTION_NAME_DURATION);
        if (Duration.DAILY.name().equalsIgnoreCase(duration)) {
            return Duration.DAILY;
        } else if (Duration.HOURLY.name().equalsIgnoreCase(duration)) {
            return Duration.HOURLY;
        } else {
            final String message = "Incorrect duration format. Please use values hourly or daily.";
            printHelp(message, options);
            throw new IOException(message);
        }
    }

    protected static int getThreshold(CommandLine commandLine, Options options) throws IOException {
        try {
            return Integer.parseInt(commandLine.getOptionValue(OPTION_NAME_THRESHOLD));
        } catch (NumberFormatException e) {
            final String message = "Incorrect threshold format. Please use an integer value.";
            printHelp(message, options);
            throw new IOException(message);
        }
    }

    private static void printHelp(String message, Options options) {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar \"parser.jar\"", "\n" + message, options, "e.g. java -jar \"parser.jar\" --accesslog=./access.log --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100");
    }


}
