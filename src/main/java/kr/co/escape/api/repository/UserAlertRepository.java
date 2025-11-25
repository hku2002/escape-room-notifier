package kr.co.escape.api.repository;

import kr.co.escape.api.entity.UserAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAlertRepository extends JpaRepository<UserAlert, Long> {
}