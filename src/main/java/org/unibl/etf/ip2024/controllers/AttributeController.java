package org.unibl.etf.ip2024.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.unibl.etf.ip2024.models.dto.AttributeDTO;
import org.unibl.etf.ip2024.services.AttributeService;

import java.util.List;

@RestController
@RequestMapping("/attributes")
@RequiredArgsConstructor
public class AttributeController {

    private static final Logger logger = LoggerFactory.getLogger(AttributeController.class);

    private final AttributeService attributeService;

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<AttributeDTO>> getAttributesByCategoryId(@PathVariable Integer categoryId) {
        logger.info("Received request to get attributes for category ID: {}", categoryId);
        try {
            List<AttributeDTO> attributes = attributeService.getAttributesByCategoryId(categoryId);
            logger.info("Found {} attributes for category ID: {}", attributes.size(), categoryId);
            return ResponseEntity.ok(attributes);
        } catch (Exception ex) {
            logger.error("An unexpected error occurred", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
