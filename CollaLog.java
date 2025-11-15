
// 컴파일 javac -cp ".;lib/mysql-connector-j-9.5.0.jar" CollaLog.java controller/ProjectController.java controller/MemberController.java controller/TaskController.java model/Models.java util/ByteLimitFilter.java db/DBConnection.java
// Picked up JAVA_TOOL_OPTIONS: -Dfile.encoding=UTF-8

// 실행  java -cp ".;lib/mysql-connector-j-9.5.0.jar" CollaLog                                        
// Picked up JAVA_TOOL_OPTIONS: -Dfile.encoding=UTF-8


import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import controller.ProjectController;
import controller.MemberController;
import controller.TaskController;

import model.Models.Member;
import model.Models.Task;

import util.ByteLimitFilter;

public class CollaLog extends JFrame {

    private DefaultTableModel taskTableModel;
    private JTable taskTable;
    private JTextArea logArea;
    private JLabel progressLabel;

    private ArrayList<Task> tasks = new ArrayList<>();
    private ArrayList<Member> members = new ArrayList<>();

    private int projectId = -1;
    private JTextField projectField;

    // controllers
    private ProjectController projectCtrl = new ProjectController();
    private MemberController memberCtrl = new MemberController();
    private TaskController taskCtrl = new TaskController();

    public CollaLog() {
        initializeUI();
        createProject();
        loadMembers();
        loadTasks();
        updateStats();
    }

    // UI
    private void initializeUI() {
        setTitle("콜라로그 - 협업 로그 관리");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(createTopPanel(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createTaskPanel());
        splitPane.setRightComponent(createLogPanel());
        splitPane.setDividerLocation(600);

        add(splitPane, BorderLayout.CENTER);
        add(createStatsPanel(), BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    // 상단 UI (프로젝트 입력)
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(new Color(70, 130, 180));

        JLabel titleLabel = new JLabel("프로젝트:");
        titleLabel.setForeground(Color.WHITE);

        projectField = new JTextField("신규 프로젝트", 15);

        // 프로젝트 이름 제한 (100 bytes)
        ((AbstractDocument) projectField.getDocument())
                .setDocumentFilter(new ByteLimitFilter(100));

        JButton teamBtn = new JButton("팀원 관리");
        teamBtn.addActionListener(e -> showTeamManagement());

        JButton reportBtn = new JButton("보고서");
        reportBtn.addActionListener(e -> showReport());

        panel.add(titleLabel);
        panel.add(projectField);
        panel.add(teamBtn);
        panel.add(reportBtn);

        return panel;
    }

    // 프로젝트 생성
    private void createProject() {
        String name = projectField.getText().trim();
        if (name.isEmpty()) name = "신규 프로젝트";

        projectId = projectCtrl.createProject(name);
        addLog("프로젝트 생성: " + name + " (ID: " + projectId + ")");
    }

    // 업무 테이블 패널
    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("업무 목록"));

        String[] cols = {"업무명", "담당자", "마감일", "상태"};
        taskTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        taskTable = new JTable(taskTableModel);
        taskTable.setRowHeight(30);

        // 상태 + 마감일 색상 렌더러
        taskTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);

                if (isSelected) return c;

                String status = (String) table.getValueAt(row, 3);
                String deadlineStr = (String) table.getValueAt(row, 2);

                Date today = new Date();
                Date deadline = null;

                try {
                    deadline = new SimpleDateFormat("yyyy-MM-dd").parse(deadlineStr);
                } catch (Exception ignored) {}

                boolean isLate = (deadline != null && today.after(deadline) && !"완료".equals(status));

                if (isLate) {
                    c.setBackground(new Color(255, 70, 70));
                    c.setForeground(Color.WHITE);

                    if (col == 3) {
                        setText("늦으셨습니다!!!!!!!!!!!");
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    }
                    return c;
                }

