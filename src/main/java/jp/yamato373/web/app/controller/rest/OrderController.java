package jp.yamato373.web.app.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jp.yamato373.web.domain.model.OrderResult;
import jp.yamato373.web.domain.service.OrderResultService;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class OrderController {

	@Autowired
	OrderResultService orderService;

	@RequestMapping(value = "/order/history", method = RequestMethod.GET)
	public List<OrderResult> getOrderHistory() {
		log.info("注文履歴取得API実行。");
		List<OrderResult> list = orderService.getOrderResultAll();
		list.sort((a, b) -> a.getClOrdId().compareTo(b.getClOrdId()));
		return list;
	}
}
