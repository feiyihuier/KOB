package com.kob.backend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLike {
    private Integer id;
    private Integer givelikeid;
    private Integer getlikeid;
    private Boolean status;
    private Date createtime;
    private Date updatetime;
}
