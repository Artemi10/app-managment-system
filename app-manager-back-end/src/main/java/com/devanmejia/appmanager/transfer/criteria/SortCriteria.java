package com.devanmejia.appmanager.transfer.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SortCriteria {
    private String value;
    private boolean isDescending;

    public SortCriteria() {
        this.value = "id";
        this.isDescending = true;
    }

    public SortCriteria(String value, boolean isDescending) {
        this.value = value;
        this.isDescending = isDescending;
    }
}
