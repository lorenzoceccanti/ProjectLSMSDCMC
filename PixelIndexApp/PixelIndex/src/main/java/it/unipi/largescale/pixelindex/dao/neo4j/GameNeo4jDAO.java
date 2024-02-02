package it.unipi.largescale.pixelindex.dao.neo4j;

import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import it.unipi.largescale.pixelindex.model.Game;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.ServiceUnavailableException;

import static org.neo4j.driver.Values.parameters;

public class GameNeo4jDAO extends BaseNeo4jDAO {

    public void insertGame(Game game) throws DAOException {
        try (Driver neoDriver = beginConnection()) {
            String query = "CREATE (g:Game {mongoId: $id, name: $name, releaseYear: $releaseYear})";
            Map<String, Object> params = new HashMap<>();
            parameters("id", game.getId(),
                    "name", game.getName(),
                    "releaseYear", game.getReleaseDate().getYear());

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
}
