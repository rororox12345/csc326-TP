package edu.ncsu.csc326.wolfcafe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Item for data transfer.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    /** TODO */
    private Long   id;
    /** TODO */
    private String name;
    /** TODO */
    private String description;
    /** TODO */
    private double price;
}
