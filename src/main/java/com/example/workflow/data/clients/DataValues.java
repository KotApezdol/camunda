package com.example.workflow.data.clients;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cm_type_of_data_values", schema = "public", catalog = "clients")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DataValues {

    @Id
    @Column(name = "cm_type_of_data")
    private int cmTypeOfData;

    @Column(name = "value")
    private String value;
}
