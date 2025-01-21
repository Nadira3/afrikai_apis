package com.precious.TaskApi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.precious.TaskApi.dto.DataImportRequest;
import com.precious.TaskApi.dto.DataImportResponse;


@FeignClient(name = "LABEL-SERVICE")
public interface LabelServiceClient {
    @PostMapping(value = "/api/labels/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<DataImportResponse> importData(@ModelAttribute DataImportRequest request);
}
