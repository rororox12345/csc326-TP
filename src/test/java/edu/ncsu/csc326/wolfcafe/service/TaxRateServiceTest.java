package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.TaxRateDto;
import edu.ncsu.csc326.wolfcafe.entity.TaxRate;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.repository.TaxRateRepository;
import edu.ncsu.csc326.wolfcafe.service.impl.TaxRateServiceImpl;

/**
 * Tax Rate Service test
 */
@SpringBootTest
public class TaxRateServiceTest {

    /** tax rate repository to use */
    @Autowired
    private TaxRateRepository  taxRateRepository;

    /** tax rate service to use */
    @Autowired
    private TaxRateServiceImpl taxRateService;

    @BeforeEach
    void setUp () {
        taxRateRepository.deleteAll();
    }

    /**
     * Successful get tax rate test
     */
    @Test
    @Transactional
    void testGetTaxRateSuccess () {
        assertTrue( taxRateRepository.findAll().isEmpty() );

        // Mock repository response
        final TaxRate mockTaxRate = new TaxRate( 1L, 7.5 );
        // when( taxRateRepository.findById( 1L ) ).thenReturn( Optional.of(
        // mockTaxRate ) );

        taxRateRepository.save( mockTaxRate );

        assertTrue( !taxRateRepository.findAll().isEmpty() );

        // Call service method
        final TaxRateDto result = taxRateService.getTaxRate();

        // Assertions
        assertNotNull( result );
        assertEquals( 7.5, result.getPercent() );
    }

    /**
     * Tests a tax rate that doesn't exist
     */
    @Test
    @Transactional
    void testGetTaxRateNotFound () {

        assertThrows( ResourceNotFoundException.class, () -> taxRateService.getTaxRate() );
    }

    /**
     * Tests update tax rate service
     */
    @Test
    @Transactional
    void testUpdateTaxRateSuccess () {
        // Mock incoming DTO

        final TaxRate mockTaxRate = new TaxRate( 1L, 7.5 );
        taxRateRepository.save( mockTaxRate );

        final TaxRateDto newTaxRateDto = new TaxRateDto( 1L, 8.0 );

        // Call service method
        final TaxRateDto result = taxRateService.updateTaxRate( newTaxRateDto );

        // Assertions
        assertNotNull( result );
        assertEquals( 8.0, result.getPercent() );
    }

    /**
     * Tests update tax rate with null input
     */
    @Test
    @Transactional
    void testUpdateTaxRateNullInput () {
        // Call service method with null input
        final Exception exception = assertThrows( IllegalArgumentException.class,
                () -> taxRateService.updateTaxRate( null ) );

        // Assertions
        assertEquals( "TaxRateDto cannot be null", exception.getMessage() );
    }
}
