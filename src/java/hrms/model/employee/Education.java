/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hrms.model.employee;

/**
 *
 * @author Manas Jena
 */
public class Education {
    private String qualification;
    private String faculty;
    private String yearofpass;
    private String degree;
    private String subject;
    private String institute;
    private String board;
    private int qfn_id;

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getYearofpass() {
        return yearofpass;
    }

    public void setYearofpass(String yearofpass) {
        this.yearofpass = yearofpass;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public int getQfn_id() {
        return qfn_id;
    }

    public void setQfn_id(int qfn_id) {
        this.qfn_id = qfn_id;
    }
    
    
}
