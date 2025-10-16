package edu.ncsu.csc326.wolfcafe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * System user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table ( name = "users" )
public class User {

    /** User id */
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long   id;

    /** User name */
    private String name;

    /** User username */
    @Column ( nullable = false, unique = true )
    private String username;

    /** User email */
    @Column ( nullable = false, unique = true )
    private String email;

    /** User password */
    @Column ( nullable = false )
    private String password;

    /** User role */
    @ManyToOne
    @JoinColumn ( name = "role_id", nullable = false )
    private Role   role;

}
