package org.example.Parser;

import org.example.DTO.ParsedBranchDTO;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UrlParser {

    private final Pattern pattern;

    public UrlParser() {
        pattern = Pattern.compile("^https://github\\.com/[a-zA-Z0-9_-]+/[a-zA-Z0-9_-]+/tree/[a-zA-Z0-9_-]+$");
    }

    //https://github.com/1lostevil1/javaCourse_TelegramBot/tree/hw5 --> ParsedBranchDTO(owner:1lostevil1, repo:javaCourse_TelegramBot, branchName:hw5)
    public ParsedBranchDTO parse(String url) {
        if(pattern.matcher(url).matches()) {
            String[] split = url.split("/");
            return new ParsedBranchDTO(split[1],split[2],split[3]);
        }
        else throw new RuntimeException("Invalid URL");
    }
}
