package com.OAP200V.MovieApp.model;

import com.OAP200V.MovieApp.controller.ErrorHandling;
import com.OAP200V.MovieApp.model.DbConnection;
import com.OAP200V.MovieApp.model.Review;
import com.OAP200V.MovieApp.controller.ReviewController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

public class ReviewRepository extends AbstractRepository<Review> {


    public ReviewRepository(DbConnection connectionManager) {
        super(connectionManager);
    }


    @Override
    protected Review mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Review(rs.getString("address"), rs.getString("address2"), rs.getInt("district"),
                rs.getTimestamp("last_update"));
    }


    // Method to insert a new rating into the database
    public boolean insertRating(String address, String address2, String district) {
        String sql = "INSERT INTO address (address, address2, district, city_id, phone, location, last_update) " +
                "VALUES (?, ?, ?, 1, '1234567', ST_GeomFromText('POINT(0 0)'), CURRENT_TIMESTAMP)";

        try {
            super.executeUpdate(sql, address, address2, district); // Pass parameters directly
            return true; // If no exception is thrown, insertion was successful
        } catch (SQLException e) {
            String insertRatingFailed = "Error inserting rating: " + e.getMessage();
            ErrorHandling errorHandling = null;
            errorHandling.displayErrorMessage(insertRatingFailed);
            return false;
        }
    }
    // Method to update an already existing rating
    public boolean updateRating(String address, String address2, String district) {
        String sql = "UPDATE address SET address2 = ?, district = ?, last_update = CURRENT_TIMESTAMP WHERE address = ?";

        try {
            // Pass the parameters directly to the update query
            super.executeUpdate(sql, address2, district, address); // Note the order of parameters
            return true; // If no exception is thrown, the update was successful
        } catch (SQLException e) {
            String updateRatingFailed = "Error updating rating: " + e.getMessage();
            ErrorHandling errorHandling = ErrorHandling.getInstance(); // Access the singleton instance
            errorHandling.displayErrorMessage(updateRatingFailed);
            return false;
        }
    }

    public boolean doesReviewExist(String title) {
        String query = "SELECT COUNT(*) FROM address WHERE address = ?";
        try (ResultSet rs = executeQuery(query, title)) {
            if (rs.next()) {
                return rs.getInt(1) > 0; // Returns true if the count is greater than 0
            }
        } catch (SQLException e) {
            System.err.println("Error checking if review exists: " + e.getMessage());
        }
        return false;
    }


    public List<Review> getAllReviews() throws SQLException {
        String query = "SELECT address, address2, district, last_update FROM address WHERE address_id > 605";
        return super.getAll(query);
    }


    public void deleteReviewFromDatabase(String title) throws SQLException {
        String query = "DELETE FROM address WHERE address = ?";
        super.executeUpdate(query, title);
        //return true; (metoden er void?, //william)
    }
}
