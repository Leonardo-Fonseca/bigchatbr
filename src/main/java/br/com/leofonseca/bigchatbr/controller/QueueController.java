package br.com.leofonseca.bigchatbr.controller;

import br.com.leofonseca.bigchatbr.queue.MessageQueue;
import br.com.leofonseca.bigchatbr.queue.QueueStatusDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Fila de Mensagens", description = "Consultar status da fila de mensagens.")
public class QueueController {

    private final MessageQueue queue;

    @Operation(summary = "Queue Status", description = "Retorna estatisticas da fila de mensagens.")
    @GetMapping("/queue/status")
    public QueueStatusDTO status() {
        return new QueueStatusDTO(
                queue.getUrgentSize(),
                queue.getNormalSize(),
                queue.getTotalEnqueued(),
                queue.getTotalProcessed()
        );
    }
}
