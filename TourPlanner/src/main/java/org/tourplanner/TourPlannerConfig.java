package org.tourplanner;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.tourplanner.config.ReportProperties;

@SpringBootApplication
@EnableConfigurationProperties(ReportProperties.class)
public class TourPlannerConfig {

}
