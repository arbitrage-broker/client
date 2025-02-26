package com.arbitragebroker.client.dto;


import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.util.List;
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class PageDto<M> implements Serializable {
    public PageDto(long recordsTotal, long recordsFiltered, List<M> data)
    {
        this.draw = 0;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.data = data;
    }
    private int draw;
    private long recordsTotal;
    private long recordsFiltered;
    private List<M> data;

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public long getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public List<M> getData() {
        return data;
    }

    public void setData(List<M> data) {
        this.data = data;
    }
}
