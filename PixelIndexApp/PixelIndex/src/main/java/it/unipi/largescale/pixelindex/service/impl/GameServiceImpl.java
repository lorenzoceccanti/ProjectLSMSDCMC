package it.unipi.largescale.pixelindex.service.impl;

import it.unipi.largescale.pixelindex.dao.GameMongoDAO;
import it.unipi.largescale.pixelindex.dao.GameNeo4jDAO;
import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.model.Game;
import it.unipi.largescale.pixelindex.service.GameService;

import java.util.List;

public class GameServiceImpl implements GameService {
    private final GameMongoDAO gameMongoDAO;
    private final GameNeo4jDAO gameNeo4jDAO;

    public GameServiceImpl() {
        this.gameMongoDAO = new GameMongoDAO();
        this.gameNeo4jDAO = new GameNeo4jDAO();
    }

    public List<GamePreviewDTO> searchGames(String name) throws ConnectionException {
        try {
            return gameNeo4jDAO.getGamesByName(name);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

    public Game getGameById(String id) throws ConnectionException {
        try {
            return gameMongoDAO.getGameById(id);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

    public void insertGameOnGraph(Game game) throws ConnectionException {
        try {
            gameNeo4jDAO.insertGame(game.getId());
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

    public void insertGameOnDocument(Game game) throws ConnectionException {
        try {
            gameMongoDAO.insertGame(game);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

}
