package msgr.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/*
 * ** IMPORTANT **
 * Spring Boot will only scan:
 * a. the package where the class annotated with @SpringBootApplication is located, and
 * b. all the sub packages beneath that one
 * if the app has flat package structure than you need to specify component scan path
 * but in that case you also need to include the package where the rest controller is found
 * otherwise you will get 404 error, as spring will not be able to wire the rest controller 
 */
@SpringBootApplication
@ComponentScan(basePackages = {"msgr.init", "msgr.db", "msgr.svc", "msgr.broker", "msgr.app"})
public class MessengerAppServer {

	public static void main(String[] args) {
		SpringApplication.run(MessengerAppServer.class, args);

	}

}
