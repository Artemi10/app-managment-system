package com.devanmejia.appmanager.service.user;

import com.devanmejia.appmanager.transfer.auth.UpdateDTO;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    String resetUser(String email);

    void confirmResetUser(String email, String resetToken);

    void activateUser(String email);

    void updateUser(String email, UpdateDTO updateDTO);
}
