package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.JwtAuthResponse;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.dto.UserDto;
import edu.ncsu.csc326.wolfcafe.entity.Role;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.exception.WolfCafeAPIException;
import edu.ncsu.csc326.wolfcafe.repository.RoleRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;
import edu.ncsu.csc326.wolfcafe.service.impl.AuthServiceImpl;

/**
 * tests the AuthService class
 */
@SpringBootTest
@Transactional
class AuthServiceTest {

    /** instance of the userrepo for testing */
    @Autowired
    private UserRepository  userRepository;

    /** instance of the rolerepo for testing */
    @Autowired
    private RoleRepository  roleRepository;

    /** instance of the password encoder to encode passwords for testing */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /** instance of the authentication service for testing */
    @Autowired
    private AuthServiceImpl authService;

    /**
     * a test user object which is created with dummy info at the start of each
     * test
     */
    private User            testUser;
    /** sets the customer role to the customer string */
    private Role            customerRole;
    /** sets the staff role to the staff string */
    private Role            staffRole;
    /** sets the registerDto to create a new user */
    private RegisterDto     registerDto;
    /** sets the loginDto to login an existing user */
    private LoginDto        loginDto;
    /** contains all the info about a user except the pass */
    private UserDto         userDto;

    /**
     * sets up the register and login info for a user, sets the role types, and
     * updates the current user
     */
    @BeforeEach
    void setUp () {
        // delete the outstanding stuff
        userRepository.deleteAll();

        // first case is for customer role, in the instance of a customer grab
        // the enum
        // and set it to that (wasn't working unless it was done like this)
        customerRole = roleRepository.findByName( "ROLE_CUSTOMER" );
        if ( customerRole == null ) {
            customerRole = new Role();
            customerRole.setName( "ROLE_CUSTOMER" );
            customerRole = roleRepository.save( customerRole );
        }

        // first case is for staff role, in the instance of a staff grab the
        // enum and
        // set it to that (wasn't working unless it was done like this)
        staffRole = roleRepository.findByName( "ROLE_STAFF" );
        if ( staffRole == null ) {
            staffRole = new Role();
            staffRole.setName( "ROLE_STAFF" );
            staffRole = roleRepository.save( staffRole );
        }

        // dummy user, set up same way as in impl
        testUser = new User();
        testUser.setName( "Sparsh" );
        testUser.setUsername( "skoyam" );
        testUser.setEmail( "skoyam@abc.com" );
        testUser.setPassword( passwordEncoder.encode( "password123" ) );

        testUser.setRole( customerRole );

        // save this new user
        testUser = userRepository.save( testUser );

        // create an init the regit dto
        registerDto = new RegisterDto();
        registerDto.setName( "New User" );
        registerDto.setUsername( "newuser" );
        registerDto.setEmail( "new@example.com" );
        registerDto.setPassword( "password123" );

        // setup the login info
        loginDto = new LoginDto();
        loginDto.setUsernameOrEmail( "skoyam" );
        loginDto.setPassword( "password123" );

        // and user info
        userDto = new UserDto();
        userDto.setId( 1L );
        userDto.setName( "Staff User" );
        userDto.setUsername( "staffuser" );
        userDto.setEmail( "staff@example.com" );
    }

    /**
     * tests the register() function
     */
    @Test
    void testRegister () {
        final String result = authService.register( registerDto );

        // check
        assertEquals( "User registered successfully.", result );

        // if we cant find it set it to null, make sure its not null tho
        final User savedUser = userRepository.findByUsername( "newuser" ).orElse( null );
        assertNotNull( savedUser );
        assertEquals( "New User", savedUser.getName() );
        assertEquals( "new@example.com", savedUser.getEmail() );
        assertTrue( passwordEncoder.matches( "password123", savedUser.getPassword() ) );

        // user with the same username as someone already in the system
        final RegisterDto duplicateUsername = new RegisterDto();
        duplicateUsername.setName( "Duplicate User" );
        duplicateUsername.setUsername( "newuser" );
        duplicateUsername.setEmail( "diff@example.com" );
        duplicateUsername.setPassword( "password123" );

        // throw
        final WolfCafeAPIException e1 = assertThrows( WolfCafeAPIException.class, () -> {
            authService.register( duplicateUsername );
        } );

        assertEquals( HttpStatus.BAD_REQUEST, e1.getStatus() );
        assertEquals( "Username already exists.", e1.getMessage() );

        // user with the same email already in the system
        final RegisterDto duplicateEmail = new RegisterDto();
        duplicateEmail.setName( "Duplicate Email" );
        duplicateEmail.setUsername( "uniqueuser" );
        duplicateEmail.setEmail( "new@example.com" );
        duplicateEmail.setPassword( "password123" );

        // throw again
        final WolfCafeAPIException e2 = assertThrows( WolfCafeAPIException.class, () -> {
            authService.register( duplicateEmail );
        } );

        assertEquals( HttpStatus.BAD_REQUEST, e2.getStatus() );
        assertEquals( "Email already exists.", e2.getMessage() );
    }

    /**
     * tests the login() function
     */
    @Test
    void testLogin () {
        final JwtAuthResponse response = authService.login( loginDto );

        assertNotNull( response );
        assertNotNull( response.getAccessToken() );
        assertEquals( "ROLE_CUSTOMER", response.getRole() );
    }

