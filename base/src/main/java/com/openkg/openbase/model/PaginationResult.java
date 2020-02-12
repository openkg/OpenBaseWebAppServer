package com.openkg.openbase.model;

import java.util.List;

public class PaginationResult<T> {
	private List<T> list;
	private Page page;

	public PaginationResult(List<T> list, Page page) {
		super();
		this.list = list;
		this.page = page;
	}

	public PaginationResult() {
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

}
