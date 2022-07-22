package com.devanmejia.appmanager.transfer.criteria;

import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.util.Objects;

@Setter
public class SortCriteria {
    private String sortValue;
    private OrderType orderType;

    public SortCriteria() {
        this.sortValue = "id";
        this.orderType = OrderType.DESC;
    }

    public SortCriteria(String sortValue, OrderType orderType) {
        this.sortValue = sortValue;
        this.orderType = orderType;
    }

    public Sort toSort() {
        var sortParam = Sort.by(sortValue);
        return switch (orderType) {
            case ASC -> sortParam.ascending();
            case DESC -> sortParam.descending();
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SortCriteria that = (SortCriteria) o;
        return Objects.equals(sortValue, that.sortValue) && orderType == that.orderType;
    }

    public enum OrderType {
        DESC, ASC
    }
}
