package jp.yamato373.web.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.yamato373.web.domain.model.OrderResult;


@Repository
public interface OrderResultRepository extends JpaRepository<OrderResult, Integer> {
}