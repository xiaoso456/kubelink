package io.github.xiaoso456.kubelink.service;


import io.github.xiaoso456.kubelink.domain.share.SharedDatum;
import io.github.xiaoso456.kubelink.mapper.DynamicMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ShareService {

    @Autowired
    private DynamicMapper dynamicMapper;

    public SharedDatum exportClusterConfig(List<Long> ids) {
        String tableName = "cluster_config";
        SharedDatum sharedDatum = new SharedDatum();
        sharedDatum.setName("cluster_config");
        sharedDatum.setDescribe("");
        List<Map<String, Object>> exportData = dynamicMapper.exportData(tableName,ids);
        exportData.forEach(map -> {
            map.remove("id");
        });
        sharedDatum.setTableData(Map.of(tableName, exportData));
        return sharedDatum;
    }

    public SharedDatum exportSyncConfig(List<Long> ids) {
        String tableName = "sync_config";
        SharedDatum sharedDatum = new SharedDatum();
        sharedDatum.setName("sync_config");
        sharedDatum.setDescribe("");
        List<Map<String, Object>> exportData = dynamicMapper.exportData(tableName,ids);
        exportData.forEach(map -> {
            map.remove("id");
        });
        sharedDatum.setTableData(Map.of(tableName, exportData));
        return sharedDatum;
    }

    public SharedDatum exportTextTemplate(List<Long> ids) {
        String tableName = "text_template";
        SharedDatum sharedDatum = new SharedDatum();
        sharedDatum.setName("text_template");
        sharedDatum.setDescribe("");
        List<Map<String, Object>> exportData = dynamicMapper.exportData(tableName,ids);
        exportData.forEach(map -> {
            map.remove("id");
        });
        sharedDatum.setTableData(Map.of(tableName, exportData));
        return sharedDatum;
    }

    @Transactional(rollbackFor = Exception.class)
    public void importSharedDatum(SharedDatum sharedDatum) {
        log.info("import shared datum, name: {}, describe: {}",sharedDatum.getName(),sharedDatum.getDescribe());
        for (Map.Entry<String,List<Map<String,Object>>> entry : sharedDatum.getTableData().entrySet()) {
            String tableName = entry.getKey();
            List<Map<String,Object>> values = entry.getValue();
            log.info("import table: {}", tableName);
            for (Map<String,Object> field : values) {
                dynamicMapper.importData(tableName, field);
            }
        }
    }
}
