package com.OAP200V.MovieApp.controller;

import com.OAP200V.MovieApp.model.ReviewRepository;
import com.OAP200V.MovieApp.model.DbConnection;
import com.OAP200V.MovieApp.model.Review;
import com.OAP200V.MovieApp.util.FileHandler;
import com.OAP200V.MovieApp.view.MyReviews;
import com.OAP200V.MovieApp.view.SearchContent;
import com.OAP200V.MovieApp.model.FormatConverter;

import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/* Imported for Action event functionality*/
import java.awt.event.*;
/* */

import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import java.util.ArrayList;

public class ReviewController {
    private ReviewRepository reviewRepository;
    private MyReviews myReviews;
    private SearchContent searchContent;


    public ReviewController(DbConnection connectionManager) {
        this.reviewRepository = new ReviewRepository(connectionManager);
    }

    // Valgfri setter hvis du vil koble MyReviews senere
    public void setMyReviews(MyReviews myReviews) {
        this.myReviews = myReviews;
    }

    public List<Review> deleteReview(String title, DefaultTableModel model) {
        List<Review> reviews = new ArrayList<Review>();
        try {
            reviewRepository.deleteReviewFromDatabase(title);
            reviews = fetchAllReviews();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return reviews;
        	/*if (success) {
        		System.out.println("Review deleted successfully!");
    			List<Review> reviews = fetchAllReviews();
                myReviews.updateTable(reviews, model);    	}
        	else {
        		System.out.println("Failed to delete review");
        	} */
    }

    public void refreshTableData(DefaultTableModel model) throws SQLException {
        List<Review> reviews = fetchAllReviews();
        myReviews.updateTable(reviews, model);
    }


    // Metode for å legge til en rating via Movie
    public boolean addRating(String title, String description, String rating) {
        // Pass the parameters directly to insertRating
        return reviewRepository.insertRating(title, description, rating);
    }


    public List<Review> fetchAllReviews() throws SQLException {
        List<Review> reviews = reviewRepository.getAllReviews(); // Anta at denne metoden finnes
        System.out.println("Antall anmeldelser hentet: " + reviews.size());
        return reviews;
    }

    /* setupCharacterLimit should be apart of the view */
    public void setupCharacterLimit(JTextArea commentArea, JLabel charCountLabel, int maxCharacters) {
        // Initialize the character count label to "0/50"
        charCountLabel.setText("0/" + maxCharacters);

        // Add a DocumentListener to update the character count as the user types
        commentArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateCharacterCount();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateCharacterCount();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateCharacterCount();
            }

            // Method to update the character count
            private void updateCharacterCount() {
                int currentLength = commentArea.getText().length();
                charCountLabel.setText(currentLength + "/" + maxCharacters);
            }
        });
    }

    /* createCharacterLimitFilter Should also be in the view */
    public DocumentFilter createCharacterLimitFilter(int maxCharacters) {
        return new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if ((fb.getDocument().getLength() + string.length()) <= maxCharacters) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr) throws BadLocationException {
                if ((fb.getDocument().getLength() - length + string.length()) <= maxCharacters) {
                    super.replace(fb, offset, length, string, attr);
                }
            }
        };
    }


    public void setupUpdateButtonAction(JButton btnUpdateReview, JTextField choosenReview, JTextArea commentArea2, JComboBox<String> ratingBox, DefaultTableModel model) {
        // Access the global instance of ErrorHandling
        ErrorHandling errorHandling = ErrorHandling.getInstance();

        btnUpdateReview.addActionListener(e -> {
            String title = choosenReview.getText(); // Get chosen movie title
            String description = commentArea2.getText(); // Get comment
            String rating = (String) ratingBox.getSelectedItem(); // Get selected rating

            if (title.length() > 0) {
                // Call updateRating to update the review
                boolean success = reviewRepository.updateRating(title, description, rating);

                if (success) {
                    System.out.println("Review updated successfully!");
                    choosenReview.setText("");
                    commentArea2.setText("");
                    ratingBox.setSelectedIndex(0);
                    String successMessage = "Your review for \"" + title + "\" has been updated successfully!";
                    errorHandling.displayInformationMessage(successMessage);
                    try {
                        List<Review> updatedList = fetchAllReviews();
                        myReviews.updateTable(updatedList, model); // Use the passed model
                    } catch (SQLException ex) {
                        errorHandling.displayErrorMessage("Failed to refresh table: " + ex.getMessage());
                    }
                } else {
                    String failedToUpdateRating = "Failed to update rating and comment.";
                    errorHandling.displayErrorMessage(failedToUpdateRating);
                }
            } else {
                // Inform the user about the missing title
                errorHandling.displayErrorMessage("Please select a movie before updating your review.");
            }
        });
    }
    public boolean doesReviewExist(String title) {
        return reviewRepository.doesReviewExist(title);
    }

    public void setupTableClickListener(JTable table, JTextField choosenReview, JTextArea commentArea, JComboBox<String> ratingBox) {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String selectedTitle = table.getValueAt(row, 0).toString();
                        choosenReview.setText(selectedTitle);
                        String selectedComment = table.getValueAt(row, 1).toString();
                        commentArea.setText(selectedComment);
                        String selectedRating = table.getValueAt(row, 2).toString();
                        ratingBox.setSelectedItem(selectedRating);
                    }
                }
            }
        });
    }


    /* re generate javadoc when method is done */

    /**
     * Listen for button press... Some ErrorHandling,
     * if button press "yes" Try the method SaveReviewToFile in the class FileHandler.
     * if button press no (expand with more description here)
     *
     * @param reviews
     * @return
     * @author William Ekedahl 7084
     */
    public ActionListener getSaveReviewToFileActionListener() {
        //Acess the global instance of ErrorHandling
        ErrorHandling errorHandling = ErrorHandling.getInstance();

        return e -> {
            try {
                saveReviewToFile();
            } catch (IOException ex) {
                String error = "An error occured: " + ex.getMessage();
                errorHandling.displayErrorMessage(error);
                ex.printStackTrace();
            } catch (SQLException ex) {
                String error = "An error occured: " + ex.getMessage();
                errorHandling.displayErrorMessage(error);
            }
        };
    }

    /* re generate javadoc when method is done */

    /**
     * Listen for button press... Some ErrorHandling,
     * <p>
     * /**
     * Listen for button press... Some ErrorHandling,
     * if button press "yes" Try the method SaveReviewToFile in the class FileHandler.
     * if button press no (expand with more description here)
     * if button press no (expand with more description here)
     *
     * @param reviews
     * @return
     * @throws IOException
     * @throws SQLException
     * @author William Ekedahl 7084
     */
    public void saveReviewToFile() throws IOException, SQLException {
        String filename = "MyReviews.csv"; //hard kodet filnavn temp

        //Acess the global instance of ErrorHandling
        ErrorHandling errorHandling = ErrorHandling.getInstance();

        //Message text for confirmation
        String confirmMessage = "Do you want to save the changes to a file locally?";

        //show confirmation dialog before saving to file
        int result = errorHandling.displayConfirmMessage(confirmMessage);

        if (result == JOptionPane.YES_OPTION) {
            //fetch the reviews from the database by using the fetchAllReviews method.
            try {
                List<Review> reviews = reviewRepository.getAllReviews();

                List<String[]> csvFormattedReviews = new ArrayList<>();
                for (Review review : reviews) {
                    FormatConverter converter = new FormatConverter(review);
                    csvFormattedReviews.add(converter.CsvFormater());
                }

                FileHandler.saveReviewToFile(filename, csvFormattedReviews);
                //The message content
                String successMessage = "Reviews saved successfully to the file " + "'" + filename + "'";
                //display the message
                errorHandling.displayInformationMessage(successMessage);
            } catch (SQLException ex) {
                String databaseErrorMessage = "Error getting reviews from the database: " + ex.getMessage();
                errorHandling.displayErrorMessage(databaseErrorMessage);
            } catch (IOException ex) {
                //Message content for the error
                String errorMessage = "Error saving reviews to file: " + ex.getMessage();
                //Displaying the error message, implemented through the ErrorHandling class.
                errorHandling.displayErrorMessage(errorMessage);
            }

        } else if (result == JOptionPane.NO_OPTION) {
            //The message content if the user presses cancel
            String cancelMessage = "Saving to file was canceled";
            //show the cancel message
            errorHandling.displayInformationMessage(cancelMessage);
        } else {
            //if the dialog window is closed without a choice
            String windowClosedMessage = "Dialog closed, no button was pressed.";
            //Display the error message
            errorHandling.displayInformationMessage(windowClosedMessage);
        }
    }

    /* re generate javadoc when method is done */

    /**
     * UploadReviewFromFile woooh was the button pressed
     * then we do the Method UploadReviewFromFile in the classs FileHandler WOOOOH
     * and then its saved to the database
     *
     * @return
     * @author William Ekedahl 7084
     */

    /* add better error handeling */
    public ActionListener getUploadReviewFromFileActionListener() {

        ErrorHandling errorHandling = ErrorHandling.getInstance();

        return e -> {
            try {
                uploadReviewsFromFile();
            } catch (IOException ex) {
                String error = "A input error occured: " + ex.getMessage();
                errorHandling.displayErrorMessage(error);
            } catch (SQLException ex) {
                String error = "A database error occured: " + ex.getMessage();
                errorHandling.displayErrorMessage(error);
            }
            ;
        };
    }

    public void uploadReviewsFromFile() throws IOException, SQLException {
        String filename = "MyReviews.csv"; //hard kodet filnavn temp

        //Acess the global instance of ErrorHandling
        ErrorHandling errorHandling = ErrorHandling.getInstance();

        // Confirmation message text
        String confirmUploadFileMessage = "Do you want to upload " + "'" + filename + "'" + " that is storing your reviews to the database?";

        int result = errorHandling.displayConfirmMessage(confirmUploadFileMessage);

        if (result == JOptionPane.YES_OPTION) {
            try {
                //get the reviews from the file
                List<String[]> csvFormattedReviews = new ArrayList<>();
                FileHandler.upLoadReviewFromFile(filename, csvFormattedReviews);

                String successMessage = "Reviews uploaded successfully from the file: " + filename;
                errorHandling.displayInformationMessage(successMessage);

            } catch (IOException ex) {
                String errorMessage = "Error reading reviews from file: " + ex.getMessage();
                errorHandling.displayErrorMessage(errorMessage);
            }
        } else if (result == JOptionPane.NO_OPTION) {
            String cancelMessage = "Uploading from file was canceled.";
            errorHandling.displayInformationMessage(cancelMessage);
        } else {
            String windowClosedMessage = "Dialog closed, no button was pressed.";
            errorHandling.displayInformationMessage(windowClosedMessage);
        }
    }
}




