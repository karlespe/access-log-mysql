package com.ef;

import com.ef.service.AccessLogStatementService;
import com.ef.service.FlaggedIpAddressService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.ef.model.Parameters;
import org.springframework.util.CollectionUtils;

import java.util.List;

@SpringBootApplication(exclude = {WebMvcAutoConfiguration.class})
public class Parser {

    @Autowired
    private AccessLogStatementService accessLogStatementService;

    @Autowired
    private FlaggedIpAddressService flaggedIpAddressService;

    public static void main(String[] args) {
        SpringApplication.run(Parser.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> parse(args);
    }

    void parse(String[] args) {

        try {

            final Parameters parameters = ParametersParser.INSTANCE.getParameters(args);

            System.out.println("\nImporting access log statements...");
            accessLogStatementService.importAccessLogStatementsAsBatch(parameters.getAccessLogPath());
            System.out.println("\nAccess log statements successfully imported.");

            final List<String> ips = accessLogStatementService.getIpAddressesForDateRangeAndThreshold(
                parameters.getStartDate(),
                parameters.getDuration(),
                parameters.getThreshold()
            );

            if (!CollectionUtils.isEmpty(ips)) {

                System.out.println("\nFlagged IP addresses:\n");
                final String note = parameters.getThreshold() + " or more requests attempted " + parameters.getDuration().name() + " starting at " + parameters.getStartDate();
                ips.stream().forEach(ip -> {
                    flaggedIpAddressService.saveFlaggedIpAddress(ip, note);
                    System.out.println(ip);
                });
                System.out.println("");

            } else {

                System.out.println("\nThere were no flagged IP addresses given the supplied search parameters.\n");

            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.exit(0);

    }

}
