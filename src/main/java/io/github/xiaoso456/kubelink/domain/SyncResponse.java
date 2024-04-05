package io.github.xiaoso456.kubelink.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.xiaoso456.kubelink.enums.SyncType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SyncResponse {

    private Boolean success;

    private Long spendMillisecond;

}

