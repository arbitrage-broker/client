package com.eshop.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class FileModel {
    private String name;
    private LocalDateTime modifiedDate;
    public String getUrl(){
        return "api/v1/files/" + name;
    }
}
