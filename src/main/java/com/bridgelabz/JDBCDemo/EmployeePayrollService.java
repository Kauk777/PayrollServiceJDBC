package com.bridgelabz.JDBCDemo;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	public List<EmployeePayrollData> readEmployeePayrollDataByDate(IOService ioService, String startDate)
			throws EmployeePayrollDataException {
		if (ioService.equals(IOService.DB_IO))
			employeePayrollList = employeePayrollDBService.readDataByDate(startDate);
		return employeePayrollList;
	}

	public int readTotalSalary(IOService ioService, String gender) throws EmployeePayrollDataException {
		int totalSalary = 0;
		if (ioService.equals(IOService.DB_IO))
			totalSalary = employeePayrollDBService.readTotalSalary(gender);
		return totalSalary;
	}

	public void updateEmployeeSalary(String name, double salary) throws EmployeePayrollDataException {
		int result = employeePayrollDBService.updateEmployeeData(name, salary);
		if (result == 0)
			throw new EmployeePayrollDataException("No Updation Performed",
					EmployeePayrollDataException.ExceptionType.UNABLE_TO_UPDATE);
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		if (employeePayrollData != null)
			employeePayrollData.employeeSalary = salary;

	}
	
	public void updateEmployeeSalariesWithThread(List<EmployeePayrollData> empList) {
		Map<Integer, Boolean> employeeAdditionStatus = new HashMap<>();
		empList.forEach( empData-> { 
			Runnable task = () -> { 
				try {
					employeeAdditionStatus.put(empData.hashCode(), false);
					System.out.println("Employee being updated: " + Thread.currentThread().getName());
					this.updateEmployeeSalary(empData.employeeName, empData.employeeSalary);
				    employeeAdditionStatus.put(empData.hashCode(), true);
				    System.out.println("Employee updated: " + Thread.currentThread().getName());
				} catch (EmployeePayrollDataException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
			Thread thread = new Thread(task, empData.employeeName);
			thread.start();
			
		});
		while (employeeAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
				
	}

	public void addEmployeePayrollData(String name, double salary, LocalDate startDate, String gender)
			throws EmployeePayrollDataException {
		employeePayrollList.add(employeePayrollDBService.addEmployeePayrollData(name, salary, startDate, gender));

	}

	public void addEmployeesPayrollData(List<EmployeePayrollData> empList) {
		empList.forEach(empData -> {
			// System.out.println("Employee Being Added: "+empData.employeeName);
			try {
				this.addEmployeePayrollData(empData.employeeName, empData.employeeSalary, empData.start,
						empData.gender);
			} catch (EmployeePayrollDataException e) {
				e.printStackTrace();
			}
			// System.out.println("Employee added: "+empData.employeeName);
		});
		// System.out.println(this.employeePayrollList);
	}

	public void addEmployeesPayrollDataWithThreads(List<EmployeePayrollData> empList) {
		Map<Integer, Boolean> employeeAdditionStatus = new HashMap<>();
		empList.forEach(empData -> {
			Runnable task = () -> {
				try {
					employeeAdditionStatus.put(empData.hashCode(), false);
					System.out.println("Employee being added: " + Thread.currentThread().getName());
					this.addEmployeePayrollData(empData.employeeName, empData.employeeSalary, empData.start,
							empData.gender);
					employeeAdditionStatus.put(empData.hashCode(), true);
					System.out.println("Employee added: " + Thread.currentThread().getName());
				} catch (EmployeePayrollDataException e) {
					e.printStackTrace();
				}
			};
			Thread thread = new Thread(task, empData.employeeName);
			thread.start();
		});
		while (employeeAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println(this.employeePayrollList);

	}

	private EmployeePayrollData getEmployeePayrollData(String name) {
		return this.employeePayrollList.stream()
				.filter(employeePayrollDataItem -> employeePayrollDataItem.employeeName.equals(name)).findFirst()
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
		else
			System.out.println(employeePayrollList);

	}

	public long countEntries(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO))
			return new EmployeePayrollFileIOService().countEntries();
		return employeePayrollList.size();
	}

	

}
