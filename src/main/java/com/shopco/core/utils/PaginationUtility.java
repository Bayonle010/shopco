package com.shopco.core.utils;

import com.shopco.core.exception.IllegalArgumentException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.LinkedHashMap;
import java.util.Map;

public class PaginationUtility {



    public static final Sort DEFAULT_SORT = Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));

    private static final int MAX_PAGE_SIZE = 35;
    private static final int MAX_PAGE = 1000;

    /** Normalize page size (1..MAX_PAGE_SIZE) */
    public static int resolvePageSize(int requestedSize) {
        if (requestedSize <= 0) return MAX_PAGE_SIZE;
        return Math.min(requestedSize, MAX_PAGE_SIZE);
    }

    public static int resolvePage(int requestedPage) {
        if (requestedPage <= 0) return MAX_PAGE;
        return Math.min(requestedPage, MAX_PAGE);
    }




    public static Pageable createPageRequest(int page, int pageSize, Sort sort){
        int size = resolvePageSize((int) pageSize);
        int pageNum = resolvePage((int) page);
        int zeroBased = Math.max(0, pageNum-1 );
        return PageRequest.of(zeroBased, pageSize, sort);
    }

    public static Pageable createPageRequest(int page, int pageSize){
        int size = resolvePageSize((int) pageSize);
        int zeroBased = Math.max(0, page-1 );
        return PageRequest.of(zeroBased, pageSize, DEFAULT_SORT);
    }

    public static <T> Map<String, Object> buildPaginationMetadata(Page<T> page) {
        Map<String, Object> meta = new LinkedHashMap<>();

        int currentPage = page.getNumber() + 1; // Spring Page is 0-based
        int pageSize = page.getSize();
        long totalItems = page.getTotalElements();
        int totalPages = page.getTotalPages();

        boolean hasNext = page.hasNext();
        boolean hasPrevious = page.hasPrevious();

        Integer nextPage = hasNext ? currentPage + 1 : null;
        Integer prevPage = hasPrevious ? currentPage - 1 : null;

        meta.put("page", currentPage);
        meta.put("pageSize", pageSize);
        meta.put("totalItems", totalItems);
        meta.put("totalPages", totalPages);
        meta.put("hasNext", hasNext);
        meta.put("hasPrevious", hasPrevious);
        meta.put("nextPage", nextPage);
        meta.put("prevPage", prevPage);
        meta.put("sort", page.getSort().toString());

        return meta;
    }




}
