package com.xixi.billCheck;

import java.util.List;
import java.util.Map;

import com.xixi.billCheck.entities.BillingItems;

/**
 * 记账疏漏查找内容
 *
 */
public class App 
{
	public static void main(String[] args) {
		try {
			Map<Integer, List<BillingItems>> accountMap1 = ExcelUtils.readExcelByPoiV2003AccountBook("F:\\BrowserDownload\\wacai_2019-08-2019-09.xls");
			Map<Integer, List<BillingItems>> billingMap1 = ExcelUtils.readExcelByPoiV2003Billing("F:\\BrowserDownload\\刘明先生201909交易明细报表.xls");
			ExcelUtils.compare("201909", billingMap1, accountMap1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
