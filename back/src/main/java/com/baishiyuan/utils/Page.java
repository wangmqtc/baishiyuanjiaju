package com.baishiyuan.utils;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @ClassName:  Page
 * @Description:分页
 * @author:     zhiyongh
 * @date:       2015-4-25 下午5:16:34
 *
 */
public class Page implements Serializable {

	private static final long serialVersionUID = -4867907781345404384L;
	/**当前页数*/
	private int pageNo = 1; 
	/**页大小*/
	private int pageSize = 10; 
	/**总的记录条数*/
	private int totalCount;
	/**总的页数*/
	private int totalPage;

	@SuppressWarnings("rawtypes")
	private List list = null;
		
	/**
	 * 通过构造函数 传入  总记录数  和  当前页
	 * @param totalCount
	 * @param pageNo
	 */
	public Page(int totalCount, int pageNo, int pageSize) {
		this.totalCount = totalCount;
		this.pageNo = pageNo;
		this.pageSize = pageSize;		
	}
	
	/**
	 * 取得总页数，总页数=总记录数/总页数
	 * @return
	 */
	public int getTotalPage() {
		totalPage = getTotalCount() / getPageSize();
		return (totalCount % pageSize == 0) ? totalPage
				: totalPage + 1;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	/**
	 * 取得选择记录的初始位置
	 * @return
	 */
	public int getStartPos() {
		return (pageNo - 1) * pageSize;
	}

	@SuppressWarnings("rawtypes")
	public List getList() {
		return list;
	}

	@SuppressWarnings("rawtypes")
	public void setList(List list) {
		this.list = list;
	}


}
