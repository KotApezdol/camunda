package com.example.workflow.data.clients;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "data_alarm", schema = "public", catalog = "clients")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@IdClass(IdAlarm.class)
public class DataAlarm {
    @Column(name = "import_date")
    private LocalDate importDate;

    @Id
    @Column(name = "client")
    private String client;

    @Id
    @Column(name = "shop_number")
    private int shopNumber;

    @Id
    @Column(name = "cash_number")
    private int cashNumber;

    @Id
    @Column(name = "cm_type_of_data")
    private String cmTypeOfData;

    @Basic
    @Column(name = "count_type_of_data")
    private int countTypeOfData;

}
