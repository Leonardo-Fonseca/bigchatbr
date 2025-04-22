package br.com.leofonseca.bigchatbr.service.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class MessageQueue {
    // Fila nao bloqueante para urgentes e normais
    private final ConcurrentLinkedDeque<Long> urgentQueue = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<Long> normalQueue = new ConcurrentLinkedDeque<>();
    // Semáforo para bloquear consumidor quando fila vazia
    private final Semaphore available = new Semaphore(0);

    private static final int MAX_URGENTS = 4;
    private int cycleCount = 0;

    // Estatísticas
    private final AtomicInteger totalEnqueued = new AtomicInteger(0);
    private final AtomicInteger totalProcessed = new AtomicInteger(0);

    /** Enfileira uma mensagem e libera um permit para o consumidor. */
    public void enqueue(Long messageId, boolean urgent) {
        if (urgent) {
            urgentQueue.addLast(messageId);
        }
        else {
            normalQueue.addLast(messageId);
        }

        totalEnqueued.incrementAndGet();

        log.info("[QUEUE] enqueued id={} urgent={} | sizes: urgent={}, normal={}",
                messageId, urgent, urgentQueue.size(), normalQueue.size());

        // sinaliza que há um item disponível
        available.release();
    }

    /**
     * Implementacao do processamento seguindo FIFO + prioridade + regra 4:1.
     * Bloqueia o thread em available.acquire() até que enqueue() libere.
     */
    public Long dequeue() throws InterruptedException {
        available.acquire();

        Long id;
        synchronized (this) {
            boolean hasUrgent = !urgentQueue.isEmpty();
            boolean hasNormal = !normalQueue.isEmpty();

            if (cycleCount < MAX_URGENTS) {
                if (hasUrgent) {
                    cycleCount++;
                    id = urgentQueue.pollFirst();
                } else if (hasNormal) {
                    cycleCount++;
                    id = normalQueue.pollFirst();
                } else {
                    id = null;
                }
            } else {
                if (hasNormal) {
                    cycleCount = 0;
                    id = normalQueue.pollFirst();
                } else if (hasUrgent) {
                    cycleCount = 1;
                    id = urgentQueue.pollFirst();
                } else {
                    id = null;
                }
            }
        }

        if (id != null) {
            totalProcessed.incrementAndGet();
        }
        return id;
    }


    // Getters para o controller
    public int getUrgentSize() {
        return urgentQueue.size();
    }
    public int getNormalSize() {
        return normalQueue.size();
    }
    public int getTotalEnqueued() {
        return totalEnqueued.get();
    }
    public int getTotalProcessed() {
        return totalProcessed.get();
    }
}
