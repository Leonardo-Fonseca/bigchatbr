package br.com.leofonseca.bigchatbr.queue;

public record QueueStatusDTO(
        int urgentQueueSize,
        int normalQueueSize,
        int totalEnqueued,
        int totalProcessed
) {}