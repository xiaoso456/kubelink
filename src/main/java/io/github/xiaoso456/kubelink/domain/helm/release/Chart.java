package io.github.xiaoso456.kubelink.domain.helm.release;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Chart {

    private List<File> templates;
    private Map<String,Object> values;
    private List<File> files;



    @JsonProperty("templates")
    public List<File> getTemplates() { return templates; }
    @JsonProperty("templates")
    public void setTemplates(List<File> value) { this.templates = value; }

    @JsonProperty("values")
    public Map<String,Object> getValues() { return values; }
    @JsonProperty("values")
    public void setValues(Map<String,Object> value) { this.values = value; }



    @JsonProperty("files")
    public List<File> getFiles() { return files; }
    @JsonProperty("files")
    public void setFiles(List<File> value) { this.files = value; }

}
