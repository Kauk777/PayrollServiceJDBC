package com.bridgelabz.JDBCDemo;

@SuppressWarnings("serial")
public class EmployeePayrollDataException extends Exception {

	enum ExceptionType {
		EMPLOYEEPAYROLL_DB_PROBLEM, UNABLE_TO_CONNECT
	}

	ExceptionType type;

	public EmployeePayrollDataException(String message, ExceptionType type) {
		super(message);
		this.type = type;

	}

}
