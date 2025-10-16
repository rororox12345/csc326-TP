package edu.ncsu.csc326.wolfcafe.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.TestUtils;
import edu.ncsu.csc326.wolfcafe.dto.TaxRateDto;
import edu.ncsu.csc326.wolfcafe.entity.TaxRate;
import edu.ncsu.csc326.wolfcafe.repository.TaxRateRepository;
import edu.ncsu.csc326.wolfcafe.service.TaxRateService;

/**
 * Test class for TaxRateController.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TaxRateControllerTest {

    /** mock mvc to use */
    @Autowired
    private MockMvc           mvc;

    /** tax rate repository to use */
    @Autowired
    private TaxRateRepository taxRateRepository;

    /** tax rate service to use */
    @Autowired
    private TaxRateService    taxRateService;

    /** set up method that resets the repository each test */
    @BeforeEach
    public void setUp () {
        taxRateRepository.deleteAll();
    }

    /**
     * tests update tax rate method
     *
     * @throws Exception
     *             if tax rate is not found
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testUpdateTaxRate () throws Exception {

        final TaxRate mockTaxRate = new TaxRate( 1L, 7.5 );

        taxRateRepository.save( mockTaxRate );
        // Create initial tax rate
        final TaxRateDto initialTaxRate = new TaxRateDto( 1L, 5.0 );
        taxRateService.updateTaxRate( initialTaxRate );

        // Verify initial tax rate
        mvc.perform( get( "/api/taxrate" ) ).andDo( print() ).andExpect( status().isOk() )
                .andExpect( content().json( "{\"percent\":5.0}" ) );

        // Update tax rate
        final TaxRateDto updatedTaxRate = new TaxRateDto( 1L, 7.5 );
        mvc.perform( put( "/api/taxrate" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( updatedTaxRate ) ).accept( MediaType.APPLICATION_JSON ) )
                .andDo( print() ).andExpect( status().isOk() ).andExpect( content().json( "{\"percent\":7.5}" ) );

    }
}
