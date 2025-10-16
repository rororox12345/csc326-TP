package edu.ncsu.csc326.wolfcafe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.JwtAuthResponse;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.dto.UserDto;
import edu.ncsu.csc326.wolfcafe.service.AuthService;

/**
 * Controller for authentication functionality.
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/auth" )
public class AuthController {

    /** Connection to AuthService */
    @Autowired
    private AuthService authService;

    /**
     * Registers a new customer user with the system.
     *
     * @param registerDto
     *            object with registration info
     * @return response indicating success or failure
     */
    @PostMapping ( "/register" )
    public ResponseEntity<String> register ( @RequestBody final RegisterDto registerDto ) {
        final String response = authService.register( registerDto );
        return new ResponseEntity<>( response, HttpStatus.CREATED );
    }

    /**
     * Logs in the given user
     *
     * @param loginDto
     *            user information for login
     * @return object representing the logged in user
     */
    @PostMapping ( "/login" )
    public ResponseEntity<JwtAuthResponse> login ( @RequestBody final LoginDto loginDto ) {
        final JwtAuthResponse jwtAuthResponse = authService.login( loginDto );
        return new ResponseEntity<>( jwtAuthResponse, HttpStatus.OK );
    }

    /**
     * gets all the staff in the system currently
     *
     * @return object representing the staff users
     */
    @GetMapping ( "/staff" )
    @PreAuthorize ( "hasRole('ADMIN')" )
    public ResponseEntity<List<UserDto>> getAllStaff () {
        // found http status selected
        return new ResponseEntity<>( authService.getAllStaff(), HttpStatus.OK );
    }

    /**
     * gets all the customers in the system currently
     *
     * @return object representing the customer users
     */
    @GetMapping ( "/customers" )
    @PreAuthorize ( "hasRole('ADMIN')" )
    public ResponseEntity<List<UserDto>> getAllCustomers () {
        // found http status selected
        return new ResponseEntity<>( authService.getAllCustomers(), HttpStatus.OK );
    }

    /**
     * Deletes the given user. Requires the ADMIN role.
     *
     * @param id
     *            id of user to delete
     * @return response indicating success or failure
     */
    @PreAuthorize ( "hasRole('ADMIN')" )
    @DeleteMapping ( "/user/{id}" )
    public ResponseEntity<String> deleteUser ( @PathVariable ( "id" ) final Long id ) {
        authService.deleteUserById( id );
        return ResponseEntity.ok( "User deleted successfully." );
    }

    /**
     * creates a staff user -> requires ADMIN role
     *
     * @param registerDto
     *            register encapsulation of user data
     * @return response of a new staff user created
     */
    @PostMapping ( "/staff" )
    @PreAuthorize ( "hasRole('ADMIN')" )
    public ResponseEntity<UserDto> createStaff ( @RequestBody final RegisterDto registerDto ) {
        final UserDto createdStaff = authService.createStaff( registerDto );
        return new ResponseEntity<>( createdStaff, HttpStatus.CREATED );
    }

    /**
     * updates any user, staff or customer -> requires ADMIN role
     *
     * @param id
     *            unique user id
     * @param registerDto
     *            register encapsulation of user data
     * @return response of a new staff user created
     */
    @PutMapping ( "/user/{id}" )
    @PreAuthorize ( "hasRole('ADMIN')" )
    public ResponseEntity<UserDto> updateUser ( @PathVariable ( "id" ) final Long id,
            @RequestBody final RegisterDto registerDto ) {
        final UserDto updatedUser = authService.updateUser( id, registerDto );
        return new ResponseEntity<>( updatedUser, HttpStatus.OK );
    }

    /**
     * Get a particular user
     *
     * @param id
     *            The user id
     * @return the User
     */
    @GetMapping ( "/user/{id}" )
    @PreAuthorize ( "hasRole('ADMIN')" )
    public ResponseEntity<UserDto> getUser ( @PathVariable ( "id" ) final Long id ) {
        final UserDto userDto = authService.getUserById( id );
        return new ResponseEntity<>( userDto, HttpStatus.OK );
    }
}
