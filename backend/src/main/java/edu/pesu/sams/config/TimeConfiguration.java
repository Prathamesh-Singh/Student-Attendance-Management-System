package edu.pesu.sams.config;
import org.springframework.context.annotation.*;
import java.time.*;
@Configuration
public class TimeConfiguration {
 @Bean public Clock samsClock(){return Clock.system(ZoneId.of("Asia/Kolkata"));}
}
