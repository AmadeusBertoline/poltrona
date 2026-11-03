package poltrona;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import java.time.ZoneId;
import java.util.TimeZone;

@SpringBootApplication
@EnableCaching 
public class PoltronaApplication {

	public static void main(String[] args) {
		String timeZone = System.getProperty(
				"app.time-zone",
				System.getenv().getOrDefault("APP_TIME_ZONE", "America/Sao_Paulo"));
		TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of(timeZone)));
		SpringApplication.run(PoltronaApplication.class, args);

	}

}
