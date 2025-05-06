import com.mysql.cj.xdevapi.DeleteStatement;

import javax.swing.*;
import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Reservations {
    private Connection connection;
    private Scanner scanner;

    public Reservations(Connection connection, Scanner scanner){
        this.connection = connection;
        this.scanner = scanner;
    }

    public void  addReservation(){
        System.out.println("----------Add a new Reservation----------");
        System.out.println("Please Enter your name: ");
        String guest_name = scanner.next();
        System.out.println("Please enter your contact number: ");
        String contact_number = scanner.next();
        System.out.println("Please enter your booking date: (YYYY-MM-DD): ");
        String reservation_date = scanner.next();
//        int room_number = 101; // The logic of this is to be changes later on.
        try{
//            String query = "Insert into reservations(guest_name, contact, date, room_number) VALUES(?,?,?,?)";
            int roomNumber = get_latest_available_room();
            System.out.println(roomNumber);
            String query = " INSERT INTO reservations (guest_name, contact_number, reservation_date, room_number) values (?, ?, ?, ?)";

//            String after_reservation_query = """
//                    update rooms r
//                    join reservations res on r.id = res.room_number
//                    set r.status = 1 where res.room_number = (
//                    select max(room_number) from reservations
//                    );
//                    """;

            String after_reservation_query = """
                    update rooms r
                    join reservations res on r.id = res.room_number
                    set r.status = 1 where res.room_number = r.id;
                        """;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, guest_name);
            preparedStatement.setString(2, contact_number);
            preparedStatement.setString(3, reservation_date);
            preparedStatement.setInt(4, roomNumber);
            int affectedRows = preparedStatement.executeUpdate();

            PreparedStatement after_reservation_preparedStatement = connection.prepareStatement(after_reservation_query);
            int affectedRows2 = after_reservation_preparedStatement.executeUpdate();
            if (affectedRows > 0 ) {
                if (affectedRows2 > 0 ) {
                    System.out.println("Reservation Added Sucessfully");
                }
            } else {
                System.out.println("Failed to make Reservation. Please try again!");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private int get_latest_available_room(){
        String getRoomQuery = """
                select  id from rooms\s
                where status= 0\s
                order by id limit 1;
                """;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getRoomQuery);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()){
                int roomId = rs.getInt("id");
                return  roomId;
            }
            else {
                return  -1;
            }
        }catch (SQLException e)
        {
            e.printStackTrace();
            return  -1;
        }
    }
    public  void viewReservation(){

        System.out.println("----------------------------------------");
        String query;
        System.out.println("View reservation by \n1. Room number\n2. Date\n ");
        int choice = scanner.nextInt();
        switch (choice){
            case 1:
                System.out.println("Reservation by room Number");
                query = "select * from reservations where room_number = ?";
                System.out.println("Enter your room number");
                int room_number_view = scanner.nextInt();
                viewReservation_prepared_stmt(query, room_number_view);
                break;
            case 2:
                System.out.println("Reservation by room Number");
                query = "select * from reservations where reservation_date = ?";
                System.out.println("Enter the date of the reservation to view your reservation.");
                String date_view = scanner.next();
                viewReservation_prepared_stmt(query, date_view);
                System.out.println("Reservation by date");
            default:
                System.out.println("Enter a valid choice.");
        }

    }

    private void viewReservation_prepared_stmt(String query, String date){
        try {
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,date);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            int id = resultSet.getInt("id");
            String guest_name = resultSet.getString("guest_name");
            String view_date = resultSet.getString("reservation_date");
            String contact = resultSet.getString("contact_number");
            int room_number = resultSet.getInt("room_number");
            System.out.println(id+ " "+ guest_name + " "+ view_date+  " "+  room_number + " " +contact);
        }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    private void viewReservation_prepared_stmt(String query, int room_number){
        try {
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1,room_number);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            int id = resultSet.getInt("id");
            String guest_name = resultSet.getString("guest_name");
            String view_date = resultSet.getString("reservation_date");
            long contact = resultSet.getLong("contact_number");
            int view_room_number = resultSet.getInt("room_number");
            System.out.println(id+ " "+ guest_name + " "+ view_date+  " "+  view_room_number + " " +contact);
        }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void deleteReservation(){

        /*
        Steps:
         1. Fetch the room number of the  user who has checked out(to delete the reservation) given their contact number,
         which is unique(let for now).
         2. Store the room number in a variable.
         3. Drop the row of the contact number to delete the reservation
         4. Update the room number status to 0(Available for reservation).
         */

        // 1. Ask the user to provide their contact number to delete the row
        System.out.println("Enter the contact number to delete the reservation");
        String contact = scanner.next();
        // 2. Fetch query to get the room number given their contact number(unique).
        String querySelect = "select room_number from reservations where contact_number = ? "; // To fetch the room number
        // 3. Delete query to delete the reservation
        String queryDelete = "Delete from reservations where contact_number = ? ";
        // 4. Update query to update the room table room to 0(available).
        String queryRoomUpdate = "update rooms set status = 0 where id = ?";
        int room_number;
        try {
            // Execute the select query to fetch the room number
            PreparedStatement preparedStatementSelect = connection.prepareStatement(querySelect);
            preparedStatementSelect.setString(1, contact);
            ResultSet resultSetSelect = preparedStatementSelect.executeQuery();
            if (resultSetSelect.next()) {
                while (resultSetSelect.next()) {
                    // Fethc the room number if it exists.
                    room_number = resultSetSelect.getInt("room_number");
                    System.out.println("The room number is " + room_number);
                    // Prepare the update statemnt to udpate the room status to available.
                    PreparedStatement preparedStatementUpdate = connection.prepareStatement(queryRoomUpdate);
                    preparedStatementUpdate.setInt(1, room_number);
                    int updateRowsAffected = preparedStatementUpdate.executeUpdate();
                    PreparedStatement preparedStatement = connection.prepareStatement(queryDelete);
                    preparedStatement.setString(1, contact);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected != 0) {
                        System.out.println("Deleted the data");
                    } else {
                        System.out.println("Not deleted or not available.");
                    }
                }
            }
            else {
                System.out.println("no data for the contact number"+contact);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

    }

}
