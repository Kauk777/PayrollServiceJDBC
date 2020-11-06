package com.bridgelabz.JDBCDemo;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

public class EmployeePayrollData {
	public int id;
	public String name;
	public String gender;
	public double salary;
	public LocalDate startDate;

	public EmployeePayrollData(int employeeId, String employeeName, double employeeSalary) {
		this.id = employeeId;
		this.name = employeeName;
		this.salary = employeeSalary;
	}

	public EmployeePayrollData(int employeeId, String employeeName, double employeeSalary, LocalDate start) {
		this(employeeId, employeeName, employeeSalary);
		this.startDate = start;
	}
	
	public EmployeePayrollData(int employeeId, String employeeName, String gender, double employeeSalary, LocalDate start) {
		this(employeeId, employeeName, employeeSalary);
		this.gender=gender;
		this.startDate = start;
	}

	

	public EmployeePayrollData(String name, double salary) {
		this.name=name;
		this.salary=salary;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(salary);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmployeePayrollData other = (EmployeePayrollData) obj;
		if (Double.doubleToLongBits(salary) != Double.doubleToLongBits(other.salary))
			return false;
		if (gender == null) {
			if (other.gender != null)
				return false;
		} else if (!gender.equals(other.gender))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EmployeePayrollData [id=" + id + ", name=" + name + ", gender=" + gender + ", salary=" + salary
				+ ", start=" + startDate + "]";
	}
	
	

	

}
