package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
import edu.ncsu.csc326.wolfcafe.security.JwtTokenProvider;
import edu.ncsu.csc326.wolfcafe.service.AuthService;

/**
 * Implemented AuthService
 */
@Service
public class AuthServiceImpl implements AuthService {

    /** Instance of user repo for testing */
    @Autowired
    private UserRepository        userRepository;

    /** Instance of role repo for testing */
    @Autowired
    private RoleRepository        roleRepository;

    /** Instance to encode passwords */
    @Autowired
    private PasswordEncoder       passwordEncoder;

    /** Instance to manage authentication routes */
    @Autowired
    private AuthenticationManager authenticationManager;

    /** Instance to get the token after the login */
    @Autowired
    private JwtTokenProvider      jwtTokenProvider;

    /**
     * Registers the given user
     *
     * @param registerDto
     *            new user information
     * @return message for success or failure
     */
    @Override
    public String register ( final RegisterDto registerDto ) {
        // Check for duplicates - username
        if ( userRepository.existsByUsername( registerDto.getUsername() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Username already exists." );
        }
        // Check for duplicates - email
        if ( userRepository.existsByEmail( registerDto.getEmail() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Email already exists." );
        }

        final User user = new User();
        user.setName( registerDto.getName() );
        user.setUsername( registerDto.getUsername() );
        user.setEmail( registerDto.getEmail() );
        user.setPassword( passwordEncoder.encode( registerDto.getPassword() ) );

        final Role userRole = roleRepository.findByName( "ROLE_CUSTOMER" );

        user.setRole( userRole );

        userRepository.save( user );

        return "User registered successfully.";
    }

    /**
     * Logins in the given user
     *
     * @param loginDto
     *            username/email and password
     * @return response with authenticated user
     */
    @Override
    public JwtAuthResponse login ( final LoginDto loginDto ) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken( loginDto.getUsernameOrEmail(), loginDto.getPassword() ) );

        SecurityContextHolder.getContext().setAuthentication( authentication );

        final String token = jwtTokenProvider.generateToken( authentication );

        final Optional<User> userOptional = userRepository.findByUsernameOrEmail( loginDto.getUsernameOrEmail(),
                loginDto.getUsernameOrEmail() );

        final String role = userOptional.get().getRole().getName();

        final JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setRole( role );
        jwtAuthResponse.setAccessToken( token );

        return jwtAuthResponse;
    }

    /**
     * Deletes the given user by id
     *
     * @param id
     *            id of user to delete
     */
    @Override
    public void deleteUserById ( final Long id ) {
        userRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "User not found with id " + id ) );
        userRepository.deleteById( id );
    }

    /**
     * creates a new staff user
     *
     * @param registerDto
     *            new user dto
     * @return userDto object
     */
    @Override
    public UserDto createStaff ( final RegisterDto registerDto ) {
        // check for dup username, id, email
        if ( userRepository.existsByUsername( registerDto.getUsername() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Username already exists." );
        }
        else if ( userRepository.existsByEmail( registerDto.getEmail() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Email already exists." );
        }

        // create new user
        final User u = new User();

        // save fields for now
        final String name = registerDto.getName();
        final String email = registerDto.getEmail();
        final String username = registerDto.getUsername();

        // assign it to staff fields, dont save the password
        u.setName( name );
        u.setEmail( email );
        u.setUsername( username );

        // special case for the user roles
        final Role staffRole = roleRepository.findByName( "ROLE_STAFF" );

        u.setRole( staffRole );

        u.setPassword( passwordEncoder.encode( registerDto.getPassword() ) );

        // save it to repo
        final User newUser = userRepository.save( u );

        // back to dto for response and ret
        final UserDto uDto = new UserDto();

        // update all the fields and ret the new dto
        uDto.setName( newUser.getName() );
        uDto.setEmail( newUser.getEmail() );
        uDto.setId( newUser.getId() );
        uDto.setUsername( newUser.getUsername() );
        uDto.setRole( staffRole );

        return uDto;
    }

    /**
     * updates a current user in the system (only staff)
     *
     * @param id
     *            -> long rep of unique id
     * @param registerDto
     *            encapsulation of user info
     * @return new updated user
     */
    @Override
    public UserDto updateUser ( final Long id, final RegisterDto registerDto ) {
        // check to see if the user in the system
        if ( !userRepository.existsById( id ) ) {
            throw new ResourceNotFoundException( "User not in system." );
        }

        // find the user by the id given
        final User u = userRepository.findById( id ).get();
        // if the user is in the system -> check and grab

        u.setEmail( registerDto.getEmail() );
        u.setName( registerDto.getName() );
        u.setUsername( registerDto.getUsername() );

        if ( registerDto.getPassword() != null && !registerDto.getPassword().equals( "" ) ) {
            u.setPassword( passwordEncoder.encode( registerDto.getPassword() ) );
        }

        // save it to repo
        final User newUser = userRepository.save( u );

        // back to dto for response and ret
        final UserDto uDto = new UserDto();

        // update all the fields and ret the new dto
        uDto.setName( newUser.getName() );
        uDto.setEmail( newUser.getEmail() );
        uDto.setId( newUser.getId() );
        uDto.setUsername( newUser.getUsername() );

        return uDto;
    }

    /**
     * gets all the staff in the system
     *
     * @return list of all the staff
     */
    @Override
    public List<UserDto> getAllStaff () {
        final Role r = roleRepository.findByName( "ROLE_STAFF" );

        final List<User> staffList = userRepository.findAllByRole( r );
        final List<UserDto> staffDtoList = new ArrayList<>();

        // iter through the staff
        for ( final User staff : staffList ) {
            // create a new dto and set like normal how we did before
            final UserDto staffDto = new UserDto();
            staffDto.setId( staff.getId() );
            staffDto.setName( staff.getName() );
            staffDto.setEmail( staff.getEmail() );
            staffDto.setUsername( staff.getUsername() );
            staffDto.setRole( staff.getRole() );

            // add the new one to the dto list and ret that list
            staffDtoList.add( staffDto );
        }

        return staffDtoList;
    }

    /**
     * gets all the customers in the system
     *
     * @return list of all the customers
     */
    @Override
    public List<UserDto> getAllCustomers () {
        final Role r = roleRepository.findByName( "ROLE_CUSTOMER" );

        final List<User> customerList = userRepository.findAllByRole( r );
        final List<UserDto> customerDtoList = new ArrayList<>();

        // iter through the staff
        for ( final User customer : customerList ) {
            // create a new dto and set like normal how we did before
            final UserDto customerDto = new UserDto();
            customerDto.setId( customer.getId() );
            customerDto.setName( customer.getName() );
            customerDto.setEmail( customer.getEmail() );
            customerDto.setUsername( customer.getUsername() );
            customerDto.setRole( customer.getRole() );

            // add the new one to the dto list and ret that list
            customerDtoList.add( customerDto );
        }

        return customerDtoList;
    }

    /**
     * Gets the corresponding user id for give user name
     *
     * @param username
     *            The username of the User
     * @return The userId
     */
    @Override
    public Long getUserIdFromUsername ( final String username ) {
        return userRepository.findByUsername( username ).get().getId();
    }

    /**
     * Get a specific user
     *
     * @param userId
     *            The User id
     * @return corresponding userDto
     */
    @Override
    public UserDto getUserById ( final Long userId ) {
        final User user = userRepository.findById( userId ).get();
        final UserDto userDto = new UserDto( user.getId(), user.getName(), user.getUsername(), user.getEmail(),
                user.getRole() );
        return userDto;
    }

}
