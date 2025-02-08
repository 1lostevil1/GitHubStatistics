package org.example.Mapper;

import org.example.DTO.BranchDTO;
import org.example.Entities.BranchEntity;
import org.springframework.stereotype.Component;

@Component
public class BranchMapper {

    public BranchDTO branchEntityToDTO(BranchEntity entity){
        return  new BranchDTO(entity.getUrl(), entity.getCheckAt());
    }
}
