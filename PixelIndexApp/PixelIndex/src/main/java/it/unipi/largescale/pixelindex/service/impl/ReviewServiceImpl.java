package it.unipi.largescale.pixelindex.service.impl;

import it.unipi.largescale.pixelindex.controller.ConsistencyThread;
import it.unipi.largescale.pixelindex.dao.mongo.ReviewMongoDAO;
import it.unipi.largescale.pixelindex.dao.neo4j.ReviewNeo4jDAO;
import it.unipi.largescale.pixelindex.dto.ReviewPageDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.model.Reaction;
import it.unipi.largescale.pixelindex.service.ReviewService;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.model.Review;

import java.util.Map;


public class ReviewServiceImpl implements ReviewService {

    private final ReviewMongoDAO reviewMongoDAO;
    private final ReviewNeo4jDAO reviewNeo4jDAO;

    public ReviewServiceImpl() {
        this.reviewMongoDAO = new ReviewMongoDAO();
        this.reviewNeo4jDAO = new ReviewNeo4jDAO();
    }

    public void insertReview(Review review, ConsistencyThread consistencyThread) throws ConnectionException {
        try {
            String reviewId = reviewMongoDAO.insertReview(review);
            review.setId(reviewId);

            consistencyThread.addTask(() -> {
                try {
                    reviewNeo4jDAO.insertReview(reviewId, review.getGameId(), review.getAuthor());
                    // TODO: inserire concistency flag in mongo?
                    // gameMongoDAO.updateConsistencyFlag(game.getId());
                } catch (DAOException e) {

                }
            });
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }

    }

    public void deleteReview(String reviewId, ConsistencyThread consistencyThread) throws ConnectionException {
        // TODO: da testare
        try {
            reviewMongoDAO.deleteReview(reviewId);
            consistencyThread.addTask(() -> {
                try {
                    reviewNeo4jDAO.deleteReview(reviewId);
                } catch (DAOException e) {

                }
            });

        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

    public ReviewPageDTO getReviews(String gameId) throws ConnectionException {
        // TODO: da testare
        try {
            return reviewMongoDAO.getReviewsByGameId(gameId);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

    public String addReaction(String reviewId, String username, Reaction reaction, String gameId, String reviewAuthor) throws ConnectionException {
        // TODO: da testare
        try {
            return reviewNeo4jDAO.addReaction(reviewId, username, reaction, gameId, reviewAuthor);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

    public Map<String, Integer> getReactionsCount(String reviewId) throws DAOException {
        // TODO: domanda - è necessario il blocco try-catch in quelli sopra?
        return reviewNeo4jDAO.getReactionsCount(reviewId);
    }
}