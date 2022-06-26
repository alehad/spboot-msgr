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

@SpringBootTest(classes={msgr.app.MessengerAppServer.class})
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.test.properties")
class SimpleDbStoreTest {
	
	@Autowired
	private SimpleDbStore simpleDb;

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
	void testGetMessages() {
		assertEquals(simpleDb.getMessages().size(), 2, "Basic implementation has 2 entries");
	}
	
	@Test
	void testGetMessage() {
		assertEquals(simpleDb.getMessage(0).getAuthor(), "auth1", "Get Message 1, assert on author field");
		assertEquals(simpleDb.getMessage(1).getMessage(), "msg2", "Get Message 2, assert on message content");
	}	
 }
