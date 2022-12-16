package com.example.workflow.data.clients;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "data_log", schema = "public", catalog = "clients")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@IdClass(IdDataLog.class)
public class DataLog {
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
    @Column(name = "count_items")
    private int itemsCount;
    @Column(name = "count_mrc")
    private int mrccount;

    @Type(ListArrayType.class)
    @Column(name = "array_item", columnDefinition = "text[]")
    private List<String> item;

    @Type(ListArrayType.class)
    @Column(name = "array_mrc", columnDefinition = "text[]")
    private List<String> mrc;

    @Column(name = "cm_extra_info")
    private String cmExtraInfo;
}
