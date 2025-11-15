package controller;

import db.DBConnection;
import model.Models.Member;

import java.sql.*;

// 팀원 관리 기능 처리
public class MemberController {

    // 팀원 추가
    public Member addMember(String name, String role, int projectId) {
        String sql =
                "INSERT INTO member(name, role, project_id) VALUES(?, ?, ?)";

        int id = -1;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            pstmt.setString(2, role);
            pstmt.setInt(3, projectId);

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) id = rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Member(id, name, role);
    }

    // 팀원 삭제
    public boolean deleteMember(int memberId) {

        String sql = "DELETE FROM member WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberId);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            // 외래키 때문에 업무가 걸려 삭제 불가한 경우
            return false;
        }
    }
}
