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
                                      String bdate, String address, Sex sex,
                                      Double salary, String superSsn, Long Dno){
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
            pstmt.setString(7, sex.toString());
            pstmt.setString(8, salary.toString());
            pstmt.setString(9, superSsn);
            pstmt.setString(10, Dno.toString());
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

    public static List<EmployeeDto> findAllEmployee(Connection conn, List<String> selectedAttributes ){
        ArrayList<EmployeeDto> employees = new ArrayList<>();
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
