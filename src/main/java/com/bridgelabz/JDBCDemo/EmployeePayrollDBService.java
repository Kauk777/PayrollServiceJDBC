package com.bridgelabz.JDBCDemo;

import java.sql.Array;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBService {
	private int connectionCounter=0;
	private static EmployeePayrollDBService employeePayrollDBService;
	private PreparedStatement employeePayrollDataStatement;

	private EmployeePayrollDBService() {

	}

	public static EmployeePayrollDBService getInstance() {
		if (employeePayrollDBService == null)
			employeePayrollDBService = new EmployeePayrollDBService();
		return employeePayrollDBService;
	}

	private Connection getConnection() throws SQLException {
		connectionCounter++;
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String user = "root";
		String password = "root";

		Connection connection;
		System.out.println("Processing thread: " + Thread.currentThread().getName()+" Connecting to database with Id: " + connectionCounter);
		connection = DriverManager.getConnection(jdbcURL, user, password);
		System.out.println("Processing thread: " + Thread.currentThread().getName()+ " Id: " + connectionCounter + " Connection is successful!!!! " + connection);

		return connection;
	}

	public List<EmployeePayrollData> readData() throws EmployeePayrollDataException {
		String sql = "SELECT * FROM employee_payroll;";
		return this.getReadQueryData(sql);
	}

	private List<EmployeePayrollData> getReadQueryData(String sql) throws EmployeePayrollDataException {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try (Connection connection = this.getConnection()) {

			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			employeePayrollList = this.getEmployeePayrollData(result);

		} catch (SQLException e) {
			throw new EmployeePayrollDataException(e.getMessage(),
					EmployeePayrollDataException.ExceptionType.EMPLOYEEPAYROLL_DB_PROBLEM);
		}
		return employeePayrollList;

	}

	public List<EmployeePayrollData> readDataByDate(String startDate) throws EmployeePayrollDataException {
		String sql = "select * from employee_payroll where start between cast(? as date) and date(now());";
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try (Connection connection = this.getConnection()) {
			PreparedStatement prepareStatement = connection.prepareStatement(sql);
			prepareStatement.setString(1, startDate);
			ResultSet result = prepareStatement.executeQuery();
			employeePayrollList = this.getEmployeePayrollData(result);
		} catch (SQLException e) {
			throw new EmployeePayrollDataException(e.getMessage(),
					EmployeePayrollDataException.ExceptionType.EMPLOYEEPAYROLL_DB_PROBLEM);
		}
		return employeePayrollList;
	}

	public int readTotalSalary(String gender) throws EmployeePayrollDataException {
		String sql = "select gender,sum(salary) from employee_payroll group by gender;";
		int totalSalary = 0;
		try (Connection connection = this.getConnection()) {
			PreparedStatement prepareStatement = connection.prepareStatement(sql);
			ResultSet result = prepareStatement.executeQuery();
			while (result.next()) {
				if (result.getString(1).equalsIgnoreCase(gender))
					totalSalary = result.getInt(2);
			}
		} catch (SQLException e) {
			throw new EmployeePayrollDataException(e.getMessage(),
					EmployeePayrollDataException.ExceptionType.EMPLOYEEPAYROLL_DB_PROBLEM);
		}
		return totalSalary;
	}

	public int updateEmployeeData(String name, double salary) throws EmployeePayrollDataException {
		return this.updateEmployeeDataUSingStatement(name, salary);
	}

	private int updateEmployeeDataUSingStatement(String name, double salary) throws EmployeePayrollDataException {
		String sql = String.format("UPDATE employee_payroll SET salary=%.2f WHERE name='%s';", salary, name);
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
		} catch (SQLException e) {
			throw new EmployeePayrollDataException(e.getMessage(),
					EmployeePayrollDataException.ExceptionType.EMPLOYEEPAYROLL_DB_PROBLEM);
		}
	}

	public List<EmployeePayrollData> getEmployeePayrollData(String name) throws EmployeePayrollDataException {
		List<EmployeePayrollData> employeePayrollList = null;
		if (this.employeePayrollDataStatement == null)
			this.prepareStatementForEmployeeData();
		try {
			employeePayrollDataStatement.setString(1, name);
			ResultSet result = employeePayrollDataStatement.executeQuery();
			employeePayrollList = this.getEmployeePayrollData(result);
		} catch (SQLException e) {
			throw new EmployeePayrollDataException(e.getMessage(),
					EmployeePayrollDataException.ExceptionType.EMPLOYEEPAYROLL_DB_PROBLEM);
		}
		return employeePayrollList;
	}

	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet result) throws EmployeePayrollDataException {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try {
			while (result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				Double salary = result.getDouble("salary");
				LocalDate start = result.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id, name, salary, start));
			}
		} catch (SQLException e) {
			throw new EmployeePayrollDataException(e.getMessage(),
					EmployeePayrollDataException.ExceptionType.EMPLOYEEPAYROLL_DB_PROBLEM);
		}

		return employeePayrollList;
	}
	private void prepareStatementForEmployeeData() throws EmployeePayrollDataException {
		try {
			Connection connection = this.getConnection();
			String sql = "SELECT * FROM employee_payroll WHERE name=? ";
			employeePayrollDataStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			throw new EmployeePayrollDataException(e.getMessage(),
					EmployeePayrollDataException.ExceptionType.EMPLOYEEPAYROLL_DB_PROBLEM);
		}
	}

	public EmployeePayrollData addEmployeePayrollData(String name, double salary, LocalDate startDate, String gender) throws EmployeePayrollDataException {
		int employeeId=-1;
		Connection connection=null;
		EmployeePayrollData employeePayrollData=null;
		try {
			connection=this.getConnection();
		} catch (SQLException e) {
			throw new EmployeePayrollDataException(e.getMessage(),EmployeePayrollDataException.ExceptionType.EMPLOYEEPAYROLL_DB_PROBLEM);
		}
		try (Statement statement = connection.createStatement()) {
			String sql=String.format("INSERT INTO employee_payroll (name,salary,start,gender) "+"VALUES ('%s', %s, '%s','%s')", name, salary, Date.valueOf(startDate), gender);
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if(rowAffected==1) {
				ResultSet result=statement.getGeneratedKeys();
				if(result.next())
					employeeId=result.getInt(1);
			}
			
		} catch (SQLException e) {
			throw new EmployeePayrollDataException(e.getMessage(),EmployeePayrollDataException.ExceptionType.EMPLOYEEPAYROLL_DB_PROBLEM);
		}
		
		try(Statement statement = connection.createStatement()) {
			double deductions=salary*0.2;
			double taxablePay=salary-deductions;
			double tax=taxablePay*0.1;
			double netPay=salary-tax;
			String sql=String.format("INSERT INTO payroll_details "+"(employee_id,basic_pay,deductions,taxable_pay,tax,net_pay) VALUES "+"(%s,%s,%s,%s,%s,%s)",employeeId,salary,deductions,taxablePay,tax,netPay);
			int rowAffected = statement.executeUpdate(sql);
			if(rowAffected==1)
				employeePayrollData=new EmployeePayrollData(employeeId,name,salary,startDate);
			
		} catch (SQLException e) {
			throw new EmployeePayrollDataException(e.getMessage(),EmployeePayrollDataException.ExceptionType.EMPLOYEEPAYROLL_DB_PROBLEM);
		}
		
		return employeePayrollData;
		
	}

	public EmployeePayrollData addEmployeePayroll(int employeeId, String name, double salary, LocalDate startDate,
			String gender, int companyID, String[] department, String companyName) throws EmployeePayrollDataException {
		Connection connection = null;
		EmployeePayrollData employeePayrollData = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			throw new EmployeePayrollDataException(e.getMessage(),
					EmployeePayrollDataException.ExceptionType.EMPLOYEEPAYROLL_DB_PROBLEM);
		}
		try (Statement statement = connection.createStatement()) {
			double deductions = salary * 0.2;
			double taxablePay = salary - deductions;
			double tax = taxablePay * 0.1;
			double netPay = salary - tax;
			String sql = String.format("INSERT INTO payroll_details "
					+ "(employee_id,basic_pay,deductions,taxable_pay,tax,net_pay) VALUES " + "(%s,%s,%s,%s,%s,%s)",
					employeeId, salary, deductions, taxablePay, tax, netPay);
			int rowAffected = statement.executeUpdate(sql);
			if (rowAffected == 1)
				employeePayrollData = new EmployeePayrollData(employeeId, name, salary, startDate);

		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

		}

		try (Statement statement = connection.createStatement()) {
			int deptId = 0;
			int rowsAffected = 0;
			for (String dept : department) {
				if (dept.equalsIgnoreCase("Sales"))
					deptId = 101;
				if (dept.equalsIgnoreCase("Marketing"))
					deptId = 102;
				if (dept.equalsIgnoreCase("IT"))
					deptId = 103;
				if (dept.equalsIgnoreCase("HR"))
					deptId = 104;
				String sql = String.format(
						"INSERT INTO employee_department " + "(dept_id,dept_name,emp_id) VALUES " + "(%s,'%s',%s) ",
						deptId, dept, employeeId);
				rowsAffected = rowsAffected + statement.executeUpdate(sql);
			}

			if (rowsAffected == department.length)
				employeePayrollData = new EmployeePayrollData(employeeId, name, salary, startDate);

		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

		}

		try (Statement statement = connection.createStatement()) {
			String sql = String.format(
					"INSERT INTO employee_company " + "(companyID,companyName) VALUES " + "(%s,'%s') ", companyID,
					companyName);
			int rowAffected = statement.executeUpdate(sql);
			if (rowAffected == 1)
				employeePayrollData = new EmployeePayrollData(employeeId, name, salary, startDate);

		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		try {
			connection.commit();
		} catch (SQLException e) {

			e.printStackTrace();
		} finally {

			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return employeePayrollData;
	}

}
