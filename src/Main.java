import java.sql.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/company";
        String user = "sangjin";
        String password = "qwer1234";
        Connection conn = null;
        try{
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("success");
        } catch (SQLException e) {
            System.out.println("SQLException" + e);
        }

        Statement stmt = conn.createStatement();
        String sql = "select * from EMPLOYEE";
        ResultSet rs = stmt.executeQuery(sql);

        ArrayList list = new ArrayList();
        while (rs.next()) {
            String fNmae = rs.getString("Fname");
            System.out.println(fNmae);
        }
    }
}