package com.example.workflow.data.clients;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "data_alarm", schema = "public", catalog = "clients")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DataAlarm {
    @Basic
    @Id
    @Column(name = "cash_ip")
    private String cashIp;

    @Basic
    @Column(name = "client_id")
    private int clientId;

    @Basic
    @Column(name = "shop_number")
    private int shopNumber;

    @Basic
    @Column(name = "cash_number")
    private int cashNumber;

    @Basic
    @Column(name = "count_items_err")
    private int itemsError;

    @Basic
    @Column(name = "count_mrc_err")
    private int mrcError;

    @Basic
    @Column(name = "count_check_err")
    private int checkError;
}
