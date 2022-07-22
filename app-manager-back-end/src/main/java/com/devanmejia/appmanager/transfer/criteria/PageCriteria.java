package com.devanmejia.appmanager.transfer.criteria;

import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.Min;

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

    public Pageable toPageable() {
        return PageRequest.of(page - 1, pageSize);
    }

    public Pageable toPageable(Sort sort) {
        return PageRequest.of(page - 1, pageSize, sort);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageCriteria that = (PageCriteria) o;
        return page == that.page && pageSize == that.pageSize;
    }
}
