package com.bridgelabz.JDBCDemo;

import java.time.LocalDate;
import java.util.Arrays;

public class EmployeePayrollData {
	private int employeeId;
	public String employeeName;
	public double employeeSalary;
	private LocalDate start;
	private int companyID;
	private String[] department;

	public EmployeePayrollData(int employeeId, String employeeName, double employeeSalary) {
		this.employeeId = employeeId;
		this.employeeName = employeeName;
		this.employeeSalary = employeeSalary;
	}

	public EmployeePayrollData(int employeeId, String employeeName, double employeeSalary, LocalDate start) {
		this(employeeId, employeeName, employeeSalary);
		this.start = start;
	}

	public EmployeePayrollData(int employeeId, String employeeName, double employeeSalary, LocalDate start,
			int companyID, String[] department) {
		this(employeeId, employeeName, employeeSalary, start);
		this.companyID = companyID;
		this.department = department;
	}

	@Override
	public String toString() {
		return "EmployeePayrollData [employeeId=" + employeeId + ", employeeName=" + employeeName + ", employeeSalary="
				+ employeeSalary + ", start=" + start + ", companyID=" + companyID + ", department="
				+ Arrays.toString(department) + "]";
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
		if (employeeId != other.employeeId)
			return false;
		if (employeeName == null) {
			if (other.employeeName != null)
				return false;
		} else if (!employeeName.equals(other.employeeName))
			return false;
		if (Double.doubleToLongBits(employeeSalary) != Double.doubleToLongBits(other.employeeSalary))
			return false;
		return true;
	}

}
