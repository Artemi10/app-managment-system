package com.devanmejia.appmanager.transfer.criteria;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

@Getter
@Setter
public class PageCriteria {
    @Min(value = 1, message = "Page request is invalid")
    private int page;
    @Min(value = 1, message = "Page request is invalid")
    private int pageSize;

    public PageCriteria() {
        this.page = 1;
        this.pageSize = 3;
    }

    public PageCriteria(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }
}
