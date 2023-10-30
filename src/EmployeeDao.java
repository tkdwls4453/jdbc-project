import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDao {
    public static Connection getConnection(String url, String user, String password) {
        Connection conn = null;
        try{
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("데이터베이스 연결 성공");

        } catch (SQLException e) {
            System.out.println("데이터베이스 연결 실패");
            System.out.println("SQLException" + e);
        }
        return conn;
    }

    /**
     * 직원 정보 삽입
     * @param conn
     * @param fname
     * @param minit
     * @param lname
     * @param ssn
     * @param bdate
     * @param address
     * @param sex
     * @param salary
     * @param superSsn
     * @param Dno
     */
    public static void createEmployee(Connection conn, String fname, String minit, String lname, String ssn,
                                      String bdate, String address, String sex,
                                      Double salary, String superSsn, String Dno){
        try {
            String sql = "insert into EMPLOYEE (Fname, Minit, Lname, Ssn, Bdate, Address, Sex, Salary, Super_ssn, Dno, created, modified) " +
                    "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            pstmt.setString(1, fname);
            pstmt.setString(2, minit);
            pstmt.setString(3, lname);
            pstmt.setString(4, ssn);
            pstmt.setString(5, bdate);
            pstmt.setString(6, address);
            pstmt.setString(7, sex);
            pstmt.setString(8, salary.toString());
            pstmt.setString(9, superSsn);
            pstmt.setString(10, Dno);
            pstmt.setString(11, now);
            pstmt.setString(12, now);

            int r = pstmt.executeUpdate();
            System.out.println("변경된 row: " + r);
            System.out.println("직원이 등록됐습니다.");
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public static List<EmployeeDto> findAllEmployee(Connection conn, String searchCondition, String conditionValue){
        ArrayList<EmployeeDto> employees = new ArrayList<>();
        try {
            String selectClause = "select E.Fname, E.Minit, E.Lname, E.Ssn, E.Bdate, E.Address, E.Sex, E.Salary, S.Fname, S.Minit, S.Lname, D.Dname";
            String fromClause = "from EMPLOYEE E, EMPLOYEE S, DEPARTMENT D";
            String whereClause = "where E.Super_ssn = S.Ssn AND E.Dno = D.Dnumber ";

            if (!conditionValue.equals("")) {
                switch (searchCondition) {
                    case "ALL":
                        break;
                    case "FNAME":
                        whereClause += "AND E.Fname = '" + conditionValue + "'";
                        break;
                    case "SSN":
                        whereClause += "AND E.Ssn = '" + conditionValue + "'";
                        break;
                    case "ADDRESS":
                        whereClause += "AND E.Address LIKE '%" + conditionValue + "%'";
                        break;
                    case "SEX":
                        whereClause += "AND E.Sex = '" + conditionValue + "'";
                        break;
                    case "BIG_SALARY":
                        whereClause += "AND E.Salary >= " + conditionValue;
                        break;
                    case "SMALL_SALARY":
                        whereClause += "AND E.Salary <= " + conditionValue;
                        break;
                    case "SUPERVISOR_NAME":
                        whereClause += "AND S.Fname = '" + conditionValue + "'";
                        break;
                    case "DNAME":
                        whereClause += "AND D.Dname = '" + conditionValue + "'";
                        break;
                }
            }

            String sql = selectClause + " " + fromClause + " " + whereClause + ";";

            System.out.println("sql = " + sql);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                EmployeeDto employeeDto = new EmployeeDto(rs.getString("E.Fname") + " " + rs.getString("E.Minit") + " " + rs.getString("E.Lname"),
                        rs.getString("Ssn"), rs.getString("Bdate"), rs.getString("Address"), rs.getString("Sex"), rs.getString("Salary"),
                        rs.getString("S.Fname") + " " + rs.getString("S.Minit") + " " + rs.getString("S.Lname"),
                        rs.getString("D.Dname"));
                employees.add(employeeDto);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }
    public static void closeConnection(Connection conn) {
        try{
            conn.close();
            System.out.println("데이터베이스 연결 해제");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
