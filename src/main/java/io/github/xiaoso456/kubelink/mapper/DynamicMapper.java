package io.github.xiaoso456.kubelink.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DynamicMapper {


    List<Map<String,Object>> exportData(String tableName,List<Long> ids);


    void importData(String tableName, Map<String,Object> field);

}
