package io.github.xiaoso456.kubelink.domain.helm;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import io.kubernetes.client.openapi.models.V1Secret;
import lombok.Builder;
import lombok.Data;
import org.apache.logging.log4j.util.Base64Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.zip.GZIPInputStream;

@Data
@Builder
public class Helm {

    private String name;

    private String status;

    private String version;

    private Date modifiedAt;

    public static Helm fromSecret(V1Secret v1Secret){
        Map<String, String> labels = v1Secret.getMetadata().getLabels();

        Helm helm = Helm.builder()
                .name(labels.get("name"))
                .status(labels.get("status"))
                .version(labels.get("version"))
                .modifiedAt(Date.from(Instant.ofEpochSecond(Long.parseLong(labels.get("modifiedAt")))))
                .build();
        String releaseInfo = new String(v1Secret.getData().get("release"));
        byte[] releases = decompressGzip(Base64.getDecoder().decode(v1Secret.getData().get("release")));
        String s = new String(releases);
        return helm;
    }

    private static byte[] decompressGzip(byte[] gzipData)  {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(gzipData);
             GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = gzipInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
