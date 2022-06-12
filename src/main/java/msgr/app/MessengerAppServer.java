package msgr.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"msgr.svc", "msgr.app"})
//@ComponentScan({"msgr.db"})
public class MessengerAppServer {

	public static void main(String[] args) {
		SpringApplication.run(MessengerAppServer.class, args);

	}

}
