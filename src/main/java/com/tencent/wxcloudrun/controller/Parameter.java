package com.tencent.wxcloudrun.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 参数
 * 
 * @author little sheep
 */

public class Parameter implements java.io.Serializable {

	private static final long serialVersionUID = -3149051815003111978L;

	/**
	 * id
	 */
	private String id;
	/**
	 * 参数名称
	 */
	private String name;
	/**
	 * 编号
	 */
	private String code;
	/**
	 * 排序
	 */
	private Integer sort;
	/**
	 * 状态(-1停用,2有效)
	 */
	private Integer state;
	/**
	 * 父参数
	 */
	private Parameter parent;
	/**
	 * 子参数
	 */
	private Set<Parameter> children = new HashSet<Parameter>();
	/**
	 * 有效子参数
	 */
	private Set<Parameter> usingChildren = new HashSet<Parameter>();

	/** default constructor */
	public Parameter() {
		this.setId(UUID.randomUUID().toString());
	}

	/** minimal constructor */
	public Parameter(String id) {
		this.id = id;
	}

	public Parameter(String id, String name, String code, Integer sort, Integer state, Parameter parent,
			Set<Parameter> children, Set<Parameter> usingChildren) {
		super();
		this.id = id;
		this.name = name;
		this.code = code;
		this.sort = sort;
		this.state = state;
		this.parent = parent;
		this.children = children;
		this.usingChildren = usingChildren;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Parameter getParent() {
		return parent;
	}

	public void setParent(Parameter parent) {
		this.parent = parent;
	}

	public Set<Parameter> getChildren() {
		return children;
	}

	public void setChildren(Set<Parameter> children) {
		this.children = children;
	}

	public Set<Parameter> getUsingChildren() {
		return usingChildren;
	}

	public void setUsingChildren(Set<Parameter> usingChildren) {
		this.usingChildren = usingChildren;
	}
}