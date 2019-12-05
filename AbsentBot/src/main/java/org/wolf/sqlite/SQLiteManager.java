package org.wolf.sqlite;

import java.util.ArrayList;
import java.util.Comparator;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import org.wolf.sqlite.entities.AbsentEntry;
import org.wolf.sqlite.entities.MessageBuffer;

public class SQLiteManager {
	private static final String DB_PATH = "";
	private static final String DB_NAME = "absent.db";
	
	private static final String CONNECTION_STRING = "jdbc:sqlite:" + DB_PATH + DB_NAME;

	private static final String GET_ABSENTS_BY_MENTION = "select * from absents where invalid = 0 and member = ?";
	private static final String INSERT_ABSENT = "insert into absents(member, from_date, to_date, invalid) values (?,?,?,?)";
	private static final String UPDATE_ABSENT = "update absents set member = ?, from_date = ?, to_date = ?, invalid = ? where id = ?";
	private static final String DELETE_ABSENT = "delete absents where id = ?";
	

	private static final String GET_MESSAGE_BUFFER_BY_MENTION = "select * from message_buffer where member = ? and reaction = 0";
	private static final String GET_MESSAGE_BUFFER_BY_MESSAGE_ID = "select * from message_buffer where message_id = ? and reaction = 0";
	private static final String INSERT_MESSAGE_BUFFER = "insert into message_buffer(member, message_id, reaction, created_at, from_date, to_date) values(?,?,?,?,?,?)";
	private static final String UPDATE_MESSAGE_BUFFER = "update message_buffer set member = ?, message_id = ?, reaction = ?, created_at = ?, from_date = ?, to_date = ? where id = ?";
	//private static final String DELETE_MESSAGE_BUFFER = "delete message_buffer where id = ?";
	
	
	//private static final boolean DEBUG = true;
	
	private static SQLiteManager singleton;
	private SQLiteManager() {
		super();
	}
	
	public static SQLiteManager singleton() {
		if (singleton == null) singleton = new SQLiteManager();
		return singleton;
	}
	
