package ee.insa.crmapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CrmAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrmAppApplication.class, args);
	}

}
