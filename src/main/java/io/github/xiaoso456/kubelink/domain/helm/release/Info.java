package io.github.xiaoso456.kubelink.domain.helm.release;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Info {
    private String firstDeployed;
    private String lastDeployed;
    private String deleted;
    private String description;
    private String status;
    private String notes;

    @JsonProperty("first_deployed")
    public String getFirstDeployed() { return firstDeployed; }
    @JsonProperty("first_deployed")
    public void setFirstDeployed(String value) { this.firstDeployed = value; }

    @JsonProperty("last_deployed")
    public String getLastDeployed() { return lastDeployed; }
    @JsonProperty("last_deployed")
    public void setLastDeployed(String value) { this.lastDeployed = value; }

    @JsonProperty("deleted")
    public String getDeleted() { return deleted; }
    @JsonProperty("deleted")
    public void setDeleted(String value) { this.deleted = value; }

    @JsonProperty("description")
    public String getDescription() { return description; }
    @JsonProperty("description")
    public void setDescription(String value) { this.description = value; }

    @JsonProperty("status")
    public String getStatus() { return status; }
    @JsonProperty("status")
    public void setStatus(String value) { this.status = value; }

    @JsonProperty("notes")
    public String getNotes() { return notes; }
    @JsonProperty("notes")
    public void setNotes(String value) { this.notes = value; }
}
