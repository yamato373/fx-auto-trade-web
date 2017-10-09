package jp.yamato373.web.app.controller.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
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

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Model model) {
		log.info("今日の利益Web実行。");

		Date now = new Date();

		// 今日の利益取得
		BigDecimal profitToday = autoTradeService.getProfitByDate(DateUtils.truncate(now, Calendar.DAY_OF_MONTH), now);
		model.addAttribute("profitToday", profitToday);

		// 昨日の利益取得
		BigDecimal profitYesterday = autoTradeService.getProfitByDate(
				DateUtils.truncate(DateUtils.addDays(now, -1), Calendar.DAY_OF_MONTH),
				DateUtils.truncate(now, Calendar.DAY_OF_MONTH));
		model.addAttribute("profitYesterday", profitYesterday);

		// 今日の利確リスト取得 TODO クエリ一発で取得できるようにする
		List<PositionHistory> positionHistoryList = autoTradeService
				.getPositionHistoryByDate(DateUtils.truncate(now, Calendar.DAY_OF_MONTH), now);
		List<TradeResultDto> tradeResultDtoList = new ArrayList<>();
		positionHistoryList.stream().sorted(Comparator.comparing(PositionHistory::getSettlTime)).forEach(ph -> {
			BigDecimal buyPx = orderResultService.getOrderResult(ph.getAskClOrdId()).getLastPx();
			BigDecimal sellPx = orderResultService.getOrderResult(ph.getBidClOrdId()).getLastPx();
			BigDecimal profit = sellPx.subtract(buyPx).multiply(BigDecimal.valueOf(100));
			tradeResultDtoList.add(new TradeResultDto(ph.getTrapPx(), buyPx, sellPx, profit, ph.getSettlTime()));
		});
		model.addAttribute("tradeResultDtoList", tradeResultDtoList);

		// 今日の利確リスト取得 TODO クエリ一発で取得できるようにする
		List<PositionHistory> positionHistoryYesterdayList = autoTradeService.getPositionHistoryByDate(
				DateUtils.truncate(DateUtils.addDays(now, -1), Calendar.DAY_OF_MONTH),
				DateUtils.truncate(now, Calendar.DAY_OF_MONTH));
		List<TradeResultDto> tradeResultDtoYesterdayList = new ArrayList<>();
		positionHistoryYesterdayList.stream().sorted(Comparator.comparing(PositionHistory::getSettlTime))
				.forEach(ph -> {
					BigDecimal buyPx = orderResultService.getOrderResult(ph.getAskClOrdId()).getLastPx();
					BigDecimal sellPx = orderResultService.getOrderResult(ph.getBidClOrdId()).getLastPx();
					BigDecimal profit = sellPx.subtract(buyPx).multiply(BigDecimal.valueOf(100));
					tradeResultDtoYesterdayList
							.add(new TradeResultDto(ph.getTrapPx(), buyPx, sellPx, profit, ph.getSettlTime()));
				});
		model.addAttribute("tradeResultDtoYesterdayList", tradeResultDtoYesterdayList);

		// ポジション数取得
		long positionCount = autoTradeService.getPositionCount();
		model.addAttribute("positionCount", positionCount);

		// 全ポジション取得 TODO クエリ一発で取得できるようにする
		List<PositionDto> positionDtoList = new ArrayList<>();
		autoTradeService.getAllPosition().forEach(p -> {
			String range = p.getTrapPx().toString() + "以下";
			OrderResult orderResult = orderResultService.getOrderResult(p.getAskClOrdId());
			positionDtoList.add(
					new PositionDto(range, orderResult.getPrice(), orderResult.getLastPx(), orderResult.getExecTime()));
		});
		model.addAttribute("positionDtoList", positionDtoList);

		return "top";
	}

	@AllArgsConstructor
	@Getter
	public static class TradeResultDto {
		private final BigDecimal range;
		private final BigDecimal buyPx;
		private final BigDecimal sellPx;
		private final BigDecimal profit;
		private final Date execTime;
	}

	@AllArgsConstructor
	@Getter
	public static class PositionDto {
		private final String range;
		private final BigDecimal orderPx;
		private final BigDecimal fillPx;
		private final Date execTime;
	}

}
