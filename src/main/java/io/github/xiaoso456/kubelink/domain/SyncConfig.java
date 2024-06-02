package io.github.xiaoso456.kubelink.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.xiaoso456.kubelink.enums.SyncType;
import lombok.Data;

@Data
@TableName(value ="sync_config")
public class SyncConfig {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private SyncType syncType;

    private Boolean autoSync;

    private Boolean enable;

    private String namespace;

    private String pod;

    private String container;

    private String source;

    private String target;

}

