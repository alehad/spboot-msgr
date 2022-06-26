package msgr.db;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import msgr.msg.Message;

@SpringBootTest(classes={msgr.app.MessengerAppServer.class})
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.test.properties")
class MongoDbStoreTest {
	
	@Autowired
	private MongoDbStore mongoDb;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() {
		mongoDb.deleteAll();
		
		assertEquals(mongoDb.getMessages().size(), 0, "empty mongo db before tests");
		
		System.out.println("load mongo db with 100 messages");

		for (int i = 0; i < 100; i++) {
			mongoDb.createMessage(new Message("msg " + (i + 1), "auth1"));
		}

		assertEquals(mongoDb.getMessages().size(), 100, "100 messages in mongo");
		
		assertEquals(mongoDb.getMessage(1).getMessage(), "msg 1", "got msg 1");
		
		assertEquals(mongoDb.getMessagesBy("auth1").size(), 100, "got 100 messages by auth1");
		
		mongoDb.updateMessage(1, new Message("new message 1", "auth2"));
		
		assertEquals(mongoDb.getMessage(1).getMessage(), "new message 1", "got updated msg 1");
		
		assertEquals(mongoDb.getMessagesBy("auth2").size(), 1, "got 1 messages by auth2");
		
		mongoDb.deleteMessagesBy("auth2");

		assertEquals(mongoDb.getMessages().size(), 99, "99 messages in mongo after delete by auth2");
		
		assertEquals(mongoDb.getMessagesBy("auth1").size(), 99, "got 100 messages by auth1");

		assertEquals(mongoDb.getMessagesBy("auth2").size(), 0, "no messages by auth2 after delete");

		mongoDb.deleteMessagesBy("auth1");

		assertEquals(mongoDb.getMessages().size(), 0, "empty mongo db after tests");
		
		System.out.println("deleted all messages from mongo db");
	}
}
