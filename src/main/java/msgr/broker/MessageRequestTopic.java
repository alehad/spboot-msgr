package msgr.broker;

public enum MessageRequestTopic {

	GetAllMessages   ("alehad.messenger.topic.getall"),
	GetAllMessagesBy ("alehad.messenger.topic.getallby"),
	GetOneMessage    ("alehad.messenger.topic.getone"),
	AddOneMessage    ("alehad.messenger.topic.addone"),
	UpdateMessage    ("alehad.messenger.topic.update"),
	UpdateMessageBy  ("alehad.messenger.topic.updateby"),
	DeleteMessage    ("alehad.messenger.topic.delete"),
	DeleteMessagesBy ("alehad.messenger.topic.deleteby"),
	DeleteAllMessages("alehad.messenger.topic.deleteall");
	
	public final String topic;
	
	MessageRequestTopic(String topic) {
		this.topic = topic;
	}
}
