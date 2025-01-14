package com.eshop.client.model;

import com.eshop.client.util.DateUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Objects;

@Data
@Accessors(chain = true)
public class NotificationModel extends BaseModel<Long> {
    private UserModel sender;
    private UserModel recipient;
    private String subject;
    private String body;
    private boolean read = false;
    private String role;
    public String getCreatedDateAgo(){
        return DateUtil.timeAgo(this.createdDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (NotificationModel) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
