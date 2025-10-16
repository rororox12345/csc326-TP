package edu.ncsu.csc326.wolfcafe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ingredient for the coffee maker. Ingredient is a Data Access Object (DAO) is
 * tied to the database using Hibernate libraries. IngredientRepository provides
 * the methods for database CRUD operations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table ( name = "ingredients" )
public class Ingredient {

    /** Ingredient Id */
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long   id;

    /** Ingredient name */
    @Column ( unique = true, nullable = false )
    private String name;

}
