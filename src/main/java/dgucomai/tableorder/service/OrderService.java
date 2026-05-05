package dgucomai.tableorder.service;

import dgucomai.tableorder.domain.StaffCall;
import dgucomai.tableorder.repository.StaffCallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final StaffCallRepository staffCallRepository;

    public void callStaff(Long tableId) {
        StaffCall staffCall = new StaffCall(tableId);
        staffCallRepository.save(staffCall);
    }
}