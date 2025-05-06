import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UpdateReservation {
    Connection connection;
    Scanner scanner;
    public  UpdateReservation(Connection connection, Scanner scanner){
        this.connection = connection;
        this.scanner = scanner;
    }



    public void updateReservation(){
        try {


            StringBuilder query = new StringBuilder("update reservation where contact_number = ? set ");

            System.out.println("Enter the new name of guest ('none' to skip):");
            String new_name = scanner.next();



            System.out.println("Enter new phone number. press 0 to skip): ");
            int new_phone = scanner.nextInt();


            System.out.println("Enter new Reservation Date ('none' to skip):");
            String new_date = scanner.next();







        } finally {
            System.out.println("Finally Block");
        }

    }



    public  static  void main(String[] args){
        Connection connection;
        Scanner scanner;
        UpdateReservation updatereservations = new UpdateReservation(connection, scanner);
    }

}
