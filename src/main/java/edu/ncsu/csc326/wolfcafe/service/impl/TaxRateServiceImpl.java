package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.TaxRateDto;
import edu.ncsu.csc326.wolfcafe.entity.TaxRate;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.repository.TaxRateRepository;
import edu.ncsu.csc326.wolfcafe.service.TaxRateService;

/**
 * Tax Rate Service class. This class is the actual implementation for getting
 * and updating a tax rate.
 */
@Service
public class TaxRateServiceImpl implements TaxRateService {

    /** Tax rate repository to use in the implementation */
    @Autowired
    private TaxRateRepository taxRateRepository;

    @Override
    public TaxRateDto getTaxRate () {
        final List<TaxRate> taxRateList = taxRateRepository.findAll();
        if ( taxRateList.isEmpty() ) {
            throw new ResourceNotFoundException( "No Tax Rate found" );
        }
        final TaxRate taxRate = taxRateList.getFirst();

        return new TaxRateDto( taxRate.getId(), taxRate.getPercent() );
    }

    @Override
    public TaxRateDto updateTaxRate ( final TaxRateDto taxRateDto ) {
        if ( taxRateDto == null ) {
            throw new IllegalArgumentException( "TaxRateDto cannot be null" );
        }

        if ( taxRateDto.getPercent() < 0 ) {
            throw new IllegalArgumentException( "Percent cannot be less than 0" );
        }

        final List<TaxRate> taxRateList = taxRateRepository.findAll();
        if ( taxRateList.isEmpty() ) {
            throw new ResourceNotFoundException( "No Tax Rate found" );
        }
        final TaxRate taxRate = taxRateList.getFirst();

        taxRate.setPercent( taxRateDto.getPercent() );
        final TaxRate savedTaxRate = taxRateRepository.save( taxRate );

        return new TaxRateDto( savedTaxRate.getId(), savedTaxRate.getPercent() );
    }
}
