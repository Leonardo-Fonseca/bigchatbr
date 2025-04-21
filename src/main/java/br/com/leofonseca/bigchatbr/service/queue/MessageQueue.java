package br.com.leofonseca.bigchatbr.service.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Semaphore;

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

    /** Enfileira uma mensagem e libera um permit para o consumidor. */
    public void enqueue(Long messageId, boolean urgent) {
        if (urgent) {
            urgentQueue.addLast(messageId);
        }
        else {
            normalQueue.addLast(messageId);
        }

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
        available.acquire();  // espera algo na fila
        synchronized (this) {
            boolean hasU = !urgentQueue.isEmpty();
            boolean hasN = !normalQueue.isEmpty();

            // slots 0,1,2,3 → urgentes
            if (cycleCount < MAX_URGENTS) {
                if (hasU) {
                    cycleCount++;
                    return urgentQueue.pollFirst();
                }
                if (hasN) {
                    // se não houver urgente, mas tiver normal, serve normal
                    cycleCount++;
                    return normalQueue.pollFirst();
                }
            }
            // slot 4 → normal
            if (cycleCount == MAX_URGENTS) {
                if (hasN) {
                    cycleCount = 0;              // reinicia o ciclo
                    return normalQueue.pollFirst();
                }
                if (hasU) {
                    // fallback em urgente
                    cycleCount = 1;              // já processou 1 urgente do próximo ciclo
                    return urgentQueue.pollFirst();
                }
            }

            // nenhuma mensagem? ( não deveria acontecer, mas pra segurança )
            return null;
        }
    }
}
