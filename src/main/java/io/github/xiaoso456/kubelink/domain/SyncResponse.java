package io.github.xiaoso456.kubelink.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SyncResponse {

    private Boolean success;

    private Long spendMillisecond;

}

