package com.shopco.core.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtility {
    public static Pageable createPageRequest(int page, int pageSize, Sort sort){
        //API IS 1-BASED, SPRING DATA IS 0-BASED
        int zeroBased = Math.max(0, page-1 );
        return PageRequest.of(zeroBased, pageSize, sort);
    }

    public static Pageable createPageRequest(int page, int pageSize){
        return createPageRequest(page,pageSize, Sort.unsorted());
    }

}
