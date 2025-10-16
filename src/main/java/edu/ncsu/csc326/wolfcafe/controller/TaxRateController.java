package edu.ncsu.csc326.wolfcafe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.TaxRateDto;
import edu.ncsu.csc326.wolfcafe.service.TaxRateService;

/**
 * Tax Rate Controller class.
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/taxrate" )
public class TaxRateController {

    /** tax rate service to use */
    @Autowired
    private TaxRateService taxRateService;

    /**
     * Gets current tax rate
     *
     * @return the response of getting the tax rate
     */
    @GetMapping
    @PreAuthorize ( "hasAnyRole('ADMIN', 'CUSTOMER', 'GUEST')" )
    public ResponseEntity<TaxRateDto> getTaxRate () {
        final TaxRateDto taxRateDto = taxRateService.getTaxRate();
        return ResponseEntity.ok( taxRateDto );
    }

    /**
     * Updates the tax rate
     *
     * @param taxRateDto
     *            dto to use
     * @return response after updating tax rate
     */
    @PutMapping
    @PreAuthorize ( "hasRole('ADMIN')" )
    public ResponseEntity<TaxRateDto> updateTaxRate ( @RequestBody final TaxRateDto taxRateDto ) {
        final TaxRateDto savedTaxRateDto = taxRateService.updateTaxRate( taxRateDto );
        return ResponseEntity.ok( savedTaxRateDto );
    }
}
