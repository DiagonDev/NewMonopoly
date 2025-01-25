package unimib.dabancherz.newmonopoly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"unimib.dabancherz.newmonopoly", "unimib.dabancherz.newmonopoly.database.repository", "unimib.dabancherz.newmonopoly.database.entity", "unimib.dabancherz.newmonopoly.database.service", "unimib.dabancherz.newmonopoly.handler"})
public class NewMonopolyApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewMonopolyApplication.class, args);
	}

}
