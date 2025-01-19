package com.arbitragebroker.client.model;

import com.arbitragebroker.client.util.DateUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.StringJoiner;

import static com.arbitragebroker.client.util.StringUtils.shortenText;

@Data
@Accessors(chain = true)
public class NotificationModel extends BaseModel<Long> {
    @NotNull
    private UserModel sender;
    private UserModel recipient;
    @NotNull
    private String subject;
    @NotNull
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if(sender!=null)
            builder.append("Sender: %s\n".formatted(sender.getSelectTitle()));
        if(StringUtils.hasLength(subject))
            builder.append("Subject: %s\n".formatted(subject));
        if(StringUtils.hasLength(body))
            builder.append("Body: ").append(shortenText(body));
       return builder.toString();
    }
}
