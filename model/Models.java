package model;

// Member 모델
public class Models {

    public static class Member {
        public int id;
        public String name;
        public String role;

        public Member(int id, String name, String role) {
            this.id = id;
            this.name = name;
            this.role = role;
        }

        public String display() {
            if (role == null || role.isEmpty()) return name;
            return name + "(" + role + ")";
        }
    }

    // Task 모델
    public static class Task {
        public int id;
        public String name;
        public String member;
        public String deadline;
        public String status;

        public Task(int id, String name, String member, String deadline, String status) {
            this.id = id;
            this.name = name;
            this.member = member;
            this.deadline = deadline;
            this.status = status;
        }
    }
}
