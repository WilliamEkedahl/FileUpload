package com.OAP200V.MovieApp.model;
import java.sql.Timestamp;

/**
 * @author Ingvild
 */
public class Review {
    // Fields for information about the review
    private int id;
    private String title;
    private String comment;
    private int rating;
    private Timestamp date;


    /**
     * Constructor to initialize a Review with all details.
     * @param title
     * @param comment
     * @param rating
     * @param date

     */
    public Review(String title, String comment, int rating, Timestamp date) {
        this.title = title;
        this.comment = comment;
        this.rating = rating;
        this.date = date;
    }

    /**
     * Constructor to initialize a Review without comment, which is set to default.
     *
     * @param title
     * @param rating
     * @param date
     */
    public Review(String title, int rating, Timestamp date) {
        this(title, null, rating, date);
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

}
