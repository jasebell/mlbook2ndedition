package mlbook.ch12.kafka.mlp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBTools {
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void writeLinearResults(String uuid, double intercept, double slope, double r2, long time, String output) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/mlchapter12?user=xxxx&password=xxxx");

            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO training_log VALUES (null, ?, NOW(), ?, ?, ?, ?, ?)");
            pstmt.clearParameters();
            pstmt.setString(1, uuid);
            pstmt.setDouble(2, 1.0);
            pstmt.setLong(3, time);
            pstmt.setDouble(4, r2);
            pstmt.setString(5, "");
            pstmt.setString(6, "slr");
            pstmt.execute();
            pstmt.close();

            PreparedStatement pstmt2 = conn.prepareStatement("INSERT INTO linear_model VALUES (null, ?, NOW(), ? , ?, ?, ?)");
            pstmt2.clearParameters();
            pstmt2.setString(1, uuid);
            pstmt2.setDouble(2, slope);
            pstmt2.setDouble(3,intercept);
            pstmt2.setDouble(4, r2);
            pstmt2.setString(5, output);
            pstmt2.execute();
            pstmt2.close();
            conn.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void writeResultsToDB(String uuid, double evalsplit, long exectime, double accuracy, String output, String model_type){
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/mlchapter12?user=xxxx&password=xxxx");
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO training_log VALUES (null, ?, NOW(), ?, ?, ?, ?, ?)");
            pstmt.clearParameters();
            pstmt.setString(1, uuid);
            pstmt.setDouble(2, evalsplit);
            pstmt.setLong(3, exectime);
            pstmt.setDouble(4, accuracy);
            pstmt.setString(5, output);
            pstmt.setString(6,model_type);
            pstmt.execute();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
