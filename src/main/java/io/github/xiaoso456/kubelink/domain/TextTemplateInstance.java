package io.github.xiaoso456.kubelink.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.xiaoso456.kubelink.handler.StringMapTypeMappingHandler;
import lombok.Data;

import java.util.Map;

@Data
@TableName(value ="text_template_instance")
public class TextTemplateInstance {


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * instance  id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * template id
     */
    private Long templateId;

    /**
     * template version
     */
    private Long templateVersion;

    /**
     * template instance name
     */
    private String name;

    /**
     * template instance content
     */
    private String content;


    /**
     * template description
     */
    private String description;





}
