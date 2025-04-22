package br.com.leofonseca.bigchatbr.service.queue;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageQueueTest {

    @Test
    void shouldProcessInFourToOnePatternWhenPreFilled() throws InterruptedException {
        MessageQueue queue = new MessageQueue();

        for (long i = 1; i <= 10; i++) {
            queue.enqueue(i, true);
        }
        queue.enqueue(100L, false);
        queue.enqueue(101L, false);

        List<Long> actualOrder = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            actualOrder.add(queue.dequeue());
        }

        List<Long> expectedOrder = List.of(
                1L, 2L, 3L, 4L,    // 4 urgentes
                100L,              // 1 normal
                5L, 6L, 7L, 8L,    // 4 urgentes
                101L,              // 1 normal
                9L, 10L            // últimas 2 urgentes
        );

        assertEquals(expectedOrder, actualOrder,
                "A fila deve entregar no padrão [U,U,U,U,N, U,U,U,U,N, U,U]");
    }
}
