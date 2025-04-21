package br.com.leofonseca.bigchatbr.service.queue;

import br.com.leofonseca.bigchatbr.domain.message.MessageResponseDTO;
import br.com.leofonseca.bigchatbr.domain.message.MessageStatus;
import br.com.leofonseca.bigchatbr.service.MessageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageProcessor implements Runnable {
    private final MessageQueue queue;
    private final MessageService messageService;

    /** Inicia a thread logo após a criação do bean Spring */
    @PostConstruct
    public void start() {
        Thread t = new Thread(this, "message-processor-thread");
        t.setDaemon(true);
        t.start();
        log.info("[PROCESSOR] thread started");
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Long msgId = queue.dequeue(); // fica bloqueado até chegar alguma mensagem
                process(msgId);
            }catch (InterruptedException ie) {
                // sinal de shutdown legítimo: registra e sai do loop
                log.warn("[PROCESS] interrupted, shutting down thread");
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                // qualquer outra falha: registra mas mantém a thread viva
                log.error("[PROCESS] error inesperado no loop de processamento", e);
            }
        }
    }

    private void process(Long id) throws Exception {
        try {
            Thread.sleep(1000); // Simula o tempo de processamento
            messageService.updateStatus(id, MessageStatus.PROCESSING);
            messageService.sendMessage(id); // Logica de envio simulada no messageService
            messageService.updateStatus(id, MessageStatus.SENT);

            log.info("[PROCESS] FINISH id={} at {}", id, LocalDateTime.now());

        } catch (Exception ex) {

            messageService.updateStatus(id, MessageStatus.FAILED);
            log.error("[PROCESS] FAILED id={} at {} → {}", id, LocalDateTime.now(), ex.getMessage());

        }
    }
}
