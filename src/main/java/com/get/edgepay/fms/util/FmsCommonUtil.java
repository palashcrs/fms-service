package com.get.edgepay.fms.util;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.get.edgepay.fms.common.FMSRuleConstant;
import com.get.edgepay.fms.common.FmsError;
import com.get.edgepay.fms.common.FmsErrorCode;
import com.get.edgepay.fms.common.FmsErrorCodeMap;
import com.get.edgepay.fms.constant.FmsConstant;
import com.get.edgepay.fms.domain.dto.FMSRequest;
import com.get.edgepay.fms.domain.dto.FMSResponse;
import com.get.edgepay.fms.domain.dto.FmsRuleDto;
import com.get.edgepay.fms.domain.dto.FmsRuleEntityDto;
import com.get.edgepay.fms.domain.dto.FmsTransactionDto;
import com.get.edgepay.fms.exception.FmsException;
import com.get.edgepay.fms.exception.InputParamNotFoundException;
import com.get.edgepay.fms.exception.RequestNotFoundException;

public class FmsCommonUtil {

	private static final Logger log = LoggerFactory.getLogger(FmsCommonUtil.class);

	private static FmsCommonUtil fmsCommonUtil = new FmsCommonUtil();

	private static String os = System.getProperty("os.name").toLowerCase();

	private static String algorithm;

	static {
		if (os.indexOf("win") >= 0) {
			algorithm = "SHA1PRNG";
		} else {
			algorithm = "NativePRNG";
		}
	}

	private FmsCommonUtil() {

	}

	public static FmsCommonUtil getInstance() {
		return fmsCommonUtil;
	}

	public final void addError(FMSResponse fmsResponse, FmsErrorCode fmsErrorCode) {
		FmsError fmsError = new FmsError();
		fmsError.setFmsErrorCode(fmsErrorCode.getErrorCode());
		fmsError.setFmsErrorMessage(FmsErrorCodeMap.getMsg(fmsErrorCode.getErrorCode()));
		fmsResponse.setFmsError(fmsError);
	}

	public void validateInput(Object obj, String checkFlag) throws Exception {
		if (obj == null) {
			throw new RequestNotFoundException();
		}

		if (obj instanceof FMSRequest) {
			FMSRequest fmsRequest = (FMSRequest) obj;

			if (FmsConstant.NEW_RULEENTITY_FLAG.equals(checkFlag)) {
				List<FmsRuleEntityDto> fmsRuleEntityDtoList = fmsRequest.getFmsRuleEntities();
				if (fmsRuleEntityDtoList == null) {
					throw new InputParamNotFoundException();
				}
				for (FmsRuleEntityDto fmsRuleEntityDto : fmsRuleEntityDtoList) {
					if (fmsRuleEntityDto != null) {
						if (fmsRuleEntityDto.getRuleEntityName() == null) {
							throw new InputParamNotFoundException();
						}
					}
				}
			}

			if (FmsConstant.NEW_RULE_FLAG.equals(checkFlag)) {
				if (fmsRequest.getUserId() == null) {
					throw new InputParamNotFoundException();
				}
				if (fmsRequest.getMerchantId() == null) {
					throw new InputParamNotFoundException();
				}
				List<FmsRuleDto> fmsRulesDtoList = fmsRequest.getFmsRules();
				if (fmsRulesDtoList == null) {
					throw new InputParamNotFoundException();
				}
				for (FmsRuleDto fmsRuleDto : fmsRulesDtoList) {
					if (fmsRuleDto != null) {
						if (fmsRuleDto.getFmsRuleEntity() == null) {
							throw new InputParamNotFoundException();
						}
						if (fmsRuleDto.getFmsRuleEntity() != null) {
							if (fmsRuleDto.getFmsRuleEntity().getRuleEntityName() == null
									|| fmsRuleDto.getFmsRuleEntity().getRuleEntityName() == null) {
								throw new InputParamNotFoundException();
							}
						}
					}
				}
			}

			if (FmsConstant.ALL_RULE_FLAG.equals(checkFlag)) {
				if (fmsRequest.getUserId() == null) {
					throw new InputParamNotFoundException();
				}
			}

			if (FmsConstant.GLOBAL_RULE_FLAG.equals(checkFlag)) {
				if (fmsRequest.getUserId() == null) {
					throw new InputParamNotFoundException();
				}
			}

			if (FmsConstant.TXN_FRAUD_CALC_FLAG.equals(checkFlag)) {
				if (fmsRequest.getUserId() == null || fmsRequest.getMerchantId() == null
						|| fmsRequest.getFmsTransactions() == null) {
					throw new InputParamNotFoundException();
				}
				if (fmsRequest.getFmsTransactions() != null) {
					if (fmsRequest.getFmsTransactions().get(0) == null) {
						throw new InputParamNotFoundException();
					}
					if (fmsRequest.getFmsTransactions().get(0).getFmsTransactionId() != null) {
						throw new FmsException();
					}
				}
			}

			if (FmsConstant.TXN_SEARCH_FLAG.equals(checkFlag)) {
				if (fmsRequest.getUserId() == null) {
					throw new InputParamNotFoundException();
				}
				if (fmsRequest.getMerchantId() == null) {
					throw new InputParamNotFoundException();
				}
				if (fmsRequest.getFmsTransactions() == null) {
					if (fmsRequest.getFmsTransactions().get(0) == null) {
						throw new InputParamNotFoundException();
					}
				}
			}
		}
	}

