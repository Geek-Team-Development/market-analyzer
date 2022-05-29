package ru.manalyzer.config;

import io.mongock.runner.spring.base.events.SpringMigrationSuccessEvent;
import org.springframework.amqp.core.*;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import ru.manalyzer.persist.User;
import ru.manalyzer.repository.UserRepository;

@Component
public class MongockSuccessEventListener implements ApplicationListener<SpringMigrationSuccessEvent> {

    private final DirectExchange frontNotifyExchange;
    private final AmqpAdmin amqpAdmin;
    private final UserRepository userRepository;

    public MongockSuccessEventListener(DirectExchange frontNotifyExchange,
                                       AmqpAdmin amqpAdmin, UserRepository userRepository) {
        this.frontNotifyExchange = frontNotifyExchange;
        this.amqpAdmin = amqpAdmin;
        this.userRepository = userRepository;
    }

    @Override
    public void onApplicationEvent(SpringMigrationSuccessEvent event) {
        User admin = userRepository.findByEmail("admin@mail.ru").get();
        createProductUpdateQueueForUser(admin.getId());
    }

    private void createProductUpdateQueueForUser(String userId) {
        Queue frontNotifyQueue = new Queue("front.notify.queue." + userId, false);
        Binding binding = BindingBuilder
                .bind(frontNotifyQueue)
                .to(frontNotifyExchange)
                .with(userId);
        amqpAdmin.declareQueue(frontNotifyQueue);
        amqpAdmin.declareBinding(binding);
    }
}
