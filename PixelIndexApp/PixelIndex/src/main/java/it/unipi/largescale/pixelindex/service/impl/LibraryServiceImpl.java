package it.unipi.largescale.pixelindex.service.impl;

import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.service.LibraryService;
import it.unipi.largescale.pixelindex.dao.neo4j.LibraryNeo4jDAO;

import java.util.List;

public class LibraryServiceImpl implements LibraryService {
    private final LibraryNeo4jDAO libraryNeo4jDAO;

    public LibraryServiceImpl() {
        libraryNeo4jDAO = new LibraryNeo4jDAO();
    }

    public void addGame(String username, GamePreviewDTO gamePreviewDTO) throws ConnectionException {
        try {
            libraryNeo4jDAO.addGame(username, gamePreviewDTO.getId());
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

    public void removeGame(String username, GamePreviewDTO gameId) throws ConnectionException {
        try {
            libraryNeo4jDAO.removeGame(username, gameId.getId());
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

    public List<GamePreviewDTO> getGames(String username) throws ConnectionException {
        try {
            return libraryNeo4jDAO.getGames(username);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }
}
