package com.roacg.core.web.model;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

@Data
public class PageResponse<T> implements Serializable {

    private Collection<T> content;

    private Long total;

    private boolean hasNext;

    public PageResponse<T> page(Page page) {
        this.setHasNext(page.hasNext());
        this.setTotal(page.getTotalElements());
        return this;
    }

    public static <T> PageResponse<T> of(Collection<T> content) {
        PageResponse<T> response = new PageResponse<>();
        response.setContent(content);
        return response;
    }

    public static <T> PageResponse<T> empty() {
        PageResponse<T> response = new PageResponse<>();
        response.setContent(Collections.emptyList());
        return response;
    }
}
