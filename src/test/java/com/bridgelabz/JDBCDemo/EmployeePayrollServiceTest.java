package com.bridgelabz.JDBCDemo;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import com.bridgelabz.JDBCDemo.EmployeePayrollService.IOService;

public class EmployeePayrollServiceTest 
{
	@Test
	public void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEntries() {
		EmployeePayrollData[] arrayOfEmps= {
				new EmployeePayrollData(1,"Equila Joe", 80000),
				new EmployeePayrollData(2,"Dee Holy", 70000),
				new EmployeePayrollData(3,"Fredich Karl", 90000)
		};
		EmployeePayrollService employeePayrollService;
		employeePayrollService=new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		employeePayrollService.writeEmployeePayrollData(IOService.FILE_IO);
		employeePayrollService.printData(IOService.FILE_IO);
		long entries=employeePayrollService.countEntries(IOService.FILE_IO);
		Assert.assertEquals(3, entries);
	}
	
	
	@Test
	public void givenEmployeePayrollsWhenRetrievedInDBShouldMatchEmployeeCount() throws EmployeePayrollDataException {
		EmployeePayrollService employeePayrollService=new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData=employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Assert.assertEquals(3, employeePayrollData.size());
		
	}
	
	@Test
	public void givenNewSalaryForEmployeeWhenUpdatedShouldMatch() throws EmployeePayrollDataException {
		EmployeePayrollService employeePayrollService=new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData=employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.updateEmployeeSalary("Nadia",3000000.00);
		boolean result=employeePayrollService.checkEmployeePayrollSyncWithDB("Nadia");
		Assert.assertTrue(result);
	}
	
	@Test
	public void givenEmployeePayrollsWhenRetrievedInDateRangeDBShouldMatchEmployeeCount() throws EmployeePayrollDataException {
		EmployeePayrollService employeePayrollService=new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData=employeePayrollService.readEmployeePayrollDataByDate(IOService.DB_IO,"2018-07-10");
		Assert.assertEquals(12, employeePayrollData.size());
		
	}
	 
	@Test
	public void givenEmployeePayrollsWhenTotalSalaryByMaleGenderRetrievedShouldMatchSalary() throws EmployeePayrollDataException {
		EmployeePayrollService employeePayrollService=new EmployeePayrollService();
		int totalSalary=employeePayrollService.readTotalSalary(IOService.DB_IO,"M");
		Assert.assertEquals(9400000, totalSalary);
		
	}
	
	@Test
	public void givenEmployeePayrollsWhenTotalSalaryByFemaleGenderRetrievedShouldMatchSalary() throws EmployeePayrollDataException {
		EmployeePayrollService employeePayrollService=new EmployeePayrollService();
		int totalSalary=employeePayrollService.readTotalSalary(IOService.DB_IO,"F");
		Assert.assertEquals(3000000, totalSalary);
		
	}
	
	@Test
	public void givenEmployeeDBWhenAddedShouldSyncWithDB() throws EmployeePayrollDataException {
		EmployeePayrollService employeePayrollService=new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		//String[] departments= {"IT","Marketing"};
		employeePayrollService.addEmployeePayrollData("Mark",5000000.00,LocalDate.now(),"M");
		boolean result=employeePayrollService.checkEmployeePayrollSyncWithDB("Mark");
		Assert.assertTrue(result);
		
	}
	
	@Test
	public void givenMultipleEmployees_WhenAddedToDB_ShouldMatchCountEntries() throws EmployeePayrollDataException {
		EmployeePayrollData[] empArrays= { new EmployeePayrollData(0,"Alicia","F",980000.0,LocalDate.now()),
										   new EmployeePayrollData(0,"Greg","M",880000.0,LocalDate.now()),
										   new EmployeePayrollData(0,"Ester","F",710000.0,LocalDate.now()),
										   new EmployeePayrollData(0,"Hndrich","M",100000.0,LocalDate.now()),
										   new EmployeePayrollData(0,"Barry","M",750000.0,LocalDate.now()) };
		EmployeePayrollService employeePayrollService=new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Instant start=Instant.now();
		employeePayrollService.addEmployeesPayrollData(Arrays.asList(empArrays));
		Instant end=Instant.now();
		System.out.println("Duration without thread "+Duration.between(start,end));
		Instant threadStart=Instant.now();
		employeePayrollService.addEmployeesPayrollDataWithThreads(Arrays.asList(empArrays));
		Instant threadEnd=Instant.now();
		System.out.println("Duration with thread "+Duration.between(start,end));
		employeePayrollService.printData(IOService.DB_IO);
		Assert.assertEquals(12 , employeePayrollService.countEntries(IOService.DB_IO));
	}
		
		
}
	
