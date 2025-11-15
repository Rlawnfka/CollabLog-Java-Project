package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// DB 연결 담당
public class DBConnection {

    // MySQL 연결 URL
    private static final String URL =
            "jdbc:mysql://localhost:3306/collabLog?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Seoul";

    // MySQL 계정
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // DB 연결 메서드
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
