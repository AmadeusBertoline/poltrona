package poltrona;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching 
public class PoltronaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PoltronaApplication.class, args);

	}

}
