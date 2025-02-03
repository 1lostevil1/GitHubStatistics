package org.example.Mapper;

import org.example.DTO.BranchDTO;
import org.example.Entities.Github.BranchEntity;
import org.springframework.stereotype.Component;

@Component
public class BranchMapper {

    public BranchDTO branchEntityToDTO(BranchEntity entity){
        return  new BranchDTO(entity.getOwner(), entity.getRepo(), entity.getBranch(), entity.getTimestamp());
    }

    public BranchEntity dtoToBranchEntity(BranchDTO dto){
        return new BranchEntity(dto.owner(), dto.repo(), dto.branchName(), dto.timestamp());
    }
}
