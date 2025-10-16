package edu.ncsu.csc326.wolfcafe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * InventoryIngredients for the wolf cafe. InventoryIngredient is a Data Access
 * Object (DAO) is tied to the database using Hibernate libraries.
 * InventoryIngredientRepository provides the methods for database CRUD
 * operations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table ( name = "inventory_ingredients" )
public class InventoryIngredient {

    /** Ingredient id */
    @Id
    private Long id;

    /** Quantity in Inventory */
    @Column ( nullable = false )
    private int  quantity;
}
