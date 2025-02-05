package org.example.Controller;

import lombok.extern.slf4j.Slf4j;
import org.example.DTO.BranchDTO;
import org.example.Repository.BranchRepo;
import org.example.Service.BranchUpdateService;
import org.example.Utils.BranchMapper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;


@EnableScheduling
@Service
@Slf4j
public class ScheduledUpdater {


    private final BranchUpdateService branchUpdateService;

    private final BranchRepo branchRepo;

    private final BranchMapper branchMapper;

;

    public ScheduledUpdater(BranchRepo branchRepo, BranchUpdateService branchUpdateService) {


        this.branchRepo = branchRepo;
        this.branchUpdateService = branchUpdateService;
        this.branchMapper = new BranchMapper();


    }

    @Scheduled(fixedDelayString = "#{scheduler.interval}")
    public void task() {

    log.info("Sheduling task");
        OffsetDateTime time = OffsetDateTime.now().minusMinutes(5);

        List<BranchDTO> links = branchRepo.findAllByTimestampBefore(time).stream().map(branchMapper::branchEntityToDTO).toList();

        branchUpdateService.checkUpdates(links);

    }
}
