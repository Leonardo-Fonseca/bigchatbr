package br.com.leofonseca.bigchatbr.service.queue;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageQueueTest {

    //@Test
    /*void shouldProcessInFourToOnePatternWhenPreFilled() throws InterruptedException {
        // 1) Instancia a fila
        MessageQueue queue = new MessageQueue();

        // 2) Pré‑enche 10 urgentes (IDs 1..10) e 2 normais (100, 101)
        for (long i = 1; i <= 10; i++) {
            queue.enqueue(i, true);
        }
        queue.enqueue(100L, false);
        queue.enqueue(101L, false);

        // 3) Chama dequeue() 12 vezes e coleta os IDs retornados
        List<Long> actualOrder = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            actualOrder.add(queue.dequeue());
        }

        // 4) Define a ordem esperada: 4 urgentes, 1 normal, 4 urgentes, 1 normal, 2 urgentes
        List<Long> expectedOrder = List.of(
                1L, 2L, 3L, 4L,    // 4 urgentes
                100L,              // 1 normal
                5L, 6L, 7L, 8L,    // 4 urgentes
                101L,              // 1 normal
                9L, 10L            // últimas 2 urgentes
        );

        // 5) Verifica
        assertEquals(expectedOrder, actualOrder,
                "A fila deve entregar no padrão [U,U,U,U,N, U,U,U,U,N, U,U]");
    }*/
}
