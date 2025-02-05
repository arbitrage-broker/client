package com.arbitragebroker.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class PagedResponse<T> implements Serializable {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public PagedResponse() {
    }

    @JsonCreator
    public PagedResponse(
            @JsonProperty("content") List<T> content,
            @JsonProperty("pageNumber") int pageNumber,
            @JsonProperty("pageSize") int pageSize,
            @JsonProperty("totalElements") long totalElements,
            @JsonProperty("totalPages") int totalPages,
            @JsonProperty("last") boolean last) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }


    public static <T> PagedResponse<T> fromPage(org.springframework.data.domain.Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
    public Page<T> toPage() {
        return new PageImpl<>(content, PageRequest.of(pageNumber, pageSize), totalElements);
    }

    public <U> PagedResponse<U> map(Function<? super T, ? extends U> converter) {
        List<U> mappedContent = this.content.stream()
                .map(converter)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                mappedContent,
                this.pageNumber,
                this.pageSize,
                this.totalElements,
                this.totalPages,
                this.last
        );
    }
}
