package Controller;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.StudentTM;

import java.sql.*;

public class StudentFormController {
    public TextField txtId;
    public TextField txtName;
    public TextField txtContact;
    public Button btnAdd;
    public Button btnClear;
    public Button btnRemove;
    public Button btnSave;
    public TableView<StudentTM> tblStudent;
    public Button btnNew;
    public Button btnDelete;
    public ListView<String> lstContact;


    private Connection connection;

    private PreparedStatement pstmInsertCustomer;
    private PreparedStatement pstmQueryID;
    private PreparedStatement pstmQueryStudent;
    private PreparedStatement pstmInsertContact;

    public void initialize(){
        tblStudent.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblStudent.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        ChangeListener<String> listener = (observable, oldValue, newValue) -> {
            String id = txtId.getText();
            String name = txtName.getText();


            btnSave.setDisable(!(id.matches("[C]\\d{3}") &&
                    name.matches("[A-Za-z ]{3,}")
                    ));
        };

        txtId.textProperty().addListener(listener);
        txtName.textProperty().addListener(listener);
        txtContact.textProperty().addListener(listener);

        btnSave.setDisable(true);
        btnSave.setDefaultButton(true);


        tblStudent.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, selectedCustomer) -> {
            if(selectedCustomer != null){
                txtId.setText(selectedCustomer.getId());
                txtName.setText(selectedCustomer.getName());
                txtId.setDisable(true);
                btnSave.setDisable(false);
                btnDelete.setDisable(false);
                btnSave.setText("Update");
            }else{
                btnSave.setText("Save");
                btnDelete.setDisable(false);
            }
        }));

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/DEP_master", "root", "test");
            pstmQueryID = connection.prepareStatement("SELECT id FROM student WHERE id=?");
            pstmQueryStudent = connection.prepareStatement("SELECT student_id FROM contact WHERE student_id=?");
            pstmInsertCustomer = connection.prepareStatement("INSERT INTO student VALUES (?,?,?)");
            pstmInsertContact = connection.prepareStatement("INSERT INTO contact VALUES (?, ?)");
        }catch (SQLException | ClassNotFoundException ex){
            new Alert(Alert.AlertType.ERROR, "Failed to connect to the database server").show();
            ex.printStackTrace();
            System.exit(1);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() ->{
            try{
                if(!connection.isClosed()){
                    connection.close();
                }
            }catch (SQLException ex){
                ex.printStackTrace();
            }
        }));

        try {
            Statement stmt = connection.createStatement();
            ResultSet rst = stmt.executeQuery("SELECT * FROM student");
            while(rst.next()){
                String id = rst.getString("id");
                String name = rst.getString("name");
                tblStudent.getItems().add(new StudentTM(id,name));
                System.out.println("----------------------------------");
                System.out.println(id);
                System.out.println(name);
                System.out.println("---------------------------------");

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }



    }

    public void btnSave_OnAction(ActionEvent actionEvent) {

        String id = txtId.getText();
        String name = txtName.getText();

        if(btnSave.getText().equals("SAVE")) {
            /*Todo: Save the customer in DB*/

            try {
                Statement stmt = connection.createStatement();
                String sql;
                sql = "SELECT id FROM student WHERE id='"+ id +"'";
                if(stmt.executeQuery(sql).next()){
                    new Alert(Alert.AlertType.ERROR, "Student ID Already Exists").show();
                    txtId.requestFocus();
                    return;
                }

                sql = "INSERT INTO student VALUES('%s','%s','%s')";
                sql = String.format(sql, id, name);
                int affectedRows = stmt.executeUpdate(sql);

                if (affectedRows == 1) {
                    tblStudent.getItems().add(new StudentTM(id, name));
                    btnNew.fire();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save the Student, retry").show();

                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to save the Student").show();

            }

            // Todo: Update the Customer in DB
        }else {

            try {
                Statement stmt = connection.createStatement();
                String sql = "SELECT * FROM student WHERE name = '" + name + "';";
                ResultSet rst = stmt.executeQuery(sql);
                if(rst.next() && !rst.getString("name").equals(id)){
                    new Alert(Alert.AlertType.ERROR, "NAME already exists").show();
                    txtName.requestFocus();
                    return;
                }


                sql = "UPDATE student SET name='%s' WHERE id='%s'";
                sql = String.format(sql, name, id);
                int affectedRows = stmt.executeUpdate(sql);
                System.out.println(affectedRows);

                if (affectedRows == 1) {
                    StudentTM selectedCustomer = tblStudent.getSelectionModel().getSelectedItem();
                    selectedCustomer.setName(name);
                    tblStudent.refresh();
                    btnNew.fire();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save the Student").show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to save the Student").show();
            }
        }
    }

    public void btnNew_OnAction(ActionEvent actionEvent) {
        txtId.clear();
        txtName.clear();
        tblStudent.getSelectionModel().clearSelection();
        txtId.requestFocus();
    }

    public void btnDelete_OnAction(ActionEvent actionEvent) {

        StudentTM selectedCustomer = tblStudent.getSelectionModel().getSelectedItem();

        try {
            Statement stmt = connection.createStatement();
            int affectedRows = stmt.executeUpdate("DELETE FROM student WHERE id='" + selectedCustomer.getId() +"'");

            if(affectedRows == 1){
                tblStudent.getItems().remove(selectedCustomer);
                tblStudent.refresh();
            }else{
                new Alert(Alert.AlertType.ERROR, "Failed to Delete Student").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to Delete Student").show();
        }
    }


    // Todo: Add contact to in DB
    public void btnAdd_onAction(ActionEvent actionEvent) {
        String id = txtId.getText();
        String contact = txtContact.getText();
        lstContact.getItems().add(contact);
        txtContact.clear();
        txtContact.requestFocus();

        try {
            Statement stmt = connection.createStatement();
            String sql;
            sql = "SELECT student_id FROM contact WHERE student_id='"+ id +"'";
            if(stmt.executeQuery(sql).next()){
                new Alert(Alert.AlertType.ERROR, "Student ID Already Exists").show();
                txtId.requestFocus();
                return;
            }

            sql = "INSERT INTO contact VALUES('%s','%s')";
            sql = String.format(sql, id, contact);
            int affectedRows = stmt.executeUpdate(sql);

            if (affectedRows == 1) {
                lstContact.getItems().add(contact);
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to ADD contact retry").show();

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to ADD contact").show();

        }
    }

    public void btnRemove_OnAction(ActionEvent actionEvent) {
        int selectedItem = lstContact.getSelectionModel().getSelectedIndex();
        lstContact.getItems().remove(selectedItem);
    }

    public void btnClear_onAction(ActionEvent actionEvent) {
        lstContact.getItems().clear();
    }
}
