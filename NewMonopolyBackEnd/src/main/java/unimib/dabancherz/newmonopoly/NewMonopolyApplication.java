package unimib.daBancherz.NewMonopoly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"unimib.daBancherz.NewMonopoly", "unimib.daBancherz.NewMonopoly.database.repository", "unimib.daBancherz.NewMonopoly.database.entity", "unimib.daBancherz.NewMonopoly.database.service", "unimib.daBancherz.NewMonopoly.handler"})
public class NewMonopolyApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewMonopolyApplication.class, args);
	}

}
