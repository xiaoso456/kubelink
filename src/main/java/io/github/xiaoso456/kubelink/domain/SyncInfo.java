package io.github.xiaoso456.kubelink.domain;

import io.github.xiaoso456.kubelink.enums.SyncType;
import lombok.Data;

@Data
public class SyncInfo {

    private String id;

    private SyncType syncType;

    private Boolean autoSync;

    private Boolean enable;

    private String namespace;

    private String pod;

    private String container;

    private String source;

    private String target;

}

