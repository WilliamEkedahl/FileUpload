package com.OAP200V.MovieApp.model;
import com.OAP200V.MovieApp.model.Review;

/**
 * @author William Ekedahl
 * @return
 */
public class FormatConverter {

    private Review review;

    public FormatConverter(Review review) {
        this.review = review;
    }

    //using ternary operators
    public String[] CsvFormater() {
        return new String[]{
                review.getTitle() != null ? review.getTitle() : "",
                review.getComment() != null ? review.getComment() : "",
                String.valueOf(review.getRating()),
                review.getDate() != null ? review.getDate().toString() : ""
        };
    }
}
