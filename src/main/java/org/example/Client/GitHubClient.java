package org.example.Client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


public class GitHubClient {

    private final WebClient webClient;

    public GitHubClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<String> getInfo(){
        LocalDate currentDate = LocalDate.now();
        LocalDate lastMonthDate = currentDate.minusYears(1);

        // Форматируем даты в ISO 8601 формат
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String sinceDate = lastMonthDate.atStartOfDay().format(formatter) + "Z"; // Начало месяца
        String untilDate = currentDate.atTime(23, 59, 59).format(formatter) + "Z"; // Конец месяца

        // Выполняем запрос с параметрами since и until
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/repos/MrsSbr/amm-java-9-91-2024/commits")
                        .build())
                .retrieve()
                .bodyToMono(List.class) // Получаем тело ответа как список
                .block()  // Блокируем для синхронного выполнения (можно использовать в асинхронном контексте)
                .stream()  // Преобразуем список в стрим
                .map(commit -> {
                    // Извлекаем SHA из каждого коммита
                    Map<String, Object> commitMap = (Map<String, Object>) commit;
                    return (String) commitMap.get("sha");
                })
                .toList(); // Преобразуем в List<String>
    }
}
