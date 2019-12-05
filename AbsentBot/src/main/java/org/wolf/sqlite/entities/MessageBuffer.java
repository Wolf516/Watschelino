package org.wolf.sqlite.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageBuffer {
/* "CREATE TABLE IF NOT EXISTS absents (\n" 
							+ "	id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
							+ " member TEXT NOT NULL,\n"
							+ " message_id TEXT NOT NULL,\n"
							+ " reaction INTEGER NOT NULL,\n"
							+ " created_at TEXT NOT NULL,\n"
							+ " from_date TEXT NOT NULL,\n"
							+ " to_date TEXT NOT NULL,\n"
							+ ");\n";*/
	private int id;
	private String memberMention;
	private String messageId;
	private int reaction;
	private Date createdAt;
	private Date fromDate;
	private Date toDate;
	
	public MessageBuffer(String memberMention, String messageId, int reaction, Date createdAt, Date fromDate,
			Date toDate) {
		super();
		this.id = 0;
		this.memberMention = memberMention;
		this.messageId = messageId;
		this.reaction = reaction;
		this.createdAt = createdAt;
		this.fromDate = fromDate;
		this.toDate = toDate;
	}
	
	public MessageBuffer(int id, String memberMention, String messageId, int reaction, String createdAt, String fromDate,
			String toDate) throws ParseException {
		super();
		this.id = id;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdfTs = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		this.memberMention = memberMention;
		this.messageId = messageId;
		this.reaction = reaction;
		this.createdAt = sdfTs.parse(createdAt);
		this.fromDate = sdf.parse(fromDate);
		this.toDate = sdf.parse(toDate);
	}

	public String getMemberMention() {
		return memberMention;
	}

	public void setMemberMention(String memberMention) {
		this.memberMention = memberMention;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public int getReaction() {
		return reaction;
	}

	public void setReaction(int reaction) {
		this.reaction = reaction;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public int getId() {
		return id;
	}
	
	@Override
	public String toString(){
		return "Message Buffer Entry "+id+"\nId: " + id + "\nMember: " + memberMention + "\nMessage Id: " + messageId + "\nReaction: " + reaction + "\nCreated at: " + createdAt + "\nFrom date: " + fromDate + "\nTo date: " + toDate;
	}
	
	
}
