package ir.bontech.financial.repository;

import ir.bontech.financial.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByAccountsId(Long id);
}
