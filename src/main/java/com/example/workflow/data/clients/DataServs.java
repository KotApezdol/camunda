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
@Table(name = "data_servs", schema = "public", catalog = "clients")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DataServs {
    @Basic
    @Id
    @Column(name = "shop_ip")
    private String shopIp;

    @Basic
    @Column(name = "client_id")
    private int clientId;

    @Basic
    @Column(name = "shop_number")
    private int shopNumber;

    @Type(ListArrayType.class)
    @Column(name = "array_cashes", columnDefinition = "text[]")
    private List<String> cashes;

    @Basic
    @Column(name = "checked")
    private boolean checked;

}
