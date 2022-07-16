package msgr.svc;

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

import msgr.db.IMessageStore;
import msgr.msg.Message;

@SpringBootTest(classes={msgr.app.MessengerAppServer.class})
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.test.properties")
class MessageStoreServiceTest {
	
	@Autowired
	private MessageStoreService storeService;

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
		storeService.getRegisteredStores().forEach(store -> testStore(store));
	}

	private void testStore(IMessageStore store) {
		if (!store.isInitialized()) {
			store.initialize();
		}
		
		System.out.println("testing DbStore: " + store.getClass().getName());
		
		store.deleteAll();
		
		assertEquals(store.getMessages().size(), 0, "empty db store before tests");
		
		System.out.println("load store with 100 messages");

		for (int i = 0; i < 100; i++) {
			store.createMessage(new Message("msg " + (i + 1), "auth1"));
		}

		assertEquals(store.getMessages().size(), 100, "100 messages in store");
		
		assertEquals(store.getMessage(0).getMessage(), "msg 1", "got msg 1");
		
		assertEquals(store.getMessagesBy("auth1").size(), 100, "got 100 messages by auth1");
		
		store.updateMessage(0, new Message("new message 1", "auth2"));
		
		assertEquals(store.getMessage(0).getMessage(), "new message 1", "got updated msg 1");
		
		assertEquals(store.getMessagesBy("auth2").size(), 1, "got 1 messages by auth2");
		
		store.deleteMessagesBy("auth2");

		assertEquals(store.getMessages().size(), 99, "99 messages in store after delete by auth2");
		
		assertEquals(store.getMessagesBy("auth1").size(), 99, "got 99 messages by auth1");

		assertEquals(store.getMessagesBy("auth2").size(), 0, "no messages by auth2 after delete");

		store.deleteMessagesBy("auth1");

		assertEquals(store.getMessages().size(), 0, "empty db store after tests");
		
		System.out.println("deleted all messages from db store");

		System.out.println("completed db store test suite for " + store.getClass().getName());
	}
}
