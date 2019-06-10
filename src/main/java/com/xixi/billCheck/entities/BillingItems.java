package com.xixi.billCheck.entities;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Ming
 *	每条笔记的对象，用于标准化对比条目
 *
 */
public class BillingItems implements Comparable<BillingItems> {

	private static SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
	
	private int hash;
	//该条目消费的值
	private double amount;
	
	//该条目时间
	private Date spendDate;
	
	//条目唯一值 Date + serialId
	private String uuid;
	
	//条目内容
	private String rowContent;
	
	//账本类型 0：账单，1：记账账本
	private int billingType;

	
	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getSpendDate() {
		return spendDate;
	}

	public void setSpendDate(Date spendDate) {
		this.spendDate = spendDate;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getRowContent() {
		return rowContent;
	}

	public void setRowContent(String rowContent) {
		this.rowContent = rowContent;
	}

	@Override
	public int compareTo(BillingItems paramT) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public int hashCode(){
		if(hash==0){
			hash = Integer.parseInt(yyyyMMdd.format(spendDate)) + (int)(this.amount*100);	
		}
		return hash;
	}
	
	public void incrementHash(){
		hash = hash + 1;
	}
	
	
	public boolean equals(Object obj) {
		return (this.hash == obj.hashCode());
	}

	public int getBillingType() {
		return billingType;
	}

	public void setBillingType(int billingType) {
		this.billingType = billingType;
	}
}
