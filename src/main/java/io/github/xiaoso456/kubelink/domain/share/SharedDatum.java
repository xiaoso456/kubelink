package io.github.xiaoso456.kubelink.domain.share;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SharedDatum {

    private String name;

    private String describe;

    private Map<String, List<Map<String,Object>>> tableData;



}
