package com.akapps.etutor;

public class StudentPost {
    String category;
    String subjects;
    String salaryRange;
    String fulladd;
    String lang;
    String lant;
    String uid;
    String structureddata;
    String showfirst;

    public StudentPost(String category, String subjects, String salaryRange, String fulladd, String lang, String lant, String uid, String structureddata, String showfirst) {
        this.category = category;
        this.subjects = subjects;
        this.salaryRange = salaryRange;
        this.fulladd = fulladd;
        this.lang = lang;
        this.lant = lant;
        this.uid = uid;
        this.structureddata = structureddata;
        this.showfirst = showfirst;
    }

    public String getCategory() {
        return category;
    }

    public String getSubjects() {
        return subjects;
    }

    public String getSalaryRange() {
        return salaryRange;
    }

    public String getFulladd() {
        return fulladd;
    }

    public String getLang() {
        return lang;
    }

    public String getLant() {
        return lant;
    }

    public String getUid() {
        return uid;
    }

    public String getStructureddata() {
        return structureddata;
    }

    public String getShowfirst() {
        return showfirst;
    }
}
