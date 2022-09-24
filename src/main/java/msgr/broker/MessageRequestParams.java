package msgr.broker;

import msgr.msg.Message;

public class MessageRequestParams {
	
	// any of the below can be null/-1, depending on the request
	private int 	findById;
	private String  findByAuthor;
	
	private Message messagePayload;

	public MessageRequestParams(int byId, String byAuthor, Message msg) {
		this.setFindById(byId);
		this.setFindByAuthor(byAuthor);
		this.setMessagePayload(msg);
	}
	
	public MessageRequestParams() {
		this.setFindById(-1);
		this.setFindByAuthor("");
		this.setMessagePayload(null);
	}
	
	public MessageRequestParams(Message msg) {
		this.setFindById(-1);
		this.setFindByAuthor("");
		this.setMessagePayload(msg);
	}

	public MessageRequestParams(int byId, Message msg) {
		this.setFindById(byId);
		this.setFindByAuthor("");
		this.setMessagePayload(msg);
	}
	
	public MessageRequestParams(String byAuthor, Message msg) {
		this.setFindById(-1);
		this.setFindByAuthor(byAuthor);
		this.setMessagePayload(msg);
	}

	public MessageRequestParams(int byId) {
		this.setFindById(byId);
		this.setFindByAuthor("");
		this.setMessagePayload(null);
	}

	public MessageRequestParams(String byAuthor) {
		this.setFindById(-1);
		this.setFindByAuthor(byAuthor);
		this.setMessagePayload(null);
	}

	public int getFindById() {
		return findById;
	}

	public void setFindById(int findById) {
		this.findById = findById;
	}

	public String getFindByAuthor() {
		return findByAuthor;
	}

	public void setFindByAuthor(String findByAuthor) {
		this.findByAuthor = findByAuthor;
	}

	public Message getMessagePayload() {
		return messagePayload;
	}

	public void setMessagePayload(Message messagePayload) {
		this.messagePayload = messagePayload;
	}
}
