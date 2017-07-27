package jp.yamato373.web.domain.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.yamato373.web.domain.model.OrderResult;
import jp.yamato373.web.domain.model.Position;
import jp.yamato373.web.domain.model.PositionHistory;
import jp.yamato373.web.domain.repository.PositionHistoryRepository;
import jp.yamato373.web.domain.repository.PositionRepository;
import lombok.AllArgsConstructor;
import lombok.Data;

@Service
public class AutoTradeService {

	@Autowired
	PositionRepository positionRepository;

	@Autowired
	PositionHistoryRepository positionHistoryRepository;

	@Autowired
	OrderResultService orderService;

	/**
	 * 保持しているポジションを全件取得
	 *
	 * @return Set<Position>
	 */
	public List<Position> getAllPosition() {
		List<Position> list = positionRepository.findAll();
		list.sort((a, b) -> a.getTrapPx().compareTo(b.getTrapPx()));
		return list;
	}

	/**
	 * 決済したポジションを全件取得*
	 * @return List<PositionHistory>
	 */
	public List<PositionHistory> getAllPositionHistory() {
		List<PositionHistory> list = positionHistoryRepository.findAll();
		list.sort((a, b) -> a.getId().compareTo(b.getId()));
		return list;
	}

	/**
	 * 利益結果取得
	 *
	 * @return Collection<ProfitReport>
	 */
	public Collection<ProfitReport> getProfitReport() {
		List<ProfitReport> list = new ArrayList<>();
		for (PositionHistory ph : getAllPositionHistory()){
			OrderResult askOr = orderService.getOrderResult(ph.getAskClOrdId());
			OrderResult bidOr = orderService.getOrderResult(ph.getBidClOrdId());
			BigDecimal profit = bidOr.getLastPx().multiply(bidOr.getLastQty()).subtract(askOr.getLastPx().multiply(askOr.getLastQty()));
			list.add(new ProfitReport(
							ph.getId(),
							profit,
							ph.getTrapPx(),
							askOr.getExecTime(),
							askOr.getLastPx(),
							askOr.getLastQty(),
							bidOr.getExecTime(),
							bidOr.getLastPx(),
							bidOr.getLastQty()));
		}
		return list;
	}

	/**
	 * 全体の利益を計算
	 *
	 * @return 利益
	 */
	public BigDecimal getProfitAll() {
		BigDecimal proft = new BigDecimal(0);
		for(ProfitReport pr : getProfitReport()){
			proft = proft.add(pr.利益);
		}
		return proft;
	}

	/**
	 * 日付を指定して利益を計算
	 *
	 * @param from
	 * @param to
	 * @return 利益
	 */
	public BigDecimal getProfitByDate(Date from, Date to) {
		List<PositionHistory> list = positionHistoryRepository.findBySettlTime(from, to);
		BigDecimal proft = new BigDecimal(0);
		for (PositionHistory ph : list){
			OrderResult askOr = orderService.getOrderResult(ph.getAskClOrdId());
			OrderResult bidOr = orderService.getOrderResult(ph.getBidClOrdId());
			proft = proft.add(bidOr.getLastPx().multiply(bidOr.getLastQty()).subtract(askOr.getLastPx().multiply(askOr.getLastQty())));
		}
		return proft;
	}

	@Data
	@AllArgsConstructor
	public static class ProfitReport {
		final private Integer ポジション履歴ID;
		final private BigDecimal 利益;
		final private BigDecimal トラップ価格;
		final private Date 購入日時;
		final private BigDecimal 購入価格;
		final private BigDecimal 購入数量;
		final private Date 売却日時;
		final private BigDecimal 売却価格;
		final private BigDecimal 売却数量;
	}
}
