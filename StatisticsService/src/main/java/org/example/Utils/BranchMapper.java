package org.example.Utils;

import org.example.DTO.BranchDTO;
import org.example.DTO.DatedListCommitRequest;
import org.example.Entities.Github.BranchEntity;
import org.example.Response.Github.Commit.CommitResponse;
import org.example.Request.Github.UpdateRequest;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
public class BranchMapper {

    public BranchDTO branchEntityToDTO(BranchEntity entity){
        return  new BranchDTO(entity.getOwner(), entity.getRepo(), entity.getName(), entity.getTimestamp());
    }

    public DatedListCommitRequest branchDTOtoDatedListCommitRequest(BranchDTO branchDTO){
        return  new DatedListCommitRequest(branchDTO.owner(), branchDTO.repo(), branchDTO.branchName(), branchDTO.timestamp(), OffsetDateTime.now());
    }

    public UpdateRequest commitListToUpdateRequest(BranchDTO branchDTO, List<CommitResponse> commitResponses){
        return new UpdateRequest
                (
                branchDTO.owner(),branchDTO.repo(),branchDTO.branchName(),
                commitResponses.stream().flatMap(commit->commit.files().stream()).toList()
                );
    }
}
