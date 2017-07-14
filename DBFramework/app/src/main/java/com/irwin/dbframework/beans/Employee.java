package com.irwin.dbframework.beans;

import com.irwin.database.BaseColumns;

/**
 * Created by ARES on 2017/7/13.
 */

public class Employee extends User {
    private int salary;

    private int post;

    private String code;

    private int score;

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public int getPost() {
        return post;
    }

    public void setPost(int post) {
        this.post = post;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String toString() {
        return "Employee{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", age=" + getAge() +
                ",salary=" + salary +
                ", post=" + post +
                ", code='" + code + '\'' +
                ", score='" + score + '\'' +
                '}';
    }

    public interface Columns extends User.Columns {
        String SALARY = "salary";

        String POST = "post";

        String CODE = "code";

        String SCORE = "score";
    }
}
