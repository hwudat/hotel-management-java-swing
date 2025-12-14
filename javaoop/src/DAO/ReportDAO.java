package DAO;

import models.DailyStat; 
import Utils.DatabaseConnection;
import java.sql.*;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportDAO {

    public Map<Integer, DailyStat> getMonthlyReport(int month, int year) {
        Map<Integer, DailyStat> statsMap = new HashMap<>();

        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
        for (int i = 1; i <= daysInMonth; i++) {
            statsMap.put(i, new DailyStat(i, 0, 0, 0));
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();

            String sqlRoom = 
                "SELECT DAY(check_out_date) as d, COUNT(*) as cnt, " +
                "SUM(rt.price_per_night * CASE WHEN DATEDIFF(day, check_in_date, check_out_date) = 0 THEN 1 ELSE DATEDIFF(day, check_in_date, check_out_date) END) as rev " +
                "FROM Booking b " +
                "JOIN Room r ON b.room_id = r.room_id " +
                "JOIN RoomType rt ON r.type_id = rt.type_id " +
                "WHERE MONTH(check_out_date) = ? AND YEAR(check_out_date) = ? AND b.status = 'Checked Out' " +
                "GROUP BY DAY(check_out_date)";

            PreparedStatement psRoom = conn.prepareStatement(sqlRoom);
            psRoom.setInt(1, month);
            psRoom.setInt(2, year);
            ResultSet rsRoom = psRoom.executeQuery();

            while (rsRoom.next()) {
                int day = rsRoom.getInt("d");
                int count = rsRoom.getInt("cnt");
                double rev = rsRoom.getDouble("rev");
                
                if(statsMap.containsKey(day)) {
                    statsMap.put(day, new DailyStat(day, count, rev, 0)); 
                }
            }

            String sqlService = 
                "SELECT DAY(usage_date) as d, SUM(total_price) as rev " +
                "FROM ServiceUsage " +
                "WHERE MONTH(usage_date) = ? AND YEAR(usage_date) = ? " +
                "GROUP BY DAY(usage_date)";

            PreparedStatement psService = conn.prepareStatement(sqlService);
            psService.setInt(1, month);
            psService.setInt(2, year);
            ResultSet rsService = psService.executeQuery();

            while (rsService.next()) {
                int day = rsService.getInt("d");
                double rev = rsService.getDouble("rev");

                if(statsMap.containsKey(day)) {
                    DailyStat s = statsMap.get(day);
                    s.setServiceRevenue(rev); 
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }

        return statsMap;
    }

    public int getTodayCheckInCount() {
        String sql = "SELECT COUNT(*) FROM Booking WHERE CAST(check_in_date AS DATE) = CAST(GETDATE() AS DATE)";
        return executeCountQuery(sql);
    }

    public int getTodayCheckOutCount() {
        String sql = "SELECT COUNT(*) FROM Booking WHERE CAST(check_out_date AS DATE) = CAST(GETDATE() AS DATE)";
        return executeCountQuery(sql);
    }

    public int getCurrentOccupiedCount() {
        String sql = "SELECT COUNT(*) FROM Room WHERE status = 'Occupied'";
        return executeCountQuery(sql);
    }

    private int executeCountQuery(String sql) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public Object[][] getTodayCheckInList() {
        String sql = "SELECT r.room_id, c.full_name, c.identity_card, rt.type_name, b.check_in_date, e.full_name " +
                     "FROM Booking b " +
                     "JOIN Customer c ON b.customer_id = c.customer_id " +
                     "JOIN Room r ON b.room_id = r.room_id " +
                     "JOIN RoomType rt ON r.type_id = rt.type_id " +
                     "LEFT JOIN Employee e ON b.employee_id = e.employee_id " +
                     "WHERE CAST(b.check_in_date AS DATE) = CAST(GETDATE() AS DATE)";
        return executeQueryAndGetArray(sql, 6);
    }

    public Object[][] getTodayCheckOutList() {
        String sql = "SELECT r.room_id, c.full_name, b.check_in_date, b.check_out_date " +
                     "FROM Booking b " +
                     "JOIN Customer c ON b.customer_id = c.customer_id " +
                     "JOIN Room r ON b.room_id = r.room_id " +
                     "WHERE CAST(b.check_out_date AS DATE) = CAST(GETDATE() AS DATE)";
        return executeQueryAndGetArray(sql, 4);
    }

    public Object[][] getOccupiedRoomsList() {
        String sql = "SELECT r.room_id, rt.type_name, c.full_name, b.check_in_date " +
                     "FROM Booking b " +
                     "JOIN Room r ON b.room_id = r.room_id " +
                     "JOIN RoomType rt ON r.type_id = rt.type_id " +
                     "JOIN Customer c ON b.customer_id = c.customer_id " +
                     "WHERE b.status = 'Checked In'";
        return executeQueryAndGetArray(sql, 4);
    }

    private Object[][] executeQueryAndGetArray(String sql, int colCount) {
        List<Object[]> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] row = new Object[colCount];
                for (int i = 0; i < colCount; i++) {
                    row[i] = rs.getString(i + 1); 
                }
                list.add(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
        
        Object[][] data = new Object[list.size()][colCount];
        for (int i = 0; i < list.size(); i++) {
            data[i] = list.get(i);
        }
        return data;
    }
}