package org.example.Service;

import org.example.Client.GitHubClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@EnableScheduling
@Service
public class ScheduledUpdater {

    @Autowired
    private GitHubClient gitHubClient;


    @Scheduled(fixedDelayString = "#{scheduler.interval}")
    public void task(){
        //берем из бд дату последней проверки
        //делаем запрос since=последняя дата + что=то until = сейчас
        //обновляем инфу в списке изменений файлов и в аутбоксе
        // фронтент там как-то это читает
    }
}
