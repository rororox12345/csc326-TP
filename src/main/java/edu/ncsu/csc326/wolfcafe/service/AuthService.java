package edu.ncsu.csc326.wolfcafe.service;

import java.util.List;

import edu.ncsu.csc326.wolfcafe.dto.JwtAuthResponse;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.dto.UserDto;

/**
 * Authorization service
 */
public interface AuthService {

    /**
     * Registers the given user
     *
     * @param registerDto
     *            new user information
     * @return message for success or failure
     */
    String register ( RegisterDto registerDto );

    /**
     * Logins in the given user
     *
     * @param loginDto
     *            username/email and password
     * @return response with authenticated user
     */
    JwtAuthResponse login ( LoginDto loginDto );

    /**
     * Deletes the given user by id
     *
     * @param id
     *            id of user to delete
     */
    void deleteUserById ( Long id );

    /**
     * Creates a staff user
     *
     * @param registerDto
     *            user encapsulation (name username pwd)
     * @return a new user w/staff permissions
     */
    UserDto createStaff ( RegisterDto registerDto );

    /**
     * updates user dto info
     *
     * @param id
     *            unique user id
     * @param registerDto
     *            user encapsulation (name username pwd)
     * @return updated userDto info
     */
    UserDto updateUser ( Long id, RegisterDto registerDto );

    /**
     * gets all the staff in the system
     *
     * @return staff list
     */
    List<UserDto> getAllStaff ();

    /**
     * gets all the customers in the system
     *
     * @return customer list
     */
    List<UserDto> getAllCustomers ();

    /**
     * Gets the corresponding user id for give user name
     *
     * @param username
     *            The username of the User
     * @return The userId
     */
    Long getUserIdFromUsername ( String username );

    /**
     * Get a specific user
     *
     * @param userId
     *            The User id
     * @return corresponding userDto
     */
    UserDto getUserById ( Long userId );

}
