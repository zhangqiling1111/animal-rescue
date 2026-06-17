package org.example.strayanimalrescuebackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Adoption {
    private int id;
    private int userId;
    private int animalId;
    private String status;
    private Date applyDate;
}
