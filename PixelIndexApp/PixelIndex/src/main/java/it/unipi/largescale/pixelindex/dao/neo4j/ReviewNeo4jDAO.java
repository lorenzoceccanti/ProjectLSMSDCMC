package it.unipi.largescale.pixelindex.dao.neo4j;

import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.model.Reaction;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;

import java.util.HashMap;
import java.util.Map;

public class ReviewNeo4jDAO extends BaseNeo4jDAO {

    public void insertReview(String reviewId, String gameId, String author) throws DAOException {
        try (Driver neoDriver = beginConnection()) {
            String query = """
                    MATCH (author:User {username: $author})
                    MATCH (game:Game {mongoId: $gameId})
                    CREATE (
                        review:Review {
                            mongoId: $reviewId
                            }
                        )
                    CREATE (author)-[:WRITES]->(review)
                    CREATE (review)-[:BELONGS]->(game)
                    """;
            Map<String, Object> params = new HashMap<>();
            params.put("reviewId", reviewId);
            params.put("gameId", gameId);
            params.put("author", author);

            try (Session session = neoDriver.session()) {
                session.executeWrite(tx -> {
                    tx.run(query, params);
                    return null;
                });
            }
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }

    public void removeReview(String reviewId) throws DAOException {
        try (Driver neoDriver = beginConnection()) {
            String query = """
                    MATCH (review:Review {mongoId: $reviewId})
                    DETACH DELETE review
                    """;
            Map<String, Object> params = new HashMap<>();
            params.put("reviewId", reviewId);

            try (Session session = neoDriver.session()) {
                session.executeWrite(tx -> {
                    tx.run(query, params);
                    return null;
                });
            }
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }

    /**
     * Add a reaction (like or dislike) to a review. If the user has already added a reaction, the previous one is
     * replaced with the new one. If the user has added a reaction and tries to add the same reaction again, the
     * reaction is removed.
     *
     * @param reviewId the id of the review
     * @param username the username of the user that adds the reaction
     * @param reaction the reaction to add
     * @throws DAOException if an error occurs
     */
    public String addReaction(String reviewId, String username, Reaction reaction, String gameId, String reviewAuthor) throws DAOException {
        try (Driver neoDriver = beginConnection()) {
            String query = """
                    // Passo 1: Assicurarsi che User, Review, e Game esistano, altrimenti crearli
                    MATCH (a:User {username: $reviewAuthor})
                    MATCH (g:Game {mongoId: $gameId})
                    MERGE (r:Review {mongoId: $reviewId})
                    MERGE (a)-[:WRITES]->(r)
                    MERGE (r)-[:BELONGS]->(g)
                                        
                    // Passo 2: Gestire la relazione LIKES
                    WITH r
                    MATCH (u:User {username: $username})
                    OPTIONAL MATCH (u)-[l:LIKES]->(r)
                    WITH u, r, l, CASE WHEN l IS NOT NULL AND l.value = $likeValue THEN 'DELETE'
                                       WHEN l IS NOT NULL AND l.value <> $likeValue THEN 'UPDATE'
                                       ELSE 'CREATE'
                                  END AS action
                    CALL apoc.do.case([
                      action = 'DELETE', 'MATCH (u)-[l:LIKES]->(r) DELETE l RETURN "deleted"',
                      action = 'UPDATE', 'MATCH (u)-[l:LIKES]->(r) SET l.value = $likeParam RETURN "updated"'
                      ],
                      'MATCH (u), (r) MERGE (u)-[:LIKES {value: $likeParam}]->(r) RETURN "created"',
                      {u:u, r:r, likeParam:$likeValue})
                    YIELD value
                    RETURN value
                    """;
            Map<String, Object> params = new HashMap<>();
            params.put("reviewId", reviewId);
            params.put("gameId", gameId);
            params.put("username", username);
            params.put("reviewAuthor", reviewAuthor);
            params.put("likeValue", reaction == Reaction.LIKE);

            try (Session session = neoDriver.session()) {
                return session.executeWrite(tx -> {
                    Result result = tx.run(query, params);
                    if (result.hasNext()) {
                        Value value = result.single().get("value");
                        if (!value.isNull()) {
                            return value.asMap().toString();
                        }
                    }
                    return "No result";
                });
            }
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }

    /**
     * Get the number of likes and dislikes of a review.
     *
     * @param reviewId the id of the review
     * @return a map containing the number of likes and dislikes
     * @throws DAOException if an error occurs
     */
    public Map<String, Integer> getReactionsCount(String reviewId) throws DAOException {
        // TODO: test della funzione
        try (Driver neoDriver = beginConnection()) {
            String query = """
                    MATCH (r:Review {mongoId: $reviewId})<-[l:LIKES]-(:User)
                    RETURN
                        SUM(CASE l.value WHEN true THEN 1 ELSE 0 END) AS likes,
                        SUM(CASE l.value WHEN false THEN 1 ELSE 0 END) AS dislikes
                    """;
            Map<String, Object> params = new HashMap<>();
            params.put("reviewId", reviewId);

            try (Session session = neoDriver.session()) {

                return session.executeWrite(tx -> {
                    Result result = tx.run(query, params);
                    int likes = result.single().get("likes").asInt();
                    int dislikes = result.single().get("dislikes").asInt();

                    Map<String, Integer> reactions = new HashMap<>();
                    reactions.put("likes", likes);
                    reactions.put("dislikes", dislikes);

                    return reactions;
                });
            }
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }


}
