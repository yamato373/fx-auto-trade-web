package jp.yamato373.web.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.yamato373.web.domain.model.OrderResult;
import jp.yamato373.web.domain.repository.OrderResultRepository;

@Service
public class OrderResultService {

	@Autowired
	OrderResultRepository orderResultRepository;

	/**
	 * 注文履歴取得
	 *
	 * @param ClOrdId
	 * @return
	 */
	public OrderResult getOrderResult(Integer clOrdId) {
		return orderResultRepository.findOne(clOrdId);
	}

	/**
	 * 全注文履歴取得
	 *
	 * @return
	 */
	public List<OrderResult> getOrderResultAll() {
		return orderResultRepository.findAll();
	}
}
