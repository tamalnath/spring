package org.tamal.spring.model;

import java.util.ArrayList;

public class Page<T> {

    public ArrayList<T> content;
    public Pageable pageable;
    public Integer totalPages;
    public Integer totalElements;
    public Boolean last;
    public Boolean first;
    public Integer size;
    public Integer number;
    public Integer numberOfElements;
    public Sort sort;
    public Boolean empty;

    public static class Pageable {
        public Sort sort;
        public Integer offset;
        public Integer pageNumber;
        public Integer pageSize;
        public Boolean paged;
        public Boolean unpaged;
    }

    public static class Sort {
        public Boolean unsorted;
        public Boolean sorted;
        public Boolean empty;
    }

}
