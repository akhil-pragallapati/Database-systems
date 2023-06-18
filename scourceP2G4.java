import oracle.jdbc.*;

import java.sql.*;

import java.util.Scanner;

import oracle.jdbc.pool.OracleDataSource;

import oracle.jdbc.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Scanner;

import java.math.*;
import java.io.*;
import java.awt.*;

public class proj2G4 {
    public static void main(String[] args) {

        try{            

            /* Below is the connection object */  
            OracleDataSource data_source = new oracle.jdbc.pool.OracleDataSource();
            data_source.setURL("jdbc:oracle:thin:@castor.cc.binghamton.edu:1521:ACAD111");
            Connection oracle_connection_object = data_source.getConnection("apragal1", "16april1999");

            System.out.println("\n ..... Connected to the Oracle Database .... \n ");
            

            System.out.println("\n ---------- Welcome to the Student Management System ----------");

            System.out.println("\n Enter anyone of the following options below: ");

            boolean keep_running = true;
            Scanner input_scanner = new Scanner(System.in);

            CallableStatement call_table = null;
            BufferedReader student_input;
            String class_id, student_id, dept_code, course_no, cl_id;

            ResultSet result_set;

            while (keep_running) {

                // Printing  the Main Menu options
                System.out.println("\n 1. Display all the tables \n");
                System.out.println("\n 2. Enroll a Graduate student into a class \n");
                System.out.println("\n 3. DELETE a Graduate student from a class \n");
                
                System.out.println("\n 4. Delete a student from students table \n");
                
                System.out.println("\n 5. Display Every Student in a class \n");

                System.out.println("\n 6. Display Prerequisites for a class \n");

                System.out.println("\n 7. Quit \n");

                int choice;
                
                choice = input_scanner.nextInt();
                
                switch(choice) {

                    case 1:
                        System.out.println("---------- Select the Table to be dispalyed ----------");
                        
                        System.out.println("\n 1. Display the Students table \n");
                        System.out.println("\n 2. Display the Course table \n");
                        System.out.println("\n 3. Display the Prerequisite table \n");
                        System.out.println("\n 4. Display the Course Credit table \n");
                        System.out.println("\n 5. Display the Classes table \n");
                        System.out.println("\n 6. Display the Score Grade table \n");
                        System.out.println("\n 7. Display the Graduate Enrollments \n");
                        System.out.println("\n 8. Display the Logs table \n");
                        System.out.println("\n 9. <-- Previous \n");

                        int table_choice;
                        table_choice = input_scanner.nextInt();

                        switch(table_choice){
                            
                            case 1:
                                call_table = oracle_connection_object.prepareCall("begin proj2.show_students(:1); end;");
                                
                                call_table.registerOutParameter(1, OracleTypes.CURSOR);

                                // Executing the procedure.
                                call_table.execute();
                                
                                result_set = (ResultSet)call_table.getObject(1);

                                // Printing the table contents.
                                System.out.println("B# || FIRSTNAME || LASTNAME || ST_LEVEL || GPA || EMAIL || BDATE");
                                for (int i = 1; result_set.next(); i++) {
                                    System.out.println(result_set.getString(1) + " || " + result_set.getString(2) + " || " + result_set.getString(3) + " || " + result_set.getString(4) + " || " + result_set.getDouble(5) + " || " + result_set.getString(6) + " || " + result_set.getString(7));
                                }
                            break;
                            case 2:
                                call_table = oracle_connection_object.prepareCall("begin proj2.show_courses(:1); end;");

                                call_table.registerOutParameter(1, OracleTypes.CURSOR);

                                // Executing the procedure.
                                call_table.execute();

                                result_set = (ResultSet)call_table.getObject(1);

                                // Printing the contents of table.
                                System.out.println("DEPT_CODE || COURSE# || TITLE");
                                for (int i = 1; result_set.next(); i++) {
                                    System.out.println(result_set.getString(1) + " || " + result_set.getString(2) + " || " + result_set.getString(3));
                                }
                            break;
                            case 3:                                
                                call_table = oracle_connection_object.prepareCall("begin proj2.show_prerequisites(:1); end;");

                                call_table.registerOutParameter(1, OracleTypes.CURSOR);

                                // Executing the procedure.
                                call_table.execute();

                                result_set = (ResultSet)call_table.getObject(1);

                                // Printing the contents of table.
                                System.out.println("DEPT_CODE || COURSE# || PREQ_DEPT_CODE || PRE_COURSE#");
                                for (int i = 1; result_set.next(); i++) {
                                    System.out.println(result_set.getString(1) + " || " + result_set.getString(2) + " || " + result_set.getString(3) + " || " + result_set.getString(4));
                                }
                            break;
                            case 4:
                                call_table = oracle_connection_object.prepareCall("begin proj2.show_course_credit(:1); end;");

                                call_table.registerOutParameter(1, OracleTypes.CURSOR);

                                // Executing the procedure
                                call_table.execute();

                                result_set = (ResultSet)call_table.getObject(1);

                                // Printing the contents of table
                                System.out.println("COURSE# || CREDITS");
                                for (int i = 1; result_set.next(); i++) {
                                    System.out.println(result_set.getString(1) + " || " + result_set.getString(2));
                                }
                            break;
                            case 5:
                                call_table = oracle_connection_object.prepareCall("begin proj2.show_classes(:1); end;");

                                call_table.registerOutParameter(1, OracleTypes.CURSOR);

                                // Executing the procedure.
                                call_table.execute();

                                result_set = (ResultSet)call_table.getObject(1);

                                // Printing the contents of table.
                                System.out.println("CLASSID || DEPT_CODE || COURSE# || SECT# || YEAR || SEMESTER || LIMIT || CLASS_SIZE || ROOM");
                                for (int i = 1; result_set.next(); i++) {
                                    System.out.println(result_set.getString(1) + " || " + result_set.getString(2) + " || " + result_set.getString(3) + " || " + result_set.getString(4) + " || " + result_set.getString(5) + " || " + result_set.getString(6) + " || " + result_set.getString(7) + " || " + result_set.getString(8) + " || " + result_set.getString (9));
                                }                                  
                            break;
                            case 6:
                                call_table = oracle_connection_object.prepareCall("begin proj2.show_score_grade(:1); end;");

                                call_table.registerOutParameter(1, OracleTypes.CURSOR);

                                // Executing the procedure.
                                call_table.execute();

                                result_set = (ResultSet)call_table.getObject(1);

                                // Printing the contents of table.
                                System.out.println("SCORE || LGRADE");
                                for (int i = 1; result_set.next(); i++) {
                                    System.out.println(result_set.getDouble(1) + " || " + result_set.getString(2));
                                }
                            break;
                            case 7:
                                call_table = oracle_connection_object.prepareCall("begin proj2.show_g_enrollments(:1); end;");

                                call_table.registerOutParameter(1, OracleTypes.CURSOR);

                                // Executing the procedure.
                                call_table.execute();

                                result_set = (ResultSet)call_table.getObject(1);

                                // Printing the contents of table.
                                System.out.println("G_B# || CLASSID || SCORE");
                                for (int i = 1; result_set.next(); i++) {
                                    System.out.println(result_set.getString(1) + " || " + result_set.getString(2) + " || " + result_set.getString(3));
                                }
                            break;
                            case 8:
                                call_table = oracle_connection_object.prepareCall("begin proj2.show_logs(:1); end;");

                                call_table.registerOutParameter(1, OracleTypes.CURSOR);

                                // Executing the procedure.
                                call_table.execute();

                                result_set = (ResultSet)call_table.getObject(1);

                                // Printing the contents of table.
                                System.out.println("LOG# || USER_NAME || OP_TIME || TABLE_NAME || OPERATION || TUPLE_KEY_VALUE");
                                for (int i = 1; result_set.next(); i++) {
                                    System.out.println(result_set.getString(1) + " || " + result_set.getString(2) + " || " + result_set.getString(3) + " || " + result_set.getString(4) + " || " + result_set.getString(5) + " || " + result_set.getString(6));
                                }
                            break;
                            case 9:    
                                System.out.println("Exit to the Main Menu");
                            break;
                            default :
                                System.out.println("Option is Invalid");
                            break;    

                        }
                    break;
                    case 2:
                        System.out.println("2: Enroll a graduate student");

                        student_input = getKeyboardReader();
                        System.out.println("\n Enter the B Number: \n");
                        student_id = student_input.readLine();

                        System.out.println("\n Enter CLASSID: \n");
                        cl_id = student_input.readLine();

                        call_table = oracle_connection_object.prepareCall("begin proj2.EnrollGraduateStudent(:1,:2,:3); end;");

                        call_table.setString(1, cl_id);
                        call_table.setString(2, student_id);
                        
                        call_table.registerOutParameter(3, Types.VARCHAR);
                        call_table.execute();

                         if(call_table.getString(3) != null)
                             {
                             System.out.println(call_table.getString(3)); 
                             }
                        

                    break;

                    case 3:
                        System.out.println("3: Delete a graduate student");
                        student_input = getKeyboardReader();
                        System.out.print("\n Enter the  B Number : \n");

                        student_id = student_input.readLine();

                        System.out.print("Please Enter CLASSID:");
                        cl_id = student_input.readLine();
                    
                        call_table = oracle_connection_object.prepareCall("BEGIN proj2.DropGraduateStudent(:1,:2,:3); end;");

                        call_table.setString(1, cl_id);

                        call_table.setString(2, student_id);
                        
                        call_table.registerOutParameter(3, Types.VARCHAR);
                        call_table.execute();

                        if(call_table.getString(3) != null)
                        {
                            System.out.println("Inside delete IF");
                             System.out.println(call_table.getString(3)); 
                        }

                    break;
                    case 4:
                        System.out.println("4: Delete a student from student table");
                        
                        student_input = getKeyboardReader();

                        System.out.println("Enter B Number: \n");
                        student_id = student_input.readLine();
                        System.out.print(" \n ");

                        call_table = oracle_connection_object.prepareCall("begin proj2.delete_student(:1,:2,:3); end;");

                        call_table.setString(1, student_id);
                        call_table.setString(2, student_id);

                        call_table.registerOutParameter(3, Types.VARCHAR);

                        // Execute procedure.
                        call_table.execute();

                        if(call_table.getString(3) != null) {
                            System.out.println(call_table.getString(3));
                        }
                        
                    break;
                    case 5:
                    System.out.println("\n 5: Display Every Student in a class \n");
                    student_input = getKeyboardReader();
                        System.out.println("\n Enter the class ID  value:");

                        cl_id = student_input.readLine();
                        System.out.println();
        
                         
                        call_table = oracle_connection_object.prepareCall("begin proj2.list_students(:1,:2,:3); end;");

                        call_table.registerOutParameter(1, OracleTypes.CURSOR);
                        call_table.setString(2, cl_id);
                        call_table.registerOutParameter(3, Types.VARCHAR);
                        call_table.execute();

                        // Reading the  cursor data
                        result_set = ((OracleCallableStatement)call_table).getCursor(1);

                        // reading the  error message
                        if(call_table.getString(3) == null) {
                            
                            System.out.println("B# || FIRSTNAME || LASTNAME ");

                            for (int i = 1; result_set.next(); i++) {
                                System.out.println(result_set.getString(1) + " || " + result_set.getString(2) + " || " + result_set.getString(3));
                            }

                        }
                        else {

                            System.out.println(call_table.getString(3));
                        }    
                    break;
                    case 6:
                        System.out.println("6: Show the prerequiste courses");

                        student_input = getKeyboardReader();

                        System.out.println("\n Enter Dept code: \n");
                        dept_code = student_input.readLine();

                        System.out.println(" \n  Enter Course Number: \n");
                        course_no = student_input.readLine();
                        System.out.print(" \n ");

                        call_table = oracle_connection_object.prepareCall("begin proj2.prerequisite_courses(:1,:2,:3,:4); end;");

                        call_table.setString(1, dept_code);
                        call_table.setInt(2, Integer.parseInt(course_no));

                        
                        // Register output parameters.
                        call_table.registerOutParameter(3, OracleTypes.CURSOR);
                        call_table.registerOutParameter(4, Types.VARCHAR);

                        // Execute procedure.
                        call_table.execute();

                        // Read cursor.
                        result_set = ((OracleCallableStatement) call_table).getCursor(3);

                        // Read error message.
                        String errorMsg = call_table.getString(4);
                        if (errorMsg != null) {
                            System.out.println(errorMsg);
                        } else {
                            System.out.println("The Prerequisite courses are:  \n");
                            // Print contents of table.
                            while (result_set.next()) {
                                System.out.println(result_set.getString(1));
                            }
                        }
                    break;
                    case 7:
                        System.out.println("You have exited the progarm Successfully, Goodbye!");
                        
                        keep_running = false;
                    break;
                    
                    
                    default:
                        System.out.println("Invalid choice. Please try again....");
                        break;
                    }
                }    
            }

        catch (SQLException ex) { System.out.println ("\n*** SQLException caught ***\n" + ex.getMessage());}
        catch (IOException e) {System.out.println("Error reading input: " + e.getMessage());}
    }

    public static BufferedReader getKeyboardReader() {
        return new BufferedReader(new InputStreamReader(System.in));
    }
    
}
