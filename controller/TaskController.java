package controller;

import db.DBConnection;
import model.Models.Task;

import java.sql.*;

// 업무(Task) 관련 기능 처리
public class TaskController {

    // 업무 추가
    public int addTask(String title, int memberId, String deadline, int projectId) {

        String sql =
            "INSERT INTO task(title, assigned_to, deadline, status, project_id) " +
            "VALUES(?, ?, ?, '미완료', ?)";

        int taskId = -1;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt =
                 conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, title);
            pstmt.setInt(2, memberId);
            pstmt.setString(3, deadline);
            pstmt.setInt(4, projectId);

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) taskId = rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return taskId;
    }

    // 업무 상태 변경
    public boolean updateStatus(int taskId, String newStatus) {

        String sql = "UPDATE task SET status=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, taskId);

            pstmt.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 업무 삭제
    public boolean deleteTask(int taskId) {

        String sql = "DELETE FROM task WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, taskId);
            pstmt.executeUpdate();
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
