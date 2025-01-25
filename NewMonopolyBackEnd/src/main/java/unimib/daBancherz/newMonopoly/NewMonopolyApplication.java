package unimib.daBancherz.newMonopoly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"unimib.daBancherz.newMonopoly", "unimib.daBancherz.newMonopoly.database.repository", "unimib.daBancherz.newMonopoly.database.entity", "unimib.daBancherz.newMonopoly.database.service", "unimib.daBancherz.newMonopoly.handler"})
public class NewMonopolyApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewMonopolyApplication.class, args);
	}

}
