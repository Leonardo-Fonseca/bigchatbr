package br.com.leofonseca.bigchatbr.service.queue;

public record QueueStatusDTO(
        int urgentQueueSize,
        int normalQueueSize,
        int totalEnqueued,
        int totalProcessed
) {}