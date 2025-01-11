package unimib.daBancherz.NewMonopoly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"unimib.daBancherz.NewMonopoly", "unimib.daBancherz.NewMonopoly.Repository", "unimib.daBancherz.NewMonopoly.Entity", "unimib.daBancherz.NewMonopoly.Service", "unimib.daBancherz.NewMonopoly.Handler"})
public class NewMonopolyApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewMonopolyApplication.class, args);
	}

}
