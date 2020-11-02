package com.bridgelabz.JDBCDemo;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class EmployeePayrollService {

	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	private List<EmployeePayrollData> employeePayrollList;
	private EmployeePayrollDBService employeePayrollDBService;

	public EmployeePayrollService() {
		employeePayrollDBService = EmployeePayrollDBService.getInstance();
	}

	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
		this();
		this.employeePayrollList = employeePayrollList;

	}

	public static void main(String[] args) {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		Scanner consoleInputReader = new Scanner(System.in);
		employeePayrollService.readEmployeePayrollData(consoleInputReader);
		employeePayrollService.writeEmployeePayrollData(IOService.CONSOLE_IO);

	}

	private void readEmployeePayrollData(Scanner consoleInputReader) {
		System.out.println("Enter Employee ID: ");
		int id = consoleInputReader.nextInt();
		System.out.println("Enter Employee Name: ");
		String name = consoleInputReader.next();
		System.out.println("Enter Employee Salary: ");
		double salary = consoleInputReader.nextDouble();
		employeePayrollList.add(new EmployeePayrollData(id, name, salary));
	}

	public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) throws EmployeePayrollDataException {
		if (ioService.equals(IOService.DB_IO))
			this.employeePayrollList = employeePayrollDBService.readData();
		return this.employeePayrollList;
	}
	
	public List<EmployeePayrollData> readEmployeePayrollDataByDate(IOService ioService, String startDate) throws EmployeePayrollDataException {
		if (ioService.equals(IOService.DB_IO))
			this.employeePayrollList = employeePayrollDBService.readDataByDate(startDate);
		return this.employeePayrollList;
	}
	
	public int readTotalSalary(IOService ioService, String gender) throws EmployeePayrollDataException {
		int totalSalary=0;
		if (ioService.equals(IOService.DB_IO))
			totalSalary=employeePayrollDBService.readTotalSalary(gender);
		return totalSalary;
	}

	public void updateEmployeeSalary(String name, double salary) throws EmployeePayrollDataException {
		int result = employeePayrollDBService.updateEmployeeData(name, salary);
		if (result == 0)
			throw new EmployeePayrollDataException("No Updation Performed",EmployeePayrollDataException.ExceptionType.UNABLE_TO_UPDATE);
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		if (employeePayrollData != null)
			employeePayrollData.employeeSalary = salary;

	}
	
    public void addEmployeePayrollData(String name, double salary, LocalDate startDate, String gender, int companyId, String[] department, String companyName) throws EmployeePayrollDataException {
		employeePayrollList.add(employeePayrollDBService.addEmployeePayrollData(name,salary,startDate,gender,companyId,department,companyName));
		
	}


	private EmployeePayrollData getEmployeePayrollData(String name) {
		return this.employeePayrollList.stream()
				.filter(employeePayrollDataItem -> employeePayrollDataItem.employeeName.equals(name))
				.findFirst()
				.orElse(null);
	}

	public boolean checkEmployeePayrollSyncWithDB(String name) throws EmployeePayrollDataException {
		List<EmployeePayrollData> employeePayrollList = employeePayrollDBService.getEmployeePayrollData(name);
		return employeePayrollList.get(0).equals(getEmployeePayrollData(name));
	}

	public void writeEmployeePayrollData(IOService ioService) {
		if (ioService.equals(IOService.CONSOLE_IO))
			System.out.println("\nWriting Employee Payroll to console\n" + employeePayrollList);
		else if (ioService.equals(IOService.FILE_IO))
			new EmployeePayrollFileIOService().writeData(employeePayrollList);

	}

	public void printData(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO))
			new EmployeePayrollFileIOService().printData();

	}

	public long countEntries(IOService ioService) {
		long entries = 0;
		if (ioService.equals(IOService.FILE_IO))
			entries = new EmployeePayrollFileIOService().countEntries();
		return entries;
	}

	
	

}
