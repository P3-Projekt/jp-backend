package com.dat3.jpgroentbackend.controllers.dto.request;

/**
 * This class represents an authentication request containing a username and password.
 * It is used to transfer authentication data from the client to the server.
 */
public class AuthRequest {

    // The username and password of the user attempting to authenticate
    public String username;
    public String password;
}
