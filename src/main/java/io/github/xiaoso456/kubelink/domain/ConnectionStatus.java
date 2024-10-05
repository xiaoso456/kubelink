package io.github.xiaoso456.kubelink.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.kubernetes.client.openapi.models.VersionInfo;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;


@Data
@Builder
public class ConnectionStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * configuration id
     */
    private Long id;

    /**
     * config name
     */
    private String name;

    /**
     * connection status
     * healthy,unhealthy,connecting,unknown
     */
    private String status;

    public static String UNHEALTHY = "unhealthy";
    public static String HEALTHY = "healthy";
    public static String CONNECTING = "connecting";
    public static String UNKNOWN = "unknown";

    private VersionInfo clusterVersionInfo;

}