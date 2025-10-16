package edu.ncsu.csc326.wolfcafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc326.wolfcafe.entity.Item;

/**
 * Repository interface for Items.
 */
public interface ItemRepository extends JpaRepository<Item, Long> {
}