    /**
     * tests the deleteUserById() function
     */
    @Test
    void testDeleteUserById () {
        authService.deleteUserById( testUser.getId() );

        assertFalse( userRepository.existsById( testUser.getId() ) );

        // test an id that dne
        final Long nonExistentId = 69420L;

        final ResourceNotFoundException exception = assertThrows( ResourceNotFoundException.class, () -> {
            authService.deleteUserById( nonExistentId );
        } );

        assertEquals( "User not found with id " + nonExistentId, exception.getMessage() );
    }

    /**
     * tests the createStaff() function
     */
    @Test
    void testCreateStaff () {
        final UserDto result = authService.createStaff( registerDto );

        assertNotNull( result );
        assertEquals( registerDto.getName(), result.getName() );
        assertEquals( registerDto.getUsername(), result.getUsername() );
        assertEquals( registerDto.getEmail(), result.getEmail() );

        // check db
        final User savedUser = userRepository.findByUsername( registerDto.getUsername() ).orElse( null );
        assertNotNull( savedUser );

        assertEquals( "ROLE_STAFF", savedUser.getRole().getName() );

        // existing username already
        final RegisterDto u = new RegisterDto();
        u.setName( "New Staff" );
        u.setUsername( "newuser" );
        u.setEmail( "newstaff@example.com" );
        u.setPassword( "abc123" );

        final WolfCafeAPIException exception = assertThrows( WolfCafeAPIException.class, () -> {
            authService.createStaff( u );
        } );

        assertEquals( HttpStatus.BAD_REQUEST, exception.getStatus() );
        assertEquals( "Username already exists.", exception.getMessage() );

        // change username to unique, email redundant
        u.setUsername( "newstaffuser" );
        u.setEmail( "new@example.com" );

        final WolfCafeAPIException exception2 = assertThrows( WolfCafeAPIException.class, () -> {
            authService.createStaff( u );
        } );

        assertEquals( HttpStatus.BAD_REQUEST, exception2.getStatus() );
        assertEquals( "Email already exists.", exception2.getMessage() );
    }

    /**
     * tests the updateUser() function
     */
    @Test
    void testUpdateUser () {
        final RegisterDto updateDto = new RegisterDto();
        updateDto.setName( "Updated Name" );
        updateDto.setUsername( "updateduser" );
        updateDto.setEmail( "updated@example.com" );

        final UserDto result = authService.updateUser( testUser.getId(), updateDto );

        assertNotNull( result );
        assertEquals( updateDto.getName(), result.getName() );
        assertEquals( updateDto.getUsername(), result.getUsername() );
        assertEquals( updateDto.getEmail(), result.getEmail() );

        // check db
        final User updatedUser = userRepository.findById( testUser.getId() ).orElse( null );
        assertNotNull( updatedUser );
        assertEquals( "Updated Name", updatedUser.getName() );
        assertEquals( "updateduser", updatedUser.getUsername() );
        assertEquals( "updated@example.com", updatedUser.getEmail() );

        // id that dne
        final Long nonExistentId = 69420L;

        final ResourceNotFoundException exception = assertThrows( ResourceNotFoundException.class, () -> {
            authService.updateUser( nonExistentId, registerDto );
        } );

        assertEquals( "User not in system.", exception.getMessage() );
    }

    /**
     * tests the getAllStaff() function
     */
    @Test
    void testGetAllStaff () {
        final User staffUser1 = new User();

        staffUser1.setName( "Staff One" );
        staffUser1.setUsername( "staff1" );
        staffUser1.setEmail( "staff1@example.com" );
        staffUser1.setPassword( passwordEncoder.encode( "password123" ) );

        staffUser1.setRole( staffRole );
        userRepository.save( staffUser1 );

        final User staffUser2 = new User();

        staffUser2.setName( "Staff Two" );
        staffUser2.setUsername( "staff2" );
        staffUser2.setEmail( "staff2@example.com" );
        staffUser2.setPassword( passwordEncoder.encode( "password123" ) );

        staffUser2.setRole( staffRole );
        userRepository.save( staffUser2 );

        final List<UserDto> result = authService.getAllStaff();

        assertNotNull( result );
        assertEquals( 2, result.size() );
        assertTrue( result.stream().anyMatch( u -> u.getUsername().equals( "staff1" ) ) );
        assertTrue( result.stream().anyMatch( u -> u.getUsername().equals( "staff2" ) ) );
    }

    /**
     * tests the getAllCustomers() function
     */
    @Test
    void testGetAllCustomers () {
        final User customerUser1 = new User();

        customerUser1.setName( "Customer One" );
        customerUser1.setUsername( "customer1" );
        customerUser1.setEmail( "customer1@example.com" );
        customerUser1.setPassword( passwordEncoder.encode( "password123" ) );

        customerUser1.setRole( customerRole );
        userRepository.save( customerUser1 );

        final User customerUser2 = new User();

        customerUser2.setName( "Customer Two" );
        customerUser2.setUsername( "customer2" );
        customerUser2.setEmail( "customer2@example.com" );
        customerUser2.setPassword( passwordEncoder.encode( "password123" ) );

        customerUser2.setRole( customerRole );
        userRepository.save( customerUser2 );

        final List<UserDto> result = authService.getAllCustomers();

        assertNotNull( result );

        // includes me and the 2 new customers
        assertEquals( 3, result.size() );
        assertTrue( result.stream().anyMatch( u -> u.getUsername().equals( "skoyam" ) ) );
        assertTrue( result.stream().anyMatch( u -> u.getUsername().equals( "customer1" ) ) );
        assertTrue( result.stream().anyMatch( u -> u.getUsername().equals( "customer2" ) ) );
    }
}
