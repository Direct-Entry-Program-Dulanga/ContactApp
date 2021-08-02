package model;

import java.io.Serializable;

public class ContactTM implements Serializable {
    private String student_id;
    private String contact;

    public ContactTM() {
    }

    public ContactTM(String student_id, String contact) {
        this.student_id = student_id;
        this.contact = contact;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "ContactTM{" +
                "student_id='" + student_id + '\'' +
                ", contact='" + contact + '\'' +
                '}';
    }
}
