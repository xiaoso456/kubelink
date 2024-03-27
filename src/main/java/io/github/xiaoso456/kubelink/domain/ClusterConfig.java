package io.github.xiaoso456.kubelink.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;


@TableName(value ="cluster_config")
@Data
public class ClusterConfig implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * configuration set id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * config name
     */
    private String name;

    /**
     * config context
     */
    private String config;




}