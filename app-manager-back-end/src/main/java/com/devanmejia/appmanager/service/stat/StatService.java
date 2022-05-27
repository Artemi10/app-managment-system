package com.devanmejia.appmanager.service.stat;


import com.devanmejia.appmanager.transfer.stat.StatRequestDTO;
import com.devanmejia.appmanager.transfer.stat.StatResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StatService {
    List<StatResponseDTO> createStats(long appId, StatRequestDTO statistics);
    List<StatResponseDTO> createStats(long appId, long userId);
}
