package edu.ncsu.csc326.wolfcafe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc326.wolfcafe.entity.Order;

/**
 * OrderRepository for working with the DB through the JpaRepository.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds all order for a specific user by user id.
     *
     * @param userId
     *            The id of the user whose orders to retrieve
     * @return retrieved orders
     */
    List<Order> findAllByUserId ( Long userId );
}
