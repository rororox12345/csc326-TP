package edu.ncsu.csc326.wolfcafe.service;

import edu.ncsu.csc326.wolfcafe.dto.TaxRateDto;

/**
 * Tax Rate Service class. This service is an interface for the service
 * implementation which actually gets and updates the tax rate.
 */
public interface TaxRateService {

    /**
     * Gets current tax rate
     *
     * @return the current tax rate
     */
    TaxRateDto getTaxRate ();

    /**
     * Updates the current tax rate
     *
     * @param taxRateDto
     *            the dto to use
     * @return the updated dto
     */
    TaxRateDto updateTaxRate ( TaxRateDto taxRateDto );

}
