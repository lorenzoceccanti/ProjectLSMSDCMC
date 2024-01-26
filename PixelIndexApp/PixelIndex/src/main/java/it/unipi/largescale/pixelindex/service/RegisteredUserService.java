package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.dto.AuthUserDTO;
import it.unipi.largescale.pixelindex.dto.UserRegistrationDTO;
import it.unipi.largescale.pixelindex.exceptions.UserNotFoundException;
import it.unipi.largescale.pixelindex.exceptions.WrongPasswordException;

public interface RegisteredUserService {
    AuthUserDTO makeLogin(String username, String password) throws WrongPasswordException, UserNotFoundException;
    AuthUserDTO register(UserRegistrationDTO registeredUserDTO, String preferredLanguage);
}