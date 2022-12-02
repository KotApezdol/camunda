package com.example.workflow.data.clients;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.List;

@Entity
@Table(name = "data_cash", schema = "public", catalog = "clients")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@IdClass(IpIdCash.class)
public class DataCash {

    @Column(name = "cash_ip")
    private String cashIp;

    @Id
    @Column(name = "client_id")
    private int clientId;

    @Id
    @Column(name = "shop_number")
    private int shopNumber;

    @Id
    @Column(name = "cash_number")
    private int cashNumber;

    @Type(ListArrayType.class)
    @Column(name = "array_item", columnDefinition = "text[]")
    private List<String> item;

    @Type(ListArrayType.class)
    @Column(name = "array_mrc", columnDefinition = "text[]")
    private List<String> mrc;

    @Column(name = "checked")
    private boolean checked;

    @Column(name = "product_uploaded")
    private boolean product_uploaded;

    @Column(name = "mrc_uploaded")
    private boolean mrc_uploaded;

    @Column(name = "itemscount")
    private int itemsCount;

    @Column(name = "mrccount")
    private int mrccount;

}
