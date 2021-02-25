package ir.bontech.financial.domain.service;

import ir.bontech.financial.controller.api.AccountSaveRequest;
import ir.bontech.financial.controller.api.RegisterRequest;
import ir.bontech.financial.domain.entity.AccountEntity;
import ir.bontech.financial.domain.entity.UserEntity;
import ir.bontech.financial.domain.service.api.AccountSaveResult;
import ir.bontech.financial.domain.service.api.UserSaveResult;
import ir.bontech.financial.exception.NotFoundException;
import ir.bontech.financial.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserSaveResult addUser(RegisterRequest request) {
        UserEntity user = UserEntity.convert(request);
        final UserEntity result = userRepository.save(user);
        log.debug("add {} in db", result);
        return new UserSaveResult(result.getId());
    }

    @Transactional
    public AccountSaveResult addAccounts(Long userId, AccountSaveRequest request) {
        final UserEntity user = loadUser(userId);
        log.debug("find User[id={}]", user.getId());
        final Set<AccountEntity> accounts = createAccounts(request.getAccountInformation());
        user.setAccounts(accounts);
        final UserEntity result = userRepository.save(user);
        log.debug("add accounts in db {}", result.getAccounts());
        return getAccountResult(result);
    }

    @Transactional(readOnly = true)
    public UserEntity loadUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("can not find user by id=" + id));
    }

    private Set<AccountEntity> createAccounts(List<AccountSaveRequest.AccountInformation> accountInformation) {
        return accountInformation.stream()
                .map(information -> AccountEntity.builder().accountNumber(information.getAccountNumber()).currentBalance(information.getCurrentBalance()).build())
                .collect(Collectors.toSet());
    }

    private AccountSaveResult getAccountResult(UserEntity user) {
        final List<String> accounts = user.getAccounts().stream()
                .map(AccountEntity::getAccountNumber)
                .collect(Collectors.toList());
        return AccountSaveResult.builder()
                .userId(user.getId())
                .accountNumbers(accounts)
                .build();
    }
}
