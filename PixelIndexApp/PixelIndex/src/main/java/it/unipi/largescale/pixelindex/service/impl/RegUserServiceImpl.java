package it.unipi.largescale.pixelindex.service.impl;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import it.unipi.largescale.pixelindex.dao.BaseMongoDAO;
import it.unipi.largescale.pixelindex.dao.RegisteredUserMongoDAO;
import it.unipi.largescale.pixelindex.dao.RegisteredUserNeo4jDAO;
import it.unipi.largescale.pixelindex.dto.AuthUserDTO;
import it.unipi.largescale.pixelindex.dto.UserRegistrationDTO;
import it.unipi.largescale.pixelindex.dto.UserSearchDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.exceptions.UserNotFoundException;
import it.unipi.largescale.pixelindex.exceptions.WrongPasswordException;
import it.unipi.largescale.pixelindex.model.RegisteredUser;
import it.unipi.largescale.pixelindex.security.Crypto;
import it.unipi.largescale.pixelindex.service.RegisteredUserService;

import java.util.ArrayList;

public class RegUserServiceImpl implements RegisteredUserService {
    private RegisteredUserMongoDAO registeredUserDAO;
    private RegisteredUserNeo4jDAO registeredUserNeo;
    UserRegistrationDTO registrationDTO = new UserRegistrationDTO();


    public RegUserServiceImpl(){
        this.registeredUserDAO = new RegisteredUserMongoDAO();
        this.registeredUserNeo = new RegisteredUserNeo4jDAO();
    }

    @Override
    public AuthUserDTO makeLogin(String username, String password) throws WrongPasswordException, UserNotFoundException, ConnectionException {
        RegisteredUser registeredUser = null;
        try{
            registeredUser = registeredUserDAO.makeLogin(username, password);
        }catch(DAOException ex)
        {
            throw new ConnectionException(ex);
        }
        AuthUserDTO authUserDTO = new AuthUserDTO();
        authUserDTO.setId(registeredUser.getId());
        authUserDTO.setUsername(registeredUser.getUsername());
        authUserDTO.setName(registeredUser.getName());
        authUserDTO.setSurname(registeredUser.getSurname());
        authUserDTO.setEmail(registeredUser.getEmail());
        authUserDTO.setDateOfBirth(registeredUser.getDateOfBirth());
        authUserDTO.setRole(registeredUser.getRole());

        return authUserDTO;
    }

    @Override
    public AuthUserDTO register(UserRegistrationDTO userRegistrationDTO, String preferredLanguage) throws ConnectionException{
        RegisteredUser registeringUser = new RegisteredUser(preferredLanguage);
        registeringUser.setUsername(userRegistrationDTO.getUsername());
        registeringUser.setName(userRegistrationDTO.getName());
        registeringUser.setSurname(userRegistrationDTO.getSurname());
        registeringUser.setHashedPassword(Crypto.hashPassword(userRegistrationDTO.getPassword()));
        registeringUser.setDateOfBirth(userRegistrationDTO.getDateOfBirth());
        registeringUser.setEmail(userRegistrationDTO.getEmail());


        RegisteredUser registeredUser = null;

        // Starting a MongoDAO transaction
        MongoDatabase db;
        try(MongoClient mongoClient = BaseMongoDAO.beginConnection())
        {
           try(ClientSession clientSession = mongoClient.startSession()){
               clientSession.startTransaction();
               try{
                   // User registration, collection users MongoDB
                   registeredUser = registeredUserDAO.register(mongoClient, registeringUser, clientSession);
                   // Adding node to Neo4J
                   registeredUserNeo.register(userRegistrationDTO.getUsername());
                   clientSession.commitTransaction();
               }catch(DAOException ex)
               {
                   clientSession.abortTransaction();
                   throw new ConnectionException(ex);
               }
           }
        }

        AuthUserDTO authUserDTO = new AuthUserDTO();
        authUserDTO.setName(registeredUser.getName());
        authUserDTO.setSurname(registeredUser.getSurname());
        authUserDTO.setDateOfBirth(registeredUser.getDateOfBirth());
        authUserDTO.setEmail(registeredUser.getEmail());

        return authUserDTO;
    }

    @Override
    public ArrayList<UserSearchDTO> searchUser(String param) throws ConnectionException
    {
        ArrayList<UserSearchDTO> authUserDTOs = new ArrayList<>();
        try{
            authUserDTOs = registeredUserNeo.searchUser(param);
        }catch(DAOException ex)
        {
            throw new ConnectionException(ex);
        }
        return authUserDTOs;
    }

    @Override
    public void followUser(String usernameSrc, String usernameDst) throws ConnectionException
    {
        try{
            registeredUserNeo.followUser(usernameSrc, usernameDst);
        }catch(DAOException ex)
        {
            throw new ConnectionException(ex);
        }
    }

    @Override
    public void unfollowUser(String usernameSrc, String usernameDst) throws ConnectionException
    {
        try{
            registeredUserNeo.unfollowUser(usernameSrc, usernameDst);
        }catch(DAOException ex)
        {
            throw new ConnectionException(ex);
        }
    }
}
