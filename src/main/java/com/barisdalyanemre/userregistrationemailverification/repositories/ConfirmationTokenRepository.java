package com.barisdalyanemre.userregistrationemailverification.repositories;

import com.barisdalyanemre.userregistrationemailverification.models.ConfirmationToken;
import com.barisdalyanemre.userregistrationemailverification.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    Optional<ConfirmationToken> findByToken(String token);

    List<ConfirmationToken> findAllByUser(User user);
}
