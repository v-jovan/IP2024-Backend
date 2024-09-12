package org.unibl.etf.ip2024.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.unibl.etf.ip2024.models.dto.RssItemDTO;
import org.unibl.etf.ip2024.services.RssNewsService;

import java.util.List;

@RestController
@RequestMapping("/news")
public class RssController {
    private final RssNewsService rssNewsService;

    public RssController(RssNewsService rssNewsService) {
        this.rssNewsService = rssNewsService;
    }

    @GetMapping()
    public ResponseEntity<List<RssItemDTO>> getDailyNews() {
        List<RssItemDTO> news = rssNewsService.getDailyNews();
        return new ResponseEntity<>(news, HttpStatus.OK);
    }
}
