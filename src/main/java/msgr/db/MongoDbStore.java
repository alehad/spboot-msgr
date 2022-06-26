package msgr.db;

import java.util.List;

import msgr.msg.Message;
import msgr.msg.StoredMessage;

import java.util.ArrayList;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

import com.mongodb.client.model.UpdateOptions;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

@Service
public class MongoDbStore implements IMessageStore {

	// for now we will statically initialize mongo db
	// once this gets containerized, we will update the initialization bit
	
	private static MongoClient mongodbClient;
	private static MongoDatabase mongodb;
	private static MongoCollection<Document> mongodbCollection;
	
	//private static String _mongodbHostName = "mongodb";
	private static String _mongodbName = "MessengerDBv2";
	private static String _mongodbCollectionName = "messages";
	
	private static String _msgId = "msgId";
	private static String _msg   = "msg";
	private static String _auth  = "author";

	@Autowired
	public MongoDbStore(@Value("${spring.data.mongodb.host:localhost}") String host, @Value("${spring.data.mongodb.port:27017}") String port) {
		// will need to update host/port once moving to K8
		// when running inside docker, the host name = name of mongo *service* or
		// compose.yaml has to have hostname: mongo-db specified for the service: mongo
		// this will create DNS name of mongo-db for the mongo running inside docker network
		mongodbClient = MongoClients.create("mongodb://" + host + ":" + port);

		mongodb = mongodbClient.getDatabase(_mongodbName); // if not present, Mongo will create it
		// do we need to authenticate?
		
		boolean createCollection = true;
		
		MongoIterable<String> collections = mongodb.listCollectionNames();
		
		while (collections.iterator().hasNext() && createCollection) {
			if (collections.iterator().next().equalsIgnoreCase(_mongodbCollectionName)) {
				createCollection = false;
			}
		}

		if (createCollection) {
			mongodb.createCollection(_mongodbCollectionName); // create a table -- in Mongo terms that is a Collection
			mongodbCollection = mongodb.getCollection(_mongodbCollectionName);
		}
		else {
			mongodbCollection = mongodb.getCollection(_mongodbCollectionName);
		}
	}
	
	@Override
	public List<Message> getMessages() {

		List<Message> messages = new ArrayList<Message>();
		
		FindIterable<Document> iterable = mongodbCollection.find();
		MongoCursor<Document> cursor = iterable.iterator();
		
		while (cursor.hasNext()) {
			Document doc = cursor.next();
			
			if (doc.containsKey(_msgId)) {
				messages.add(new StoredMessage(doc.getInteger(_msgId), doc.getString(_msg), doc.getString(_auth)));
			}
		}

		//Collections.sort(messages);

		return messages;
	}

	@Override
	public Message getMessage(int id) {
		Message  msg = null;
		Document doc = mongodbCollection.find(eq(_msgId, id)).first();

		if (doc != null) {
			msg = new StoredMessage(doc.getInteger(_msgId), doc.getString(_msg), doc.getString(_auth));
		}

		return msg;
	}

	@Override
	public Message createMessage(Message msg) {
		// check if message id already exists
		Document doc = new Document();
		int msgId = Math.toIntExact(mongodbCollection.countDocuments()) + 1;
		doc.put(_msgId, msgId); // potenital issue with deleted messages in the middle
		doc.put(_msg, msg.getMessage());
		doc.put(_auth, msg.getAuthor());
		try {
			mongodbCollection.insertOne(doc);
		}
		finally {
			// TODO: define exception to throw or how to indicate unsuccessful op 
		}
		return getMessage(msgId);
	}

	@Override
	public Message updateMessage(int id, Message msg) {
		Bson filter = eq(_msgId, id);
		Bson updateMsg = set(_msg, msg.getMessage());
		Bson updateAuth = set(_auth, msg.getAuthor());
		Bson update = combine(updateMsg, updateAuth);
		UpdateOptions options = new UpdateOptions().upsert(true); // this will insert new message if id not found
		mongodbCollection.updateOne(filter, update, options); // does not throw
		return getMessage(id);
	}

	@Override
	public void deleteMessage(int id) {
		Bson filter = eq(_msgId, id);
		mongodbCollection.deleteOne(filter);		
	}

	@Override
	public List<Message> getMessagesBy(String author) {
		List<Message> result = new ArrayList<Message>();
		Bson filter = eq(_auth, author);
		List<Document> docs = mongodbCollection.find(filter).into(new ArrayList<>());	
		for (Document doc : docs) {
			result.add(new StoredMessage(doc.getInteger(_msgId), doc.getString(_msg), doc.getString(_auth)));
		}
		return result;
	}

	@Override
	public Message updateMessageBy(String author, Message msg) {
		// this will update the first message found
		Bson filter = eq(_auth, author);
		Document doc = mongodbCollection.find(filter).first();
		return doc != null ? updateMessage(doc.getInteger(_msgId), msg) : null;
	}

	@Override
	public void deleteMessagesBy(String author) {
		Bson filter = eq(_auth, author);
		mongodbCollection.deleteMany(filter);		
	}

	@Override
	public void deleteAll() {
		mongodbCollection.deleteMany(new Document());
	}
	
}
