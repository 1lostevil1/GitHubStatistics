package org.example.Parser;

import lombok.extern.slf4j.Slf4j;
import org.example.DTO.ParsedBranchDTO;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Pattern;

@Component
@Slf4j
public class UrlParser {

    private final Pattern pattern;

    public UrlParser() {
        pattern = Pattern.compile("^https://github\\.com/[a-zA-Z0-9_-]+/[a-zA-Z0-9_-]+/tree/[a-zA-Z0-9_-]+$");
    }

    //https://github.com/1lostevil1/javaCourse_TelegramBot/tree/hw5 --> ParsedBranchDTO(owner:1lostevil1, repo:javaCourse_TelegramBot, branchName:hw5)
    public ParsedBranchDTO parse(String url) {
        if(pattern.matcher(url).matches()) {
            String[] split = url.split("/+");
            log.info(Arrays.toString(split));
            return new ParsedBranchDTO(split[2].trim(),split[3].trim(),split[5].trim());
        }
        else throw new RuntimeException("Invalid URL");
    }
}
