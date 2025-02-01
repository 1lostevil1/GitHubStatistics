package org.example.Utils;

import org.example.DTO.BranchDTO;
import org.example.DTO.DatedListCommitRequest;
import org.example.Entities.Github.BranchEntity;
import org.example.Response.Github.Commit.CommitResponse;
import org.example.Response.Github.Commit.UpdateResponse;

import java.time.OffsetDateTime;
import java.util.List;

public class Mapper {

    public BranchDTO branchEntityToDTO(BranchEntity entity){
        return  new BranchDTO(entity.getOwner(), entity.getRepo(), entity.getBranch_name(), entity.getCheck_at());
    }

    public DatedListCommitRequest branchDTOtoDatedListCommitRequest(BranchDTO branchDTO){
        return  new DatedListCommitRequest(branchDTO.owner(), branchDTO.repo(), branchDTO.branchName(), branchDTO.checkAt(), OffsetDateTime.now());
    }

    public UpdateResponse commitListToUpdateResponse(BranchDTO branchDTO, List<CommitResponse> commitResponses){
        return new UpdateResponse
                (
                branchDTO.owner(),branchDTO.repo(),branchDTO.branchName(),
                commitResponses.stream().flatMap(commit->commit.files().stream()).toList()
                );
    }
}
