package fr.cargo.tms;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAdminServer
public class TMSSbAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(TMSSbAdminApplication.class, args);
	}

}
