package com.shopco.core.response;

import lombok.Data;

import java.util.List;
@Data
public class PageResponse <T> { // used for pagination
    private List<T> content;
    private int currentPage;
    private int totalPages;
    private long totalItems;
    private int  pageSize;
    private boolean hasNext;
    private boolean hasPrevious;

    public PageResponse(List<T> content, int currentPage, int totalPages, long totalItems, int pageSize, boolean hasNext, boolean hasPrevious) {
        this.content = content;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
        this.pageSize = pageSize;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }
}
