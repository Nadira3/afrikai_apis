package com.precious.LabelAPI.dto;

import java.util.UUID;

/**
 * DTO for submitting labeling data
 */
public record ReviewRequest(UUID submissionId, Boolean review) {}
