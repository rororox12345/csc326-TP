package edu.ncsu.csc326.wolfcafe.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc326.wolfcafe.entity.InventoryIngredient;

/**
 * InventoryIngredientRepository for working with the DB through the
 * JpaRepository.
 */
public interface InventoryIngredientRepository extends JpaRepository<InventoryIngredient, Long> {

    /**
     * Finds a InventoryIngredient object with the provided id. Spring will
     * generate code to make this happen. Optional lets us call .orElseThrow()
     * when a client works with the method and the value isn't found in the
     * database.
     *
     * @param id
     *            Id of the ingredient
     * @return Found InventoryIngredient, null if none.
     */
    @Override
    Optional<InventoryIngredient> findById ( Long id );
}
