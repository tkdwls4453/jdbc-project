import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/company";
        String user = "sangjin";
        String password = "qwer1234";
        // 디비 연결
        Connection conn = EmployeeDao.getConnection(url, user, password);

        // 직원 정보 저장
//        EmployeeDao.createEmployee(conn, "sangjin", "Y", "Yoon", "123456789", "1946-01-09",
//                "3321 Castle, Spring, TX", Sex.M, 30000.00, "333445555", 5L);

        // 직원 조건으로 검색
        List<EmployeeDto> allEmployee = EmployeeDao.findAllEmployee(conn, "ALL", "40000");
        for (EmployeeDto employeeDto : allEmployee) {
            System.out.println(employeeDto.toString());
        }
        // 연결 취소
        EmployeeDao.closeConnection(conn);
    }
}
