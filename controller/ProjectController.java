package controller;

import db.DBConnection;
import model.Models.Member;
import model.Models.Task;

import java.sql.*;
import java.util.ArrayList;

// 프로젝트 관련 기능 처리
public class ProjectController {

    // 프로젝트 생성
    public int createProject(String name) {
        int projectId = -1;

        String sql = "INSERT INTO project(name) VALUES(?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                projectId = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return projectId;
    }

    // 프로젝트의 팀원 목록 로드
    public ArrayList<Member> loadMembers(int projectId) {
        ArrayList<Member> list = new ArrayList<>();

        String sql = "SELECT * FROM member WHERE project_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, projectId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(new Member(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("role")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 프로젝트의 업무 목록 로드
    public ArrayList<Task> loadTasks(int projectId) {
        ArrayList<Task> tasks = new ArrayList<>();

        String sql =
                "SELECT t.id, t.title, t.deadline, t.status, m.name, m.role " +
                "FROM task t JOIN member m ON t.assigned_to = m.id " +
                "WHERE t.project_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, projectId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                String memberName = rs.getString("name");
                String role = rs.getString("role");

                String displayName = (role == null || role.isEmpty())
                        ? memberName
                        : memberName + "(" + role + ")";

                tasks.add(new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        displayName,
                        rs.getString("deadline"),
                        rs.getString("status")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tasks;
    }
}
