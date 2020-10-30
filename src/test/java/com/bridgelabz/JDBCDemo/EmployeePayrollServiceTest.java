package com.bridgelabz.JDBCDemo;

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

}
