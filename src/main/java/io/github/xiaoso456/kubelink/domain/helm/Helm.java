package io.github.xiaoso456.kubelink.domain.helm;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.github.xiaoso456.kubelink.domain.helm.release.File;
import io.kubernetes.client.openapi.models.V1Secret;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Base64Util;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Data
@Builder
public class Helm {

    private static ObjectMapper objectMapper;
    static {
        objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    private String name;

    private String status;

    private String namespace;

    private String version;

    private Date modifiedAt;

    private Release release;

    public void exportZip(OutputStream outputStream)  {


        try (
             ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
            for(File file:release.getChart().getTemplates()){
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipOut.putNextEntry(zipEntry);

                byte[] decodeFileByte = Base64.getDecoder().decode(file.getData());
                zipOut.write(decodeFileByte);

                zipOut.closeEntry();
            }

            for(File file:release.getChart().getFiles()){
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipOut.putNextEntry(zipEntry);

                byte[] decodeFileByte = Base64.getDecoder().decode(file.getData());
                zipOut.write(decodeFileByte);

                zipOut.closeEntry();
            }

            DumperOptions dumperOptions = new DumperOptions();
            dumperOptions.setPrettyFlow(true);
            dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml yaml = new Yaml(dumperOptions);

            // export values.yaml
            String valuesOverrideYamlString = null;
            if(release.getChart().getValues() != null){
                valuesOverrideYamlString = yaml.dump(release.getChart().getValues());
            }else{
                valuesOverrideYamlString = "";
            }
            ZipEntry zipEntryValues = new ZipEntry("values.yaml");
            zipOut.putNextEntry(zipEntryValues);
            zipOut.write(valuesOverrideYamlString.getBytes(StandardCharsets.UTF_8));
            zipOut.closeEntry();

            // export values-override.yaml
            String valuesYamlString = null;
            if(release.getConfig() != null){
                valuesYamlString = yaml.dump(release.getChart().getValues());
            }else{
                valuesYamlString = "";
            }
            ZipEntry zipEntryValuesOverride = new ZipEntry("values-override.yaml");
            zipOut.putNextEntry(zipEntryValuesOverride);
            zipOut.write(valuesYamlString.getBytes(StandardCharsets.UTF_8));
            zipOut.closeEntry();

            // export install.sh
            String installSh = StrUtil.format("helm upgrade {} ./ -n {} --create-namespace -f values-override.yaml",release.getName(),release.getNamespace());
            ZipEntry zipEntryInstallSh = new ZipEntry("install.sh");
            zipOut.putNextEntry(zipEntryInstallSh);
            zipOut.write(installSh.getBytes(StandardCharsets.UTF_8));
            zipOut.closeEntry();
            zipOut.flush();

        } catch (IOException e) {
            log.error("", e);
        }
    }



    public static Helm fromSecret(V1Secret v1Secret) throws IOException {
        Map<String, String> labels = v1Secret.getMetadata().getLabels();
        byte[] releaseByte = decompressGzip(Base64.getDecoder().decode(v1Secret.getData().get("release")));
        Release release = objectMapper.readValue(releaseByte, Release.class);
        Helm helm = Helm.builder()
                .name(labels.get("name"))
                .namespace(v1Secret.getMetadata().getNamespace())
                .status(labels.get("status"))
                .version(labels.get("version"))
                .modifiedAt(Date.from(Instant.ofEpochSecond(Long.parseLong(labels.get("modifiedAt")))))
                .release(release)
                .build();
        return helm;
    }

    public static Helm fromSecretWithoutRelease(V1Secret v1Secret) throws IOException {
        Map<String, String> labels = v1Secret.getMetadata().getLabels();
        Helm helm = Helm.builder()
                .name(labels.get("name"))
                .namespace(v1Secret.getMetadata().getNamespace())
                .status(labels.get("status"))
                .version(labels.get("version"))
                .modifiedAt(Date.from(Instant.ofEpochSecond(Long.parseLong(labels.get("modifiedAt")))))
                .release(null)
                .build();
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
