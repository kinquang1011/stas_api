package vng.ge.stats.ub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import vng.ge.stats.ub.repository.UserProfileRepository;

@SpringBootApplication
@EnableAsync
public class ServerapiApplication{
	@Autowired
	private UserProfileRepository userProfileRepository;
	public static void main(String[] args) {
		SpringApplication.run(ServerapiApplication.class, args);
	}
}
