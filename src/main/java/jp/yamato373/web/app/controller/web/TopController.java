package jp.yamato373.web.app.controller.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.yamato373.web.domain.model.OrderResult;
import jp.yamato373.web.domain.model.PositionHistory;
import jp.yamato373.web.domain.service.AutoTradeService;
import jp.yamato373.web.domain.service.OrderResultService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class TopController {

	@Autowired
	AutoTradeService autoTradeService;

	@Autowired
	OrderResultService orderResultService;

	@RequestMapping(value="/", method = RequestMethod.GET)
	public String index(Model model){
		log.info("今日の利益Web実行。");

		Date now = new Date();

		// 今日の利益取得
		BigDecimal profitToday = autoTradeService.getProfitByDate(DateUtils.truncate(now, Calendar.DAY_OF_MONTH), now);
		model.addAttribute("profitToday", profitToday);

		// 昨日の利益取得
		BigDecimal profitYesterday =  autoTradeService.getProfitByDate(
				DateUtils.truncate(DateUtils.addDays(now, -1), Calendar.DAY_OF_MONTH),
				DateUtils.truncate(now, Calendar.DAY_OF_MONTH));
		model.addAttribute("profitYesterday", profitYesterday);

		// 今日の利確リスト取得
		List<PositionHistory> positionHistoryList = autoTradeService.getAllPositionHistory();
		model.addAttribute("positionHistoryList", positionHistoryList);

		// ポジション数取得
		long positionCount = autoTradeService.getPositionCount();
		model.addAttribute("positionCount", positionCount);

		// 全ポジション取得 TODO クエリ一発で取得できるようにする
		List<PositionDto> positionDtoList = new ArrayList<>();
		autoTradeService.getAllPosition().forEach(p -> {
			String range = p.getTrapPx().toString() + "以下";
			OrderResult orderResult = orderResultService.getOrderResult(p.getAskClOrdId());
			PositionDto dto = new PositionDto(range, orderResult.getPrice(), orderResult.getLastPx(), orderResult.getExecTime());
			positionDtoList.add(dto);
		});
		model.addAttribute("positionDtoList", positionDtoList);

		return "top";
	}

	@AllArgsConstructor
	@Getter
	public static class PositionDto{
		private final String range;
		private final BigDecimal orderPx;
		private final BigDecimal fillPx;
		private final Date execTime;
	}

}
