package com.bridgelabz.JDBCDemo;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;



public class EmployeePayrollDBService 
{
   private static EmployeePayrollDBService employeePayrollDBService;
   private PreparedStatement employeePayrollDataStatement;
   
   private EmployeePayrollDBService() {
	   
   }
   
   public static EmployeePayrollDBService getInstance() {
	   if(employeePayrollDBService==null)
		   employeePayrollDBService=new EmployeePayrollDBService();
	   return employeePayrollDBService;
   }

/* public static void main( String[] args )
    {
        
        
        
        try {
        	Class.forName("com.mysql.jdbc.Driver");
        	System.out.println("Driver loaded");
        } catch(ClassNotFoundException e) {
        	System.out.println("Driver not found");
        	e.printStackTrace();
        }
        
        listDrivers();
        try {
        	
        } catch(Exception e) {
        	e.printStackTrace();
        }
    }

	private static void listDrivers() {
		Enumeration<Driver> driverList=DriverManager.getDrivers();
		while (driverList.hasMoreElements()) {
			Driver driverClass=(Driver) driverList.nextElement();
			System.out.println(" "+driverClass.getClass().getName());
		}
		
	}*/
	private Connection getConnection() throws SQLException {
		String jdbcURL="jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
        String user="root";
        String password="root";
        
        Connection connection;
        System.out.println("Connection to database: "+jdbcURL);
    	connection=DriverManager.getConnection(jdbcURL,user,password);
    	System.out.println("Connection is successful!!!!"+connection);
        
		return connection;
	}

	public List<EmployeePayrollData> readData() {
		String sql="SELECT * FROM employee_payroll;";
		List<EmployeePayrollData> employeePayrollList=new ArrayList<>();
		try (Connection connection=this.getConnection()) {
			
			Statement statement=connection.createStatement();
			ResultSet result=statement.executeQuery(sql);
			employeePayrollList=this.getEmployeePayrollData(result);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	public int updateEmployeeData(String name, double salary) {
		return this.updateEmployeeDataUSingStatement(name,salary);
	}

	private int updateEmployeeDataUSingStatement(String name, double salary) {
		String sql=String.format("UPDATE employee_payroll SET salary=%.2f WHERE name='%s';", salary,name);
        try (Connection connection=this.getConnection()) {
			Statement statement=connection.createStatement();
			return statement.executeUpdate(sql);
        }  catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public List<EmployeePayrollData> getEmployeePayrollData(String name) {
		List<EmployeePayrollData> employeePayrollList=null;
		if(this.employeePayrollDataStatement==null)
			this.prepareStatementForEmployeeData();
		try {
			employeePayrollDataStatement.setString(1, name);
			ResultSet result=employeePayrollDataStatement.executeQuery();
			employeePayrollList=this.getEmployeePayrollData(result);
		}  catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet result) {
		List<EmployeePayrollData> employeePayrollList=new ArrayList<>();
		try {
			while(result.next()) {
				int id=result.getInt("id");
				String name=result.getString("name");
				Double salary=result.getDouble("salary");
				LocalDate start=result.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id,name,salary,start));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
		return employeePayrollList;
	}

	private void prepareStatementForEmployeeData() {
		try {
			Connection connection=this.getConnection();
			String sql="SELECT * FROM employee_payroll WHERE name=? ";
			employeePayrollDataStatement=connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}