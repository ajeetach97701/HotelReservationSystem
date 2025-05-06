import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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



    public void updateReservation() {
        try {
            StringBuilder query = new StringBuilder("UPDATE reservations SET ");
            List<Object> params = new ArrayList<>();

            System.out.println("Enter the guest phone number to update the details");
            String old_phone = scanner.next();
            String verify_query = "Select id from reservations where contact_number = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(verify_query);
            preparedStatement.setString(1,old_phone);
            ResultSet resultSet = preparedStatement.executeQuery();
            int get_id = 0;
            if(resultSet.next()){
                get_id=  resultSet.getInt("id");
                System.out.println("Enter the new name of guest ('none' to skip):");
                String new_name = scanner.next();
                if (!new_name.equalsIgnoreCase("none")) {
                    query.append("guest_name = ?, ");
                    params.add(new_name);
                }

                System.out.println("Enter new phone number. Press 0 to skip:");
                int new_phone = scanner.nextInt();
                if (new_phone != 0) {
                    query.append("contact_number = ?, ");
                    params.add(new_phone);
                }

                System.out.println("Enter new Reservation Date ('none' to skip):");
                String new_date = scanner.next();
                if (!new_date.equalsIgnoreCase("none")) {
                    query.append("reservation_date = ?, ");
                    params.add(new_date);
                }


                if (query.charAt(query.length() - 2) == ',') {
                    query.setLength(query.length() - 2);
                }


                query.append(" WHERE id = ?");
                System.out.println(get_id);
                params.add(get_id);


                String queryString = query.toString();
                System.out.println("Generated query: " + queryString);

                PreparedStatement preparedStatementUpdate = connection.prepareStatement(queryString);
                for (int i = 0; i < params.size(); i++) {
                    preparedStatementUpdate.setObject(i + 1, params.get(i));
                    System.out.println(params.get(i));
                }


                int rowsAffected = preparedStatementUpdate.executeUpdate();
                System.out.println(rowsAffected + " row(s) updated.");

            }
            else {
                System.out.println("No records found");
            }






        }catch(Exception e)
        {
            System.out.println(e);
        }
        //     catch (SQLException e) {
        //         e.printStackTrace();
        //         System.out.println("Error occurred while updating reservation.");
        //     } finally {
        //         System.out.println("Finally Block");
        //     }
    }

}
