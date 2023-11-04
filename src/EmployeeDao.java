import javax.xml.transform.Result;
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

    /**
     * 직원 조건 검색
     * @param conn
     * @param searchCondition
     * @param conditionValue
     * @return
     */
    public static List<EmployeeDto> findAllEmployee(Connection conn, String searchCondition, String conditionValue){
        ArrayList<EmployeeDto> employees = new ArrayList<>();
        try {
            String selectClause = "select E.Fname, E.Minit, E.Lname, E.Ssn, E.Bdate, E.Address, E.Sex, E.Salary, S.Fname, S.Minit, S.Lname, D.Dname";
            String fromClause = "from EMPLOYEE E" +
                    " LEFT JOIN EMPLOYEE S ON E.Super_ssn =  S.Ssn " +
                    " LEFT JOIN DEPARTMENT D ON E.Dno = D.Dnumber";
            String whereClause = "";

            if (!conditionValue.equals("")) {
                switch (searchCondition) {
                    case "ALL":
                        break;
                    case "FNAME":
                        whereClause += "where E.Fname = '" + conditionValue + "'";
                        break;
                    case "SSN":
                        whereClause += "where E.Ssn = '" + conditionValue + "'";
                        break;
                    case "ADDRESS":
                        whereClause += "where E.Address LIKE '%" + conditionValue + "%'";
                        break;
                    case "SEX":
                        whereClause += "where E.Sex = '" + conditionValue + "'";
                        break;
                    case "BIG_SALARY":
                        whereClause += "where E.Salary >= " + conditionValue;
                        break;
                    case "SMALL_SALARY":
                        whereClause += "where E.Salary <= " + conditionValue;
                        break;
                    case "SUPERVISOR_NAME":
                        whereClause += "where S.Fname = '" + conditionValue + "'";
                        break;
                    case "DNAME":
                        whereClause += "where D.Dname = '" + conditionValue + "'";
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
                        rs.getString("S.Fname")== null ? null : rs.getString("S.Fname") + " " + rs.getString("S.Minit") + " " + rs.getString("S.Lname"),
                        rs.getString("D.Dname"));
                employees.add(employeeDto);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    /**
     * 직원 정보 수정
     * @param conn
     * @param ssnList
     * @param changeAttribute
     * @param changeValue
     */
    public static void updateEmployee(Connection conn, List<String> ssnList, String changeAttribute, String changeValue){
        try {
            String sql = "UPDATE EMPLOYEE SET " +  changeAttribute + " = ? where Ssn = ?";
            for (String ssn : ssnList) {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, changeValue);
                pstmt.setString(2, ssn);
                int r = pstmt.executeUpdate();

                System.out.println("직원이 수정됐습니다.");
                pstmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    /**
     * 직원 정보 삭제
     * @param conn
     * @param ssnList
     */
    public static void deleteEmployee(Connection conn, List<String> ssnList) {
        try {
            String whereClause = "";

            for (int i = 0; i < ssnList.size(); i++) {
                String ssn = ssnList.get(i);
                String sql = "DELETE from EMPLOYEE where Ssn = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, ssn);

                int r = pstmt.executeUpdate();
                pstmt.close();
            }
            System.out.println(ssnList.size() + "명 직원이 삭제됐습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }


    public static void closeConnection(Connection conn) {
        try{
            conn.close();
            System.out.println("데이터베이스 연결 해제");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean existSsn(Connection connection, String ssn) {

        try {
            String sql = "Select * from Employee where Ssn = " + ssn + ";";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean existDno(Connection connection, String dno) {

        try {
            String sql = "Select * from Employee where Dno = " + dno + ";";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