	public String generateFMSTxnId(String prefix) throws FmsException {
		StringBuilder sb = new StringBuilder(prefix);
		try {
			String randomNum = String.valueOf(generateNumber(Long.valueOf(1000000), Long.valueOf(9999999)));
			String nanoTime = String.valueOf(System.nanoTime()).substring(4);
			sb.append(randomNum).append(nanoTime);
		} catch (Exception e) {
			throw new FmsException();
		}

		return sb.toString();
	}

	public synchronized Long generateNumber(Long startRange, Long endRange) {
		RandomDataGenerator randGen = new RandomDataGenerator();
		try {
			randGen.setSecureAlgorithm(algorithm, "SUN");
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
		}

		return randGen.nextSecureLong(startRange, endRange);
	}

	public String isEqualAndSetStatus(String ruleValue, String accessMode, String action, String data) {
		String fmsStatus = null;
		if (ruleValue != null) {
			if (ruleValue.equals(data)) {
				fmsStatus = setStatus(accessMode, action);
			}
		}

		return fmsStatus;
	}

	public String isContainsAndSetStatus(String ruleValue, String accessMode, String action, String data) {
		String fmsStatus = null;
		if (action != null) {
			if (action.contains(ruleValue)) {
				fmsStatus = setStatus(accessMode, action);
			}
		}

		return fmsStatus;
	}

	public String isMaxAndSetStatus(BigDecimal maxAmount, String accessMode, String action, BigDecimal data) {
		String fmsStatus = null;
		if (data != null && maxAmount != null) {
			int result = data.compareTo(maxAmount);
			if (result == 1) {
				fmsStatus = setStatus(accessMode, action);
			}
		}

		return fmsStatus;
	}

	public String isOrderLimitAndSetStatus(String ruleValue, int useLimit, String timePeriod, String accessMode,
			String action, List<FmsTransactionDto> filteredTxns, String data) {
		String fmsStatus = null;
		if (ruleValue != null && timePeriod != null && filteredTxns != null && data != null) {
			if (ruleValue.equalsIgnoreCase(data)) {
				Timestamp tsCurrent = FmsUtil.getInstance().currentTimestamp();
				Timestamp tsBefore = FmsUtil.getInstance().getBeforeTimestamp(Integer.parseInt(timePeriod), tsCurrent);
				int counter = 0;
				for (FmsTransactionDto t : filteredTxns) {
					if (t.getExecutionTs() != null) {
						if (t.getExecutionTs().before(tsCurrent) && t.getExecutionTs().after(tsBefore)) {
							counter = counter + 1;
						}
					}
				}
				log.info("counter = " + counter);
				if (counter > useLimit) {
					fmsStatus = setStatus(accessMode, action);
				}
			}
		}

		return fmsStatus;
	}

	private String setStatus(String accessMode, String action) {
		String fmsStatus = null;
		if (accessMode != null && action != null) {
			if (FMSRuleConstant.RULE_ACCESSMODE_PUBLIC.getValue().equalsIgnoreCase(accessMode)) {
				if (FMSRuleConstant.RULE_ACTION_REVIEW.getValue().equalsIgnoreCase(action)) {
					fmsStatus = FMSRuleConstant.RULE_ACTION_REVIEW_STATUS.getValue();
				} else if (FMSRuleConstant.RULE_ACTION_DECLINE.getValue().equalsIgnoreCase(action)) {
					fmsStatus = FMSRuleConstant.RULE_ACTION_DECLINE_STATUS.getValue();
				}
			}
			if (FMSRuleConstant.RULE_ACCESSMODE_PRIVATE.getValue().equalsIgnoreCase(accessMode)) {
				if (FMSRuleConstant.RULE_ACTION_REVIEW.getValue().equalsIgnoreCase(action)) {
					fmsStatus = FMSRuleConstant.RULE_ACTION_REVIEW_STATUS.getValue();
				} else if (FMSRuleConstant.RULE_ACTION_DECLINE.getValue().equalsIgnoreCase(action)) {
					fmsStatus = FMSRuleConstant.RULE_ACTION_DECLINE_STATUS.getValue();
				}
			}
		}

		return fmsStatus;
	}

}
