package org.unibl.etf.ip2024.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.unibl.etf.ip2024.models.dto.AttributeDTO;
import org.unibl.etf.ip2024.repositories.AttributeEntityRepository;
import org.unibl.etf.ip2024.services.AttributeService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttributeServiceImpl implements AttributeService {

    private final AttributeEntityRepository attributeRepository;
    private final ModelMapper modelMapper;

    public AttributeServiceImpl(AttributeEntityRepository attributeRepository, ModelMapper modelMapper) {
        this.attributeRepository = attributeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<AttributeDTO> getAttributesByCategoryId(Integer categoryId) {
        return this.attributeRepository.findByCategoryId(categoryId).stream()
                .map(attribute -> modelMapper.map(attribute, AttributeDTO.class))
                .collect(Collectors.toList());
    }
}
