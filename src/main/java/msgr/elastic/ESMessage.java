package msgr.elastic;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
//import org.springframework.data.elasticsearch.annotations.Field;
//import org.springframework.data.elasticsearch.annotations.FieldType;

import msgr.msg.Message;
import msgr.msg.StoredMessage;

@Document(indexName = "esmessageidx")
public class ESMessage extends StoredMessage {

    @Id
    private String id;

    /*
    //@Field(type = FieldType.Text)
    private String message;
    
    //@Field(type = FieldType.Text)
	private String author;
	*/
    
	public ESMessage() {
		//no-op default constructor
	}
	
	public ESMessage(int msgId, Message msg) {
		super(msgId, msg);
	}

	public ESMessage(int msgId, String message, String author) {
		super(msgId, message, author);
	}

	public ESMessage(StoredMessage msg) {
		super(msg.getMessageId(), msg);
	}
	
	/*
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	*/
}
