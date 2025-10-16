package edu.ncsu.csc326.wolfcafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc326.wolfcafe.entity.Role;

/**
 * Repository interface for Roles.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * finds a role based on the name of the given role
     *
     * @param name
     *            name of the role
     * @return the actual role associated with that name
     */
    Role findByName ( String name );
}
