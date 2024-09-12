package org.unibl.etf.ip2024.services;

import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.models.dto.RssItemDTO;

import java.util.List;

@Service
public interface RssNewsService {
    List<RssItemDTO> getDailyNews();
}
