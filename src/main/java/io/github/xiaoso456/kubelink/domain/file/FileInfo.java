package io.github.xiaoso456.kubelink.domain.file;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileInfo {

    private String name;

    private boolean dir;

}
