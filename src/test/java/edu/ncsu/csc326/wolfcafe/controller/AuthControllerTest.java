package edu.ncsu.csc326.wolfcafe.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.TestUtils;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;

/** tests the AuthController class */
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    /** given admin pass */
    @Value ( "${app.admin-user-password}" )
    private String  adminUserPassword;

    /** mock mvc */
    @Autowired
    private MockMvc mvc;

    /**
     * tests login as an admin
     *
     * @throws Exception
     *             if we can't login
     */
    @Test
    @Transactional
    public void testLoginAdmin () throws Exception {
        final LoginDto loginDto = new LoginDto( "admin", adminUserPassword );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_ADMIN" ) );
    }

    /**
     * tests creating a customer, then logging in with that info
     *
     * @throws Exception
     *             if we can't login
     */
    @Test
    @Transactional
    public void testCreateCustomerAndLogin () throws Exception {
        final RegisterDto registerDto = new RegisterDto( "Jordan Estes", "jestes", "vitae.erat@yahoo.edu",
                "JXB16TBD4LC" );

        mvc.perform( post( "/api/auth/register" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( registerDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andExpect( content().string( "User registered successfully." ) );

        final LoginDto loginDto = new LoginDto( "jestes", "JXB16TBD4LC" );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_CUSTOMER" ) );
    }

    /**
     * tests creating a staff, then logging in with that info
     *
     * @throws Exception
     *             if we can't login
     */
    @Test
    @Transactional
    public void testCreateStaffAndLogin () throws Exception {
        // login as admin
        final LoginDto loginDto = new LoginDto( "admin", adminUserPassword );

        final String token = mvc
                .perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( loginDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_ADMIN" ) ).andReturn().getResponse().getContentAsString();

        // make a new staff member using the correct endpoint
        final RegisterDto registerDto = new RegisterDto( "Sparsh Koyambreth", "skoyamb", "skoyamb@ncsu.edu",
                "Pass12345" );

        // have to grab the token like this cause of the @ tag for the
        // protection for admin usage
        mvc.perform( post( "/api/auth/staff" )
                .header( "Authorization",
                        "Bearer " + token.substring( token.indexOf( "accessToken\":\"" ) + 14,
                                token.indexOf( "\",\"tokenType\"" ) ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( registerDto ) )
                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isCreated() )
                .andExpect( jsonPath( "$.username" ).value( "skoyamb" ) );

        // log in as staff to confirm everything works
        final LoginDto staffLogin = new LoginDto( "skoyamb", "Pass12345" );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( staffLogin ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_STAFF" ) );
    }

    /**
     * tests deleting a customer
     *
     * @Exception if customer dne
     */
    @Test
    @Transactional
    public void testDeleteCustomer () throws Exception {
        final RegisterDto registerDto = new RegisterDto( "Test Customer", "tcust", "tcust@email.com", "delpass123" );
        mvc.perform( post( "/api/auth/register" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( registerDto ) ) ).andExpect( status().isCreated() );

        final LoginDto adminLogin = new LoginDto( "admin", adminUserPassword );
        final String token = mvc
                .perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( adminLogin ) ) )
                .andReturn().getResponse().getContentAsString();

        final String jwt = token.split( "\"accessToken\":\"" )[1].split( "\"" )[0];

        mvc.perform( delete( "/api/auth/user/{id}", 1L ).header( "Authorization", "Bearer " + jwt ) )
                .andExpect( status().isOk() ).andExpect( content().string( "User deleted successfully." ) );
    }

    /**
     * tests deleting a staff member
     *
     * @throws Exception
     *             if staff member dne
     */
    @Test
    @Transactional
    public void testDeleteStaff () throws Exception {
        final LoginDto adminLogin = new LoginDto( "admin", adminUserPassword );
        final String token = mvc
                .perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( adminLogin ) ) )
                .andReturn().getResponse().getContentAsString();

        final String jwt = token.split( "\"accessToken\":\"" )[1].split( "\"" )[0];

        final RegisterDto staffDto = new RegisterDto( "Delete Staff", "delstaff", "delstaff@ncsu.edu", "delstaff123" );
        mvc.perform( post( "/api/auth/staff" ).header( "Authorization", "Bearer " + jwt )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( staffDto ) ) )
                .andExpect( status().isCreated() );

        mvc.perform( delete( "/api/auth/user/{id}", 1L ).header( "Authorization", "Bearer " + jwt ) )
                .andExpect( status().isOk() ).andExpect( content().string( "User deleted successfully." ) );
    }

    /**
     * updates a current customer
     *
     * @throws Exception
     *             if customer dne
     */
    @Test
    @Transactional
    public void testUpdateCustomer () throws Exception {
        final RegisterDto registerDto = new RegisterDto( "Test Customer", "tcust", "tcust@email.com", "delpass123" );
        mvc.perform( post( "/api/auth/register" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( registerDto ) ) ).andExpect( status().isCreated() );

        final LoginDto adminLogin = new LoginDto( "admin", adminUserPassword );
        final String token = mvc
                .perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( adminLogin ) ) )
                .andReturn().getResponse().getContentAsString();

        final String jwt = token.split( "\"accessToken\":\"" )[1].split( "\"" )[0];

        final RegisterDto updated = new RegisterDto( "New Name", "newcust", "updated@email.com", "newpass" );
        mvc.perform( put( "/api/auth/user/{id}", 1L ).header( "Authorization", "Bearer " + jwt )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( updated ) ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.name" ).value( "New Name" ) );

    }

    /**
     * updates a staff member
     *
     * @throws Exception
     *             if staff member dne
     */
    @Test
    @Transactional
    public void testUpdateStaff () throws Exception {
        final LoginDto adminLogin = new LoginDto( "admin", adminUserPassword );
        final String token = mvc
                .perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( adminLogin ) ) )
                .andReturn().getResponse().getContentAsString();

        final String jwt = token.split( "\"accessToken\":\"" )[1].split( "\"" )[0];

        final RegisterDto staffDto = new RegisterDto( "Delete Staff", "delstaff", "delstaff@ncsu.edu", "delstaff123" );
        mvc.perform( post( "/api/auth/staff" ).header( "Authorization", "Bearer " + jwt )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( staffDto ) ) )
                .andExpect( status().isCreated() );

        final RegisterDto updated = new RegisterDto( "Updated Staff", "updstaff", "newstaff@ncsu.edu", "newstaffpass" );
        mvc.perform( put( "/api/auth/user/{id}", 1L ).header( "Authorization", "Bearer " + jwt )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( updated ) ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.email" ).value( "newstaff@ncsu.edu" ) );
    }

    /**
     * gets all the staff in the db
     *
     * @throws Exception
     *             if we can't get
     */
    @Test
    @Transactional
    public void testGetAllStaff () throws Exception {
        final LoginDto adminLogin = new LoginDto( "admin", adminUserPassword );
        final String token = mvc
                .perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( adminLogin ) ) )
                .andReturn().getResponse().getContentAsString();

        final String jwt = token.split( "\"accessToken\":\"" )[1].split( "\"" )[0];

        mvc.perform( get( "/api/auth/staff" ).header( "Authorization", "Bearer " + jwt ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$" ).isArray() );
    }

    /**
     * gets all the customers in the db
     *
     * @throws Exception
     *             if we can't get
     */
    @Test
    @Transactional
    public void testGetAllCustomers () throws Exception {
        final LoginDto adminLogin = new LoginDto( "admin", adminUserPassword );
        final String token = mvc
                .perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( adminLogin ) ) )
                .andReturn().getResponse().getContentAsString();

        final String jwt = token.split( "\"accessToken\":\"" )[1].split( "\"" )[0];

        mvc.perform( get( "/api/auth/customers" ).header( "Authorization", "Bearer " + jwt ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$" ).isArray() );
    }

}
