package com.akapps.etutor;

public class TeacherPost {
    String category;
    String subjects;


    public TeacherPost(String category, String subjects) {
        this.category = category;
        this.subjects = subjects;

    }

    public String getCategory() {
        return category;
    }

    public String getSubjects() {
        return subjects;
    }
}
