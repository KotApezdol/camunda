package com.example.workflow.data.postgres;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "data_control_cash_catalog_check", schema = "public", catalog = "postgres")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DataControl {
    @Basic
    @Id
    @Column(name = "client_id")
    private Integer clientId;
    @Basic
    @Column(name = "cm_type_of_data")
    private Integer cmTypeOfData;
    @Basic
    @Column(name = "shop_number")
    private Long shopNumber;
    @Basic
    @Column(name = "cash_number")
    private Long cashNumber;
    @Basic
    @Column(name = "shop_ip")
    private String shopIp;
    @Basic
    @Column(name = "cash_ip")
    private String cashIp;
    @Basic
    @Column(name = "code")
    private String code;
    @Basic
    @Column(name = "product_marking")
    private String productMarking;
    @Basic
    @Column(name = "cm_extra_info")
    private String cmExtraInfo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataControl that = (DataControl) o;

        if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null) return false;
        if (cmTypeOfData != null ? !cmTypeOfData.equals(that.cmTypeOfData) : that.cmTypeOfData != null) return false;
        if (shopNumber != null ? !shopNumber.equals(that.shopNumber) : that.shopNumber != null) return false;
        if (cashNumber != null ? !cashNumber.equals(that.cashNumber) : that.cashNumber != null) return false;
        if (shopIp != null ? !shopIp.equals(that.shopIp) : that.shopIp != null) return false;
        if (cashIp != null ? !cashIp.equals(that.cashIp) : that.cashIp != null) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (productMarking != null ? !productMarking.equals(that.productMarking) : that.productMarking != null)
            return false;
        if (cmExtraInfo != null ? !cmExtraInfo.equals(that.cmExtraInfo) : that.cmExtraInfo != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = clientId != null ? clientId.hashCode() : 0;
        result = 31 * result + (cmTypeOfData != null ? cmTypeOfData.hashCode() : 0);
        result = 31 * result + (shopNumber != null ? shopNumber.hashCode() : 0);
        result = 31 * result + (cashNumber != null ? cashNumber.hashCode() : 0);
        result = 31 * result + (shopIp != null ? shopIp.hashCode() : 0);
        result = 31 * result + (cashIp != null ? cashIp.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (productMarking != null ? productMarking.hashCode() : 0);
        result = 31 * result + (cmExtraInfo != null ? cmExtraInfo.hashCode() : 0);
        return result;
    }
}
