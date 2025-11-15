import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class CollaLog extends JFrame {
    private DefaultTableModel taskTableModel;
    private JTable taskTable;
    private JTextArea logArea;
    private JLabel progressLabel;
    private ArrayList<Task> tasks;
    private ArrayList<String> teamMembers;
    
    public CollaLog() {
        tasks = new ArrayList<>();
        teamMembers = new ArrayList<>();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("콜라로그 - 협업 로그 관리");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // 상단 프로젝트 정보 패널
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // 중앙 분할 패널
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createTaskPanel());
        splitPane.setRightComponent(createLogPanel());
        splitPane.setDividerLocation(600);
        add(splitPane, BorderLayout.CENTER);
        
        // 하단 통계 패널
        JPanel bottomPanel = createStatsPanel();
        add(bottomPanel, BorderLayout.SOUTH);
        
        setLocationRelativeTo(null);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(new Color(70, 130, 180));
        
        JLabel titleLabel = new JLabel("📋 프로젝트:");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        
        JTextField projectField = new JTextField("신규 프로젝트", 15);
        
        JButton teamBtn = new JButton("👥 팀원 관리");
        teamBtn.addActionListener(e -> showTeamManagement());
        
        JButton reportBtn = new JButton("📊 보고서");
        reportBtn.addActionListener(e -> showReport());
        
        panel.add(titleLabel);
        panel.add(projectField);
        panel.add(teamBtn);
        panel.add(reportBtn);
        
        return panel;
    }
    
    
    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("업무 목록"));
        
        // 테이블 설정
        String[] columns = {"업무명", "담당자", "마감일", "상태"};
        taskTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        taskTable = new JTable(taskTableModel);
        taskTable.setRowHeight(30);
        taskTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        
        // 상태별 색상
        taskTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String status = (String) table.getValueAt(row, 3);
                    if ("완료".equals(status)) {
                        c.setBackground(new Color(200, 255, 200));
                    } else if ("진행중".equals(status)) {
                        c.setBackground(new Color(255, 255, 200));
                    } else {
                        c.setBackground(new Color(255, 220, 220));
                    }
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(taskTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // 버튼 패널
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton addBtn = new JButton("➕ 업무 추가");
        addBtn.addActionListener(e -> addTask());
        
        JButton statusBtn = new JButton("🔄 상태 변경");
        statusBtn.addActionListener(e -> changeStatus());
        
        JButton deleteBtn = new JButton("🗑️ 삭제");
        deleteBtn.addActionListener(e -> deleteTask());
        
        btnPanel.add(addBtn);
        btnPanel.add(statusBtn);
        btnPanel.add(deleteBtn);
        
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("활동 로그"));
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        logArea.setText("=== 콜라로그 시작 ===\n\n");
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEtchedBorder());
        
        progressLabel = new JLabel("📈 진행률: 0% | 전체: 0개 | 완료: 0개 | 진행중: 0개 | 미완료: 0개");
        progressLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        
        panel.add(progressLabel);
        
        return panel;
    }
    
    private void addTask() {
        if (teamMembers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "먼저 팀원을 등록해주세요!", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JTextField nameField = new JTextField(20);
        JComboBox<String> memberCombo = new JComboBox<>(teamMembers.toArray(new String[0]));
        JTextField dateField = new JTextField("2025-09-05", 10);
        
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.add(new JLabel("업무명:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("담당자:"));
        inputPanel.add(memberCombo);
        inputPanel.add(new JLabel("마감일:"));
        inputPanel.add(dateField);
        
        int result = JOptionPane.showConfirmDialog(this, inputPanel, "업무 추가", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String taskName = nameField.getText().trim();
            String member = (String) memberCombo.getSelectedItem();
            String deadline = dateField.getText().trim();
            
            if (!taskName.isEmpty()) {
                Task task = new Task(taskName, member, deadline);
                tasks.add(task);
                taskTableModel.addRow(new Object[]{taskName, member, deadline, "미완료"});
                addLog(member + "님에게 '" + taskName + "' 업무 할당");
                updateStats();
            }
        }
    }
    
    private void changeStatus() {
        int row = taskTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "상태를 변경할 업무를 선택해주세요!", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Task task = tasks.get(row);
        String[] statuses = {"미완료", "진행중", "완료"};
        String newStatus = (String) JOptionPane.showInputDialog(this, "새로운 상태를 선택하세요:",
                "상태 변경", JOptionPane.QUESTION_MESSAGE, null, statuses, task.status);
        
        if (newStatus != null && !newStatus.equals(task.status)) {
            String oldStatus = task.status;
            task.status = newStatus;
            taskTableModel.setValueAt(newStatus, row, 3);
            addLog(task.member + "님이 '" + task.name + "' 상태를 [" + oldStatus + "] → [" + newStatus + "]로 변경");
            updateStats();
        }
    }
    
    private void deleteTask() {
        int row = taskTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "삭제할 업무를 선택해주세요!", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "정말 삭제하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Task task = tasks.get(row);
            addLog("'" + task.name + "' 업무 삭제");
            tasks.remove(row);
            taskTableModel.removeRow(row);
            updateStats();
        }
    }
    
    private void showTeamManagement() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String member : teamMembers) {
            listModel.addElement(member);
        }
        
        JList<String> memberList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(memberList);
        scrollPane.setPreferredSize(new Dimension(250, 200));
        
        JPanel btnPanel = new JPanel(new FlowLayout());
        JTextField nameField = new JTextField(15);
        JTextField roleField = new JTextField(10);
        
        JButton addBtn = new JButton("추가");
        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String role = roleField.getText().trim();
            if (!name.isEmpty()) {
                String member = name + (role.isEmpty() ? "" : "(" + role + ")");
                teamMembers.add(member);
                listModel.addElement(member);
                nameField.setText("");
                roleField.setText("");
                addLog("팀원 추가: " + member);
            }
        });
        
        JButton removeBtn = new JButton("제거");
        removeBtn.addActionListener(e -> {
            int idx = memberList.getSelectedIndex();
            if (idx != -1) {
                String member = listModel.get(idx);
                teamMembers.remove(idx);
                listModel.remove(idx);
                addLog("팀원 제거: " + member);
            }
        });
        
        btnPanel.add(new JLabel("이름:"));
        btnPanel.add(nameField);
        btnPanel.add(new JLabel("역할:"));
        btnPanel.add(roleField);
        btnPanel.add(addBtn);
        btnPanel.add(removeBtn);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        JOptionPane.showMessageDialog(this, panel, "팀원 관리", JOptionPane.PLAIN_MESSAGE);
    }
    
    private void showReport() {
        Map<String, int[]> memberStats = new HashMap<>();
        
        for (Task task : tasks) {
            memberStats.putIfAbsent(task.member, new int[2]);
            memberStats.get(task.member)[0]++;
            if ("완료".equals(task.status)) {
                memberStats.get(task.member)[1]++;
            }
        }
        
        StringBuilder report = new StringBuilder();
        report.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        report.append("         📊 프로젝트 진행 보고서\n");
        report.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        
        int total = tasks.size();
        int completed = (int) tasks.stream().filter(t -> "완료".equals(t.status)).count();
        int inProgress = (int) tasks.stream().filter(t -> "진행중".equals(t.status)).count();
        
        report.append("▶ 전체 업무: ").append(total).append("개\n");
        report.append("▶ 완료: ").append(completed).append("개\n");
        report.append("▶ 진행중: ").append(inProgress).append("개\n");
        report.append("▶ 진행률: ").append(total > 0 ? (completed * 100 / total) : 0).append("%\n\n");
        
        report.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        report.append("         👥 팀원별 성과\n");
        report.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        
        for (Map.Entry<String, int[]> entry : memberStats.entrySet()) {
            int totalTasks = entry.getValue()[0];
            int completedTasks = entry.getValue()[1];
            int percentage = totalTasks > 0 ? (completedTasks * 100 / totalTasks) : 0;
            
            report.append("▪ ").append(entry.getKey()).append("\n");
            report.append("  ").append(totalTasks).append("개 중 ").append(completedTasks).append("개 완료 (").append(percentage).append("%)\n\n");
        }
        
        JTextArea textArea = new JTextArea(report.toString(), 20, 40);
        textArea.setEditable(false);
        textArea.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane, "프로젝트 보고서", JOptionPane.INFORMATION_MESSAGE);
        
        addLog("보고서 생성 완료");
    }
    
    private void addLog(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String timestamp = sdf.format(new Date());
        logArea.append("[" + timestamp + "] " + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
    
    private void updateStats() {
        int total = tasks.size();
        int completed = (int) tasks.stream().filter(t -> "완료".equals(t.status)).count();
        int inProgress = (int) tasks.stream().filter(t -> "진행중".equals(t.status)).count();
        int notStarted = total - completed - inProgress;
        int progress = total > 0 ? (completed * 100 / total) : 0;
        
        progressLabel.setText(String.format("📈 진행률: %d%% | 전체: %d개 | 완료: %d개 | 진행중: %d개 | 미완료: %d개",
                progress, total, completed, inProgress, notStarted));
    }
    
    class Task {
        String name;
        String member;
        String deadline;
        String status;
        
        Task(String name, String member, String deadline) {
            this.name = name;
            this.member = member;
            this.deadline = deadline;
            this.status = "미완료";
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new CollaLog().setVisible(true);
        });
    }
}