package msgr.elastic;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import msgr.msg.Message;

@Document(indexName = "esmessageidx")
public class ESMessage {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String message;
    
    @Field(type = FieldType.Text)
	private String author;
	
	public ESMessage() {
		//no-op default constructor
	}
	
	public ESMessage(Message msg) {
		this.message = msg.getMessage();
		this.author  = msg.getAuthor();
	}

	public ESMessage(String message, String author) {
		this.message = message;
		this.author  = author;
	}

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
}
