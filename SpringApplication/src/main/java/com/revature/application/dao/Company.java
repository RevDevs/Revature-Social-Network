package com.revature.application.dao;

import java.util.Set;

import javax.persistence.*;

@Entity
public class Company {

	@Id
	private long companyId;

	@ManyToOne
	@JoinColumn(name = "locationId")
	private Location location;

	@Column
	private String companyName;

	@OneToMany(cascade = { CascadeType.ALL })
	@JoinColumn(name = "companyId")
	private Set<Employee> employees;

	public Company() {
	}

	public Company(Location location, String companyName) {
		super();
		this.location = location;
		this.companyName = companyName;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Set<Employee> getEmployees() {
		return employees;
	}

	public void setEmployees(Set<Employee> employees) {
		this.employees = employees;
	}

}
