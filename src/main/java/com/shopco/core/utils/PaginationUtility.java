package com.shopco.core.utils;

import com.shopco.core.exception.IllegalArgumentException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtility {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));

    public static Pageable createPageRequest(int page, int pageSize, Sort sort){
        //API IS 1-BASED, SPRING DATA IS 0-BASED
        if (pageSize == 0){
            throw new IllegalArgumentException("page size must not be less than zero");
        }
        int zeroBased = Math.max(0, page-1 );
        return PageRequest.of(zeroBased, pageSize, sort);
    }

    public static Pageable createPageRequest(int page, int pageSize){
        //API IS 1-BASED, SPRING DATA IS 0-BASED
        if (pageSize == 0){
            throw new IllegalArgumentException("page size must not be less than zero");
        }
        int zeroBased = Math.max(0, page-1 );
        return PageRequest.of(zeroBased, pageSize, DEFAULT_SORT);
    }

    


}