	public void createNewDB (){
		try {
			Connection connection = DriverManager.getConnection(CONNECTION_STRING);
			if (connection != null){
				DatabaseMetaData metadata = connection.getMetaData();
				System.out.println("Created new DB");
				System.out.println("Driver: " + metadata.getDriverName());
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	
	public void setupSchema(){
		
		String createStmt = "CREATE TABLE IF NOT EXISTS absents (\n" 
							+ "	id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
							+ " member TEXT NOT NULL,\n"
							+ " from_date TEXT NOT NULL,\n"
							+ " to_date TEXT NOT NULL,\n"
							+ " invalid INTEGER DEFAULT 0\n"
							+ ");\n";
							
		String createStmt2 = "CREATE TABLE IF NOT EXISTS message_buffer (\n" 
							+ "	id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
							+ " member TEXT NOT NULL,\n"
							+ " message_id TEXT NOT NULL,\n"
							+ " reaction INTEGER NOT NULL,\n"
							+ " created_at TEXT NOT NULL,\n"
							+ " from_date TEXT NOT NULL,\n"
							+ " to_date TEXT NOT NULL\n"
							+ ");\n";
		
		try {
			Connection connection = DriverManager.getConnection(CONNECTION_STRING);
			Statement stmt = connection.createStatement();
			stmt.execute(createStmt);
			stmt.execute(createStmt2);
			stmt.execute("delete from message_buffer");
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}	
		
	}
	
	private Connection connect(){
		Connection connection = null;
		
		try {
			connection = DriverManager.getConnection(CONNECTION_STRING);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return connection;
	}
	
	public ArrayList<MessageBuffer> getMessageBufferEntriesForMember(String memberMention){
		ArrayList<MessageBuffer> results = new ArrayList<>();
		
		Connection connection = null;
		try{
			connection = this.connect();
			PreparedStatement pstmt = connection.prepareStatement(GET_MESSAGE_BUFFER_BY_MENTION);
			pstmt.setString(1, memberMention);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()){
				results.add(new MessageBuffer(	rs.getInt("id"),
												rs.getString("member"),
												rs.getString("message_id"),
												rs.getInt("reaction"),
												rs.getString("created_at"), 
												rs.getString("from_date"),
												rs.getString("to_date")));
			}
			
		} catch (Exception e) {
			e.printStackTrace();// TODO: handle exception
			System.out.println(e.getMessage());
		}
		
		return results;
	}
	
	public ArrayList<MessageBuffer> getMessageBufferEntriesForMessage(String messageId){
		ArrayList<MessageBuffer> results = new ArrayList<>();
		
		Connection connection = null;
		try{
			connection = this.connect();
			PreparedStatement pstmt = connection.prepareStatement(GET_MESSAGE_BUFFER_BY_MESSAGE_ID);
			pstmt.setString(1, messageId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()){
				results.add(new MessageBuffer(	rs.getInt("id"),
												rs.getString("member"),
												rs.getString("message_id"),
												rs.getInt("reaction"),
												rs.getString("created_at"), 
												rs.getString("from_date"),
												rs.getString("to_date")));
			}
			
		} catch (Exception e) {
			e.printStackTrace();// TODO: handle exception
			System.out.println(e.getMessage());
		}
		
		return results;
	}
	
	public void persistMessageBufferEntries(ArrayList<MessageBuffer> entries){

		Connection connection = this.connect();
		
		for (MessageBuffer entry : entries){
			try {
				PreparedStatement pstmt = null;
				boolean create = entry.getId() == 0;
				if (create){
					pstmt = connection.prepareStatement(INSERT_MESSAGE_BUFFER);
				}
				else {
					pstmt = connection.prepareStatement(UPDATE_MESSAGE_BUFFER);
				} 
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				SimpleDateFormat sdfTs = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
				pstmt.setString(1, entry.getMemberMention());
				pstmt.setString(2, entry.getMessageId());
				pstmt.setInt(3, entry.getReaction());
				pstmt.setString(4, sdfTs.format(entry.getCreatedAt()));
				pstmt.setString(5, sdf.format(entry.getFromDate()));
				pstmt.setString(6, sdf.format(entry.getToDate()));
				
				if (!create){
					pstmt.setInt(7, entry.getId());
				}
				
				pstmt.executeUpdate();
				
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public ArrayList<AbsentEntry> getAbsentEntriesForMember(String memberMention){
		ArrayList<AbsentEntry> results = new ArrayList<>();

		Connection connection = null;
		try {
			connection = this.connect();
			PreparedStatement pstmt = connection.prepareStatement(GET_ABSENTS_BY_MENTION);
			pstmt.setString(1, memberMention);
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()){
				results.add(new AbsentEntry(rs.getInt("id"),
											rs.getString("member"), 
											rs.getString("from_date"),
											rs.getString("to_date"),
											rs.getInt("invalid")));
			}
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			
		}
		
		return results;
	}
	
	public void persistAbsentEntries(ArrayList<AbsentEntry> entries){
		
		Connection connection = this.connect();
		
		for (AbsentEntry entry : entries){
			try {
				PreparedStatement pstmt = null;
				boolean create = entry.getId() == 0;
				if (create){
					pstmt = connection.prepareStatement(INSERT_ABSENT);
				}
				else {
					pstmt = connection.prepareStatement(UPDATE_ABSENT);
				}
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				pstmt.setString(1, entry.getMemberMention());
				pstmt.setString(2, sdf.format(entry.getFromDate()));
				pstmt.setString(3, sdf.format(entry.getToDate()));
				pstmt.setInt(4, (entry.isInvalid() ? 1:0));
				
				if (!create){
					pstmt.setInt(5, entry.getId());
				}
				
				pstmt.executeUpdate();
				
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
	}
	
	public void deleteAbsentEntries(ArrayList<AbsentEntry> entries){
		
		Connection connection = this.connect();
		
		for (AbsentEntry entry : entries){
			if (entry.getId()!=0){
				try {
					PreparedStatement pstmt = connection.prepareStatement(DELETE_ABSENT);
					pstmt.setInt(1, entry.getId());
					pstmt.executeUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}
		}
	}
	
	public static ArrayList<AbsentEntry> consolidateEntries(ArrayList<AbsentEntry> entries){
		

		ArrayList<AbsentEntry> results = new ArrayList<AbsentEntry>();
		ArrayList<AbsentEntry> valids = new ArrayList<AbsentEntry>();
		ArrayList<AbsentEntry> invalids = new ArrayList<AbsentEntry>();
		
		
		for (AbsentEntry entry : entries) {
			if (entry.isInvalid()) invalids.add(entry);
			else valids.add(entry);
		}
		valids.sort(new Comparator<AbsentEntry>() {
			@Override
			public int compare(AbsentEntry a, AbsentEntry b) {
				return a.compareTo(b);
			}
		});
		
		if (valids.size()<2) return entries;
		
		
		boolean consolidated = false;
		AbsentEntry first = valids.get(0);
		AbsentEntry seccond = null;
		
		for (int i = 1; i < valids.size(); i++) {
			seccond = valids.get(i);
			if (first.overlaps(seccond)) {
				AbsentEntry e = first.consolidateWith(seccond);
				results.add(first);
				results.add(seccond);
				if (i == valids.size() -1) results.add(e);
				first = e;
				consolidated = true;
			}
			else {
				results.add(first);
				first = seccond;
				if (i == valids.size()-1) results.add(seccond);
			}

		}
		results.addAll(invalids);
		if (consolidated) return consolidateEntries(results);
		
		return results;
	}
	
	
}
