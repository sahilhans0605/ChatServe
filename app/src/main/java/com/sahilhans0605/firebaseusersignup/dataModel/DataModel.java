package com.sahilhans0605.firebaseusersignup.dataModel;

public class DataModel {


    private String id, universityCollege, name, skills, course, purl;
    String token;


    public DataModel() {
//empty constructor na bnane ki vajah se nhi chala tha app
    }



    public DataModel(String id, String universityCollege, String name, String skills, String course, String purl) {
        this.id = id;
        this.universityCollege = universityCollege;
        this.name = name;
        this.skills = skills;
        this.course = course;
        this.purl = purl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUniversityCollege() {
        return universityCollege;
    }

    public void setUniversityCollege(String universityCollege) {
        this.universityCollege = universityCollege;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getPurl() {
        return purl;
    }

    public void setPurl(String purl) {
        this.purl = purl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
