package org.wolf.sqlite.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AbsentEntry implements Comparable<AbsentEntry>{
	private int id;
	private String memberMention;
	private Date fromDate;
	private Date toDate;	
	private boolean invalid;
	
	public AbsentEntry(String memberMention, Date fromDate, Date toDate, boolean invalid) {
		super();
		this.id = 0;
		this.memberMention = memberMention;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.invalid = invalid;
	}
	
	public AbsentEntry(int id, String memberMention, String fromDate, String toDate, int invalid) throws ParseException {
		super();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		this.id = id;
		this.memberMention = memberMention;
		this.fromDate = sdf.parse(fromDate);
		this.toDate = sdf.parse(toDate);
		this.invalid = invalid > 0;
	}
	
	public int getId() {
		return id;
	}
	public String getMemberMention() {
		return memberMention;
	}
	public void setMemberMention(String memberMention) {
		this.memberMention = memberMention;
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
	public boolean isInvalid() {
		return invalid;
	}
	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}
	@Override
	public String toString (){
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		
		return "AbsentEntry " + id + "\nId: " + id + "\nMember: " + memberMention + "\nFrom: " + sdf.format(fromDate) + "\nTo: " + sdf.format(toDate);
	}

	@Override
	public int compareTo(AbsentEntry o) {
		
		return this.getFromDate().compareTo(o.getFromDate());
	}
	
	public boolean containsDate (Date date) {
		return (this.fromDate.compareTo(date) < 1 && this.toDate.compareTo(date) > -1);
	}
	
	public boolean overlaps (AbsentEntry o) {
		boolean result =((this.containsDate(o.getFromDate()) || (this.containsDate(o.getToDate()))) && !this.invalid && !o.isInvalid()); 
		return result;
	}
	
	public boolean contains(AbsentEntry o) {
		return ((this.containsDate(o.getFromDate()) && (this.containsDate(o.getToDate())))&& !this.invalid && !o.isInvalid());
	}
	
	public AbsentEntry consolidateWith(AbsentEntry o) {
		if (!this.overlaps(o)) return null;
		if (this.contains(o)) {
			this.setInvalid(true);
			o.setInvalid(true);
			return new AbsentEntry(this.memberMention, fromDate, toDate, false);
		}
		else if (o.contains(this)) {
			this.setInvalid(true);
			o.setInvalid(true);
			return new AbsentEntry(o.memberMention, o.getFromDate(), o.getToDate(), false);
		}
		else {
			Date start = (this.fromDate.compareTo(o.getFromDate()) < 1 ? this.fromDate : o.getFromDate());
			Date end = (this.toDate.compareTo(o.getToDate()) > -1 ? this.toDate : o.toDate);
			AbsentEntry consolidated = new AbsentEntry(this.memberMention, start, end, false);
			
			this.setInvalid(true);
			o.setInvalid(true);
			
			return consolidated;
		}
	}
}
