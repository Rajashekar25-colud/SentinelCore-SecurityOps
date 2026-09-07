package com.sentinelcore.secureops.dto;

import java.util.List;

public record AIChatRequest(
    String message,
    List<ChatMessageDTO> conversation,
    String currentPage,
    String currentRoute
) {}
