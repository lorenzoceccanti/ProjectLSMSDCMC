package it.unipi.largescale.pixelindex.dao;

import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.model.Game;
public interface GameDAO {

    Game getGameById(String id) throws DAOException;

}