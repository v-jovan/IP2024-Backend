package org.unibl.etf.ip2024.services;

import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.models.dto.AttributeDTO;

import java.util.List;

@Service
public interface AttributeService {
    List<AttributeDTO> getAttributesByCategoryId(Integer categoryId);
    List<AttributeDTO> getAllAttributesWithValues();
}
