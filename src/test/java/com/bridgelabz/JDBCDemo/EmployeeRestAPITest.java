package com.bridgelabz.JDBCDemo;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import com.bridgelabz.JDBCDemo.EmployeePayrollService.IOService;

public class EmployeeRestAPITest {
	
	@Before
	public void setup() {
		RestAssured.baseURI= "http://localhost";
		RestAssured.port = 3000;
	}
	
	public EmployeePayrollData[] getEmployeeList() {
		Response response=RestAssured.get("/employee_payroll");
		System.out.println("Employee payroll entry in JSON server:\n "+ response.asString());
		EmployeePayrollData[] arrEmp=new Gson().fromJson(response.asString(),EmployeePayrollData[].class);
		return arrEmp;
	}
	
	@Test
	public void getEmployeeDatainJSONServer_WhenRetreived_ReturnCount() {
		EmployeePayrollData[] arrEmp=getEmployeeList();
		EmployeePayrollService employeePayrollService;
		employeePayrollService=new EmployeePayrollService(Arrays.asList(arrEmp));
		long entries=employeePayrollService.countEntries(IOService.REST_IO);
		Assert.assertEquals(2,entries);
	}
	
	@Test
	public void employeeAdded_ShouldMatchResponseAndCount() {
		EmployeePayrollService employeePayrollService;
		EmployeePayrollData[] arrEmp=getEmployeeList();
		employeePayrollService=new EmployeePayrollService(Arrays.asList(arrEmp));
		EmployeePayrollData employeePayrollData = new EmployeePayrollData(0,"Zaxia Lonavax","F",7700000,LocalDate.now());
		Response response=addEmployeeToJsonServer(employeePayrollData);
		int statusCode=response.getStatusCode();
		Assert.assertEquals(201,statusCode);
		employeePayrollData=new Gson().fromJson(response.asString(),EmployeePayrollData.class);
		employeePayrollService.addEmployeeToPayroll(employeePayrollData,IOService.REST_IO);
		long entries=employeePayrollService.countEntries(IOService.REST_IO);
		Assert.assertEquals(3,entries);
	}
	
	private Response addEmployeeToJsonServer(EmployeePayrollData employeePayrollData) {
		String empJson=new Gson().toJson(employeePayrollData);
		RequestSpecification request=RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(empJson);
		return request.post("/employee_payroll");
	}
	
	@Test
	public void givenListOfEmployees_WhenAdded_ShouldReturnCount() {
		EmployeePayrollService employeePayrollService;
		EmployeePayrollData[] arrEmp=getEmployeeList();
		employeePayrollService=new EmployeePayrollService(Arrays.asList(arrEmp));
		
		EmployeePayrollData[] arrEmpPayroll= { 
				new EmployeePayrollData(0,"Vexento","M",8700000,LocalDate.now()),
				new EmployeePayrollData(0,"Bolario","F",6500000,LocalDate.now()),
				new EmployeePayrollData(0,"Kariline","F",7400000,LocalDate.now())
		};
		
		for(EmployeePayrollData employeePayrollData:arrEmpPayroll) {
			Response response=addEmployeeToJsonServer(employeePayrollData);
			int statusCode=response.getStatusCode();
			Assert.assertEquals(201,statusCode);
			
			employeePayrollData=new Gson().fromJson(response.asString(),EmployeePayrollData.class);
			employeePayrollService.addEmployeeToPayroll(employeePayrollData,IOService.REST_IO);
		}
		
		long entries=employeePayrollService.countEntries(IOService.REST_IO);
		Assert.assertEquals(6,entries);
	}
	
	@Test
	public void WhenSalaryOfNewEmployeeUpdated_ShouldMatch200Request() throws EmployeePayrollDataException {
		EmployeePayrollService employeePayrollService;
		EmployeePayrollData[] arrEmp=getEmployeeList();
		employeePayrollService=new EmployeePayrollService(Arrays.asList(arrEmp));
		
		employeePayrollService.updateEmployeeSalary("Cereine",777777,IOService.REST_IO);
		EmployeePayrollData employeePayrollData=employeePayrollService.getEmployeePayrollData("Cereine");
		String empJson=new Gson().toJson(employeePayrollData);
		RequestSpecification request=RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(empJson);
		Response response=request.put("/employee_payroll/"+employeePayrollData.id);
		int statusCode=response.getStatusCode();
		Assert.assertEquals(200,statusCode);
	}
	
	@Test
	public void WhenEmployeeDataDeleted_ShouldMatch200RequestAndCount() {
		EmployeePayrollService employeePayrollService;
		EmployeePayrollData[] arrEmp=getEmployeeList();
		employeePayrollService=new EmployeePayrollService(Arrays.asList(arrEmp));
		
		EmployeePayrollData employeePayrollData=employeePayrollService.getEmployeePayrollData("Vexento");
		RequestSpecification request=RestAssured.given();
		request.header("Content-Type", "application/json");
		Response response=request.delete("/employee_payroll/"+employeePayrollData.id);
		int statusCode=response.getStatusCode();
		Assert.assertEquals(200,statusCode);
		
		employeePayrollService.deleteEmployeePayroll(employeePayrollData.name,IOService.REST_IO);
		long entries=employeePayrollService.countEntries(IOService.REST_IO);
		Assert.assertEquals(5,entries);
		
	}
	

	

}
