package com.example.signup;

public class DataHolder {
    String name;
    String course;
    String contactNo;
    String email;
    String imageUriString;

    public DataHolder(String name, String course, String contactNo, String email, String imageUriString) {
        this.name = name;
        this.course = course;
        this.contactNo = contactNo;
        this.email = email;
        this.imageUriString = imageUriString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUriString() {
        return imageUriString;
    }

    public void setImageUriString(String imageUriString) {
        this.imageUriString = imageUriString;
    }
}
