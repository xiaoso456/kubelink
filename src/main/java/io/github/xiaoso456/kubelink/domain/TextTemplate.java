package io.github.xiaoso456.kubelink.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.xiaoso456.kubelink.handler.StringMapTypeMappingHandler;
import lombok.Data;

import java.util.Map;

@Data
@TableName(value ="text_template")
public class TextTemplate {


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * template id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * template name
     */
    private String name;

    /**
     * template content
     */
    private String content;

    /**
     *  template type
     */
    private String type;

    /**
     * template description
     */
    private String description;

    /**
     * template version
     */
    private Long version;

    @TableField(typeHandler = StringMapTypeMappingHandler.class)
    private Map<String,String> templateVariables;

}
