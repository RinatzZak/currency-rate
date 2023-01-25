package ru.rinattzak.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class GetUpdateRequest {
    @JsonProperty("offset")
    long offset;
}
