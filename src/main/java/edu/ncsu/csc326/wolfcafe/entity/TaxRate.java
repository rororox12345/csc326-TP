package edu.ncsu.csc326.wolfcafe.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Tax Rate for WolfCafe. This class is the entity for tax rate which is just an
 * id and percent.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TaxRate {

    /** Tax Rate Id */
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long   id;

    /** Tax Rate Percent */
    private Double percent;

}
