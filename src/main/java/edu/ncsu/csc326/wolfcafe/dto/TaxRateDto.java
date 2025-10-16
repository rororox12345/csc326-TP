package edu.ncsu.csc326.wolfcafe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for Tax Rate.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaxRateDto {

    /** Tax Rate Id */
    private Long   id;

    /** Tax Rate Percent */
    private double percent;

}
