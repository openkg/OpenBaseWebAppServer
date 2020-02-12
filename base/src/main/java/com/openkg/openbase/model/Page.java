package com.openkg.openbase.model;

import lombok.Data;


public class Page {
	private int pageSize;
	private int pageIndex;
	
	private long countTotal;
	
	public static final int DEFAULT_PAGE_SIZE = 20;
	
	public Page() {
		
	}

	public Page(int pageSize, int pageIndex, long countTotal) {
		super();
		this.pageSize = pageSize;
		this.pageIndex = pageIndex;
		this.countTotal = countTotal;
	}
	
	public int getOffset() {
		return (pageIndex - 1) * pageSize;
	}

	public void setCountTotal(int count) {
		this.countTotal = count;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public long getCountTotal() {
		return countTotal;
	}
	
}
