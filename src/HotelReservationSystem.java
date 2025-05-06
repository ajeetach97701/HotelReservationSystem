import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public  class HotelReservationSystem{

    private static  final String url = "jdbc:mysql://127.0.0.1:3306/hotel";
    private static final String username  = "root";
    private static final String password  = "root";
    public  static  void main(String[] args){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            Reservations reservations = new Reservations(connection, scanner);
            UpdateReservation updatereservations = new UpdateReservation(connection, scanner);

            while (true){
                System.out.println("------------------------------------------");
                System.out.println("---------Hotel Reservation System---------");
                System.out.println("Please choose your option: \n1. New Reservation\n2.Check Reservation\n3.Get Room Number\n4.Update Reservations\n5. Delete Reservations.");
                System.out.println("Enter Your choice:-  ");
                int choice = scanner.nextInt();
                switch (choice){
                    case 1:
                        System.out.println("New Reservation\n");
                        reservations.addReservation();
                        break;
                    case 2:
                        System.out.println("Check Reservation");
                        reservations.viewReservation();
                        break;
                    case 3:
                        System.out.println("Get Room Number");
                        break;
                    case 4:

                        System.out.println("Update Reservations");
                        updatereservations.updateReservation();
                        break;
                    case 5:
                        System.out.println("Delete Reservations");
                        reservations.deleteReservation();
                        break;
                    default:
                        System.out.println("Enter a valid choice");
                        break;

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}