                if ("완료".equals(status)) c.setBackground(new Color(200, 255, 200));
                else if ("진행중".equals(status)) c.setBackground(new Color(255, 255, 200));
                else c.setBackground(new Color(255, 220, 220));

                c.setForeground(Color.BLACK);
                return c;
            }
        });

        panel.add(new JScrollPane(taskTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addBtn = new JButton("업무 추가");
        addBtn.addActionListener(e -> addTask());

        JButton statusBtn = new JButton("상태 변경");
        statusBtn.addActionListener(e -> changeStatus());

        JButton delBtn = new JButton("삭제");
        delBtn.addActionListener(e -> deleteTask());

        btnPanel.add(addBtn);
        btnPanel.add(statusBtn);
        btnPanel.add(delBtn);

        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // 로그 패널
    private JPanel createLogPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("활동 로그"));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setText("=== 콜라로그 시작 ===\n\n");

        p.add(new JScrollPane(logArea));
        return p;
    }

    // 진행률 패널
    private JPanel createStatsPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        progressLabel = new JLabel();
        p.add(progressLabel);
        return p;
    }

    // 팀원 / 업무 불러오기
    private void loadMembers() {
        members = projectCtrl.loadMembers(projectId);
    }

    private void loadTasks() {
        tasks = projectCtrl.loadTasks(projectId);

        for (Task t : tasks) {
            taskTableModel.addRow(new Object[]{
                    t.name, t.member, t.deadline, t.status
            });
        }
    }

    // 업무 추가
    private void addTask() {

        if (members.isEmpty()) {
            JOptionPane.showMessageDialog(this, "팀원을 먼저 추가하세요.");
            return;
        }

        JTextField nameField = new JTextField(20);
        ((AbstractDocument) nameField.getDocument())
                .setDocumentFilter(new ByteLimitFilter(100));

        JComboBox<String> memberCombo = new JComboBox<>();
        for (Member m : members) memberCombo.addItem(m.display());

        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        JTextField dateField = new JTextField(today, 10);

        JPanel p = new JPanel(new GridLayout(3, 2));
        p.add(new JLabel("업무명"));
        p.add(nameField);
        p.add(new JLabel("담당자"));
        p.add(memberCombo);
        p.add(new JLabel("마감일"));
        p.add(dateField);

        int result = JOptionPane.showConfirmDialog(this, p, "업무 추가",
                JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) return;

        String title = nameField.getText().trim();
        String memberDisplay = (String) memberCombo.getSelectedItem();
        String deadline = dateField.getText().trim();

        String realName = memberDisplay.contains("(")
                ? memberDisplay.substring(0, memberDisplay.indexOf("("))
                : memberDisplay;

        int memberId = -1;
        for (Member m : members) if (m.name.equals(realName)) memberId = m.id;

        int taskId = taskCtrl.addTask(title, memberId, deadline, projectId);

        Task task = new Task(taskId, title, memberDisplay, deadline, "미완료");
        tasks.add(task);

        taskTableModel.addRow(new Object[]{title, memberDisplay, deadline, "미완료"});

        addLog("업무 추가: " + title);
        updateStats();
    }

    // 상태 변경
    private void changeStatus() {
        int row = taskTable.getSelectedRow();
        if (row == -1) return;

        Task task = tasks.get(row);

        String[] statuses = {"미완료", "진행중", "완료"};
        String newStatus = (String) JOptionPane.showInputDialog(
                this, "상태 변경", "상태 변경",
                JOptionPane.PLAIN_MESSAGE, null, statuses, task.status);

        if (newStatus == null) return;

        if (taskCtrl.updateStatus(task.id, newStatus)) {
            task.status = newStatus;
            taskTableModel.setValueAt(newStatus, row, 3);
            addLog("상태 변경: " + task.name + " → " + newStatus);
            updateStats();
        }
    }

    // 업무 삭제
    private void deleteTask() {
        int row = taskTable.getSelectedRow();
        if (row == -1) return;

        Task task = tasks.get(row);

        if (JOptionPane.showConfirmDialog(this,
                "삭제하시겠습니까?", "확인",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
            return;

        if (taskCtrl.deleteTask(task.id)) {
            tasks.remove(row);
            taskTableModel.removeRow(row);
            addLog("업무 삭제: " + task.name);
            updateStats();
        }
    }

    // 팀원 관리 창
    private void showTeamManagement() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        DefaultListModel<String> lm = new DefaultListModel<>();
        for (Member m : members) lm.addElement(m.display());

        JList<String> list = new JList<>(lm);

        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(250, 200));

        JPanel bottom = new JPanel(new FlowLayout());

        JTextField nameField = new JTextField(15);
        ((AbstractDocument) nameField.getDocument())
                .setDocumentFilter(new ByteLimitFilter(50));

        JTextField roleField = new JTextField(10);
        ((AbstractDocument) roleField.getDocument())
                .setDocumentFilter(new ByteLimitFilter(50));

        JButton addBtn = new JButton("추가");
        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String role = roleField.getText().trim();

            Member newMember = memberCtrl.addMember(name, role, projectId);

            members.add(newMember);
            lm.addElement(newMember.display());

            addLog("팀원 추가: " + newMember.display());

            nameField.setText("");
            roleField.setText("");
        });

        JButton delBtn = new JButton("삭제");
        delBtn.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx == -1) return;

            Member m = members.get(idx);

            if (memberCtrl.deleteMember(m.id)) {
                members.remove(idx);
                lm.remove(idx);
                addLog("팀원 삭제: " + m.display());
            } else {
                JOptionPane.showMessageDialog(this, "업무가 배정된 팀원은 삭제 불가");
            }
        });

        bottom.add(new JLabel("이름"));
        bottom.add(nameField);

        bottom.add(new JLabel("역할"));
        bottom.add(roleField);

        bottom.add(addBtn);
        bottom.add(delBtn);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(this, panel, "팀원 관리", JOptionPane.PLAIN_MESSAGE);
    }

    // 보고서
    private void showReport() {
        int total = tasks.size();
        int completed = (int) tasks.stream().filter(t -> "완료".equals(t.status)).count();
        int inProgress = (int) tasks.stream().filter(t -> "진행중".equals(t.status)).count();

        StringBuilder sb = new StringBuilder();
        sb.append("프로젝트 보고서\n\n");

        sb.append("전체 업무: ").append(total).append("개\n");
        sb.append("완료: ").append(completed).append("개\n");
        sb.append("진행중: ").append(inProgress).append("개\n\n");

        sb.append("팀원별 성과\n");

        for (Member m : members) {
            long totalTask = tasks.stream().filter(t -> t.member.startsWith(m.name)).count();
            long doneTask = tasks.stream().filter(t -> t.member.startsWith(m.name) && "완료".equals(t.status)).count();

            sb.append("- ").append(m.display()).append(": ")
                    .append(doneTask).append("/").append(totalTask).append(" 완료\n");
        }

        JTextArea ta = new JTextArea(sb.toString(), 20, 40);
        ta.setEditable(false);

        JOptionPane.showMessageDialog(this, new JScrollPane(ta),
                "보고서", JOptionPane.PLAIN_MESSAGE);

        addLog("보고서 생성");
    }

    // 로그 추가
    private void addLog(String msg) {
        String t = new SimpleDateFormat("HH:mm:ss").format(new Date());
        logArea.append("[" + t + "] " + msg + "\n");
    }

    // 진행률 업데이트
    private void updateStats() {
        int total = tasks.size();
        int completed = (int) tasks.stream().filter(t -> "완료".equals(t.status)).count();
        int inProgress = (int) tasks.stream().filter(t -> "진행중".equals(t.status)).count();

        int percent = total == 0 ? 0 : (completed * 100 / total);

        progressLabel.setText(
                "진행률: " + percent + "% | 전체: " + total + " | 완료: " +
                        completed + " | 진행중: " + inProgress
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CollaLog().setVisible(true));
    }
}
