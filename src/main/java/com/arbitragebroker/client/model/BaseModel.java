package com.arbitragebroker.client.model;

import com.arbitragebroker.client.validation.Update;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BaseModel<ID extends Serializable> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(groups = Update.class)
    private ID id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime modifiedDate;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime createdDate;
    protected int version;
    private String selectTitle;
}
