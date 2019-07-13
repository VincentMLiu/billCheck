package com.xixi.billCheck;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.metadata.Sheet;
import com.xixi.billCheck.entities.BillingItems;

public class ExcelUtils {

	public static SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
	public static SimpleDateFormat yyyyMM = new SimpleDateFormat("yyyyMM");
	public static SimpleDateFormat yyyy_MM_ddHHmmss = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	/**
	 * 03版本excel读数据量少于1千行数据，内部采用回调方法.
	 *
	 * @throws IOException
	 *             简单抛出异常，真实环境需要catch异常,同时在finally中关闭流
	 */
	public static void simpleReadListStringV2003() throws IOException {
		File f = new File("F:\\BrowserDownload\\刘明先生201906交易明细报表.xls");

		// InputStream inputStream =
		// FileUtil.getResourcesFileInputStream("2003.xls");

		InputStream inputStream = new BufferedInputStream(new FileInputStream(f));
		List<Object> data = EasyExcelFactory.read(inputStream, new Sheet(1, 0));
		inputStream.close();
		print(data);
	}

	public static void print(List<Object> datas) {
		int i = 0;
		for (Object ob : datas) {
			System.out.println(i++);
			System.out.println(ob);
			System.out.println(ob.toString());
		}
	}

	public static Map<Integer, List<BillingItems>> readExcelByPoiV2003Billing(String filePath) {

		File f = new File(filePath);

		InputStream inputStream;

		Map<Integer, List<BillingItems>> billingMap = new HashMap<Integer, List<BillingItems>>();
		List<BillingItems> bItemsList;
		try {
			inputStream = new FileInputStream(f);
			HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
			HSSFSheet sheetAt = workbook.getSheetAt(0);

			// 4、循环读取表格数据
			for (Row row : sheetAt) {
				// 首行（即表头）不读取
				if (row.getRowNum() == 0) {
					continue;
				}
				// 读取当前行中单元格数据，索引从0开始
				Date spendDate = yyyyMMdd.parse(row.getCell(0).getStringCellValue());
				// System.out.print(row.getCell(0).getStringCellValue()+ "\t");
				double amount = Double.parseDouble(row.getCell(6).getStringCellValue());
				// System.out.print(amount+ "\t");
				String rowContent = "[	";
				Iterator<Cell> it = row.cellIterator();
				while (it.hasNext()) {
					Cell cell = it.next();
					rowContent += cell.getStringCellValue() + "\t";
				}
				rowContent += "\t]";
				// System.out.println(rowContent);
				Calendar cal = Calendar.getInstance();
				cal.setTime(spendDate);
				BillingItems bitem = new BillingItems();
				bitem.setSpendDate(spendDate);
				bitem.setAmount(amount);
				bitem.setRowContent(rowContent);
				bitem.setBillingType(0);
				
				bItemsList = billingMap.get(cal.get(Calendar.DAY_OF_YEAR));

				if (bItemsList == null) {
					bItemsList = new ArrayList<BillingItems>();
					bitem.setUuid(cal.get(Calendar.DAY_OF_YEAR) + "1");
					bItemsList.add(bitem);
					billingMap.put(cal.get(Calendar.DAY_OF_YEAR), bItemsList);
				} else {
					int uuidPlus = bItemsList.size() + 1;
					bitem.setUuid(cal.get(Calendar.DAY_OF_YEAR) + "" + uuidPlus);
					bItemsList.add(bitem);
					billingMap.put(cal.get(Calendar.DAY_OF_YEAR), bItemsList);
				}
			}
			// 5、关闭流
			workbook.close();


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return billingMap;
	}

	
	private static void dealAccountBook(Map<Integer, List<BillingItems>> accountBookMap, HSSFSheet sheetAt ){
		// 4、循环读取表格数据
		List<BillingItems> bItemsList;
		
		for (Row row : sheetAt) {
			// 首行（即表头）不读取
			if (row.getRowNum() == 0) {
				continue;
			}
			String account = row.getCell(2).getStringCellValue();
			if(account.equals("浦发AE白") || account.equals("浦发日航卡")){
				// 读取当前行中单元格数据，索引从0开始
				Date spendDate = row.getCell(7).getDateCellValue();
				// System.out.print(row.getCell(7).getDateCellValue()+ "\t");
				double amount = row.getCell(8).getNumericCellValue();
				// System.out.print(amount+ "\t");
				String rowContent = "[	";
				Iterator<Cell> it = row.cellIterator();
				while (it.hasNext()) {
					Cell cell = it.next();
					rowContent += cell.toString() + "\t";
				}
				rowContent += "\t]";
				// System.out.println(rowContent);
				Calendar cal = Calendar.getInstance();
				cal.setTime(spendDate);
				BillingItems bitem = new BillingItems();
				bitem.setSpendDate(spendDate);
				bitem.setAmount(amount);
				bitem.setRowContent(rowContent);
				bitem.setBillingType(1);
				
				
				bItemsList = accountBookMap.get(cal.get(Calendar.DAY_OF_YEAR));
				
				if (bItemsList == null) {
					bItemsList = new ArrayList<BillingItems>();
					bitem.setUuid(cal.get(Calendar.DAY_OF_YEAR) + "1");
					bItemsList.add(bitem);
					accountBookMap.put(cal.get(Calendar.DAY_OF_YEAR), bItemsList);
				} else {
					int uuidPlus = bItemsList.size() + 1;
					bitem.setUuid(cal.get(Calendar.DAY_OF_YEAR) + "" + uuidPlus);
					bItemsList.add(bitem);
					accountBookMap.put(cal.get(Calendar.DAY_OF_YEAR), bItemsList);
				}
				
			}
		}
	}
	
	public static Map<Integer, List<BillingItems>> readExcelByPoiV2003AccountBook(String filePath) {

		File f = new File(filePath);

		InputStream inputStream;

		Map<Integer, List<BillingItems>> accountBookMap = new HashMap<Integer, List<BillingItems>>();
		
		try {
			inputStream = new FileInputStream(f);
			HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
			HSSFSheet sheetAt = workbook.getSheetAt(0);
			dealAccountBook(accountBookMap, sheetAt);
//			HSSFSheet sheetAt1 = workbook.getSheetAt(1);
//			dealAccountBook(accountBookMap, sheetAt1);
			// 4、循环读取表格数据
			// 5、关闭流
			workbook.close();


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return accountBookMap;

	}

	public static void compare(String billingMonth, Map<Integer, List<BillingItems>> billingMap1, Map<Integer, List<BillingItems>> accountMap1){
		try {
			Calendar startCal = Calendar.getInstance();
			String startDateStr = billingMonth + "04";
			Date startDate = yyyyMMdd.parse(startDateStr);
			startCal.setTime(startDate);
			startCal.add(Calendar.MONTH, -1);
			
			Calendar endCal = Calendar.getInstance();
			String endDateStr = billingMonth + "03";
			Date endDate = yyyyMMdd.parse(endDateStr);
			endCal.setTime(endDate);
			
			
			while(startCal.compareTo(endCal) <= 0){
				
				//对比一天之内的消费笔数
				List<BillingItems> billingList = billingMap1.get(startCal.get(Calendar.DAY_OF_YEAR))==null? new ArrayList<BillingItems>() : billingMap1.get(startCal.get(Calendar.DAY_OF_YEAR));
				List<BillingItems> accountList = accountMap1.get(startCal.get(Calendar.DAY_OF_YEAR))==null? new ArrayList<BillingItems>() : accountMap1.get(startCal.get(Calendar.DAY_OF_YEAR));
				String date = yyyyMMdd.format(startCal.getTime());
				
				compareBill(date, billingList, accountList);
				startCal.add(Calendar.DATE, 1);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

	
	public static void compareBill(String date, List<BillingItems> billingList, List<BillingItems> accountList){
//		int compare = billingList.size() - accountList.size();
//		List<BillingItems> billingListRemove = new ArrayList<BillingItems>();
//		List<BillingItems> accountListRemove = new ArrayList<BillingItems>();
		
        long st = System.nanoTime();
        List<BillingItems> diff = new ArrayList<BillingItems>();
        List<BillingItems> maxList = billingList;
        List<BillingItems> minList = accountList;
        
		if(accountList.size() > billingList.size()){
            maxList = accountList;
            minList = billingList;
        }
        Map<BillingItems,Integer> map = new HashMap<BillingItems,Integer>(maxList.size());
        for (BillingItems billingItems : maxList) {
            if(map.get(billingItems)!=null)
            {
            	//如果当天有金额相同的笔数，哈希值+1，作为另一个key输入
            	billingItems.incrementHash();
                map.put(billingItems, 1);
                continue;
            }
            map.put(billingItems, 1);
            
        }
        for (BillingItems billingItems : minList) {
//        	billingItems.equals(obj)
            if(map.get(billingItems)!=null)
            {
                if(map.get(billingItems)==2){
                	billingItems.incrementHash();
                	if(map.get(billingItems)!=null){
                    	map.put(billingItems, 2);
                        continue;
                	}
                }else {
                	map.put(billingItems, 2);
                	continue;
                }
            }
            diff.add(billingItems);
        }
        for(Map.Entry<BillingItems, Integer> entry : map.entrySet())
        {
            if(entry.getValue()==1)
            {
                diff.add(entry.getKey());
            }
        }
        
        
        for(BillingItems billingItems : diff){
        	if(billingItems.getBillingType()==0){
        		System.out.println("#################少计喽####################");
        		System.out.println(billingItems.getRowContent());
        	}else if(billingItems.getBillingType()==1){
        		System.out.println("#################多计喽####################");
        		System.out.println(billingItems.getRowContent());
        	}
        }
        
        
		
//		if(compare == 0){
//			//消费笔数相同
//			maxList = billingList;
//			minList = accountList;
//			
//			for(Iterator<BillingItems> iterator1 = billingList.iterator(); iterator1.hasNext();){
//				BillingItems bitems = iterator1.next();
//				for(Iterator<BillingItems> iterator2 = accountList.iterator(); iterator2.hasNext();){
//					BillingItems aitems = iterator2.next();
//					if(aitems.getAmount() - bitems.getAmount() == 0){
//						//如果有消费金额一样的，就删掉
//						billingListRemove.add(bitems);
//						//即使有两笔消费金额一样的，如果记错了，也会在账单中或账本中显示
//						accountListRemove.add(aitems);
//					}
//				}
//			}
//			
//			accountList.removeAll(accountListRemove);
//			billingList.removeAll(billingListRemove);
//			if(billingList.size()>0){
//				System.out.println("**************************日期：" + date + "************************".hashCode());
//				System.out.println("###############有可能少记喽#############");
//				for(BillingItems bitems: billingList){
//					System.out.println(bitems.getRowContent());
//				}
//			}
//			if(accountList.size()>0){
//				System.out.println("**************************日期：" + date + "************************");
//				System.out.println("###############有可能多记喽#############");
//				for(BillingItems aitems: accountList){
//					System.out.println(aitems.getRowContent());
//				}
//			}
//			
//		} else if(compare > 0){
//			//账单笔数 大于 账本笔数，少记了
//			
//			for(Iterator<BillingItems> iterator1 = billingList.iterator(); iterator1.hasNext();){
//				BillingItems bitems = iterator1.next();
//				for(Iterator<BillingItems> iterator2 = accountList.iterator(); iterator2.hasNext();){
//					BillingItems aitems = iterator2.next();
//					if(aitems.getAmount() - bitems.getAmount() == 0){
//						//如果有消费金额一样的，就删掉
//						billingListRemove.add(bitems);
//						//即使有两笔消费金额一样的，如果记错了，也会在账单中或账本中显示
//						accountListRemove.add(aitems);
//					}
//				}
//			}
//			accountList.removeAll(accountListRemove);
//			billingList.removeAll(billingListRemove);
//			
//			if(billingList.size()>0){
//				System.out.println("**************************日期：" + date + "************************");
//				System.out.println("###############有可能少记喽#############");
//				for(BillingItems bitems: billingList){
//					System.out.println(bitems.getRowContent());
//				}
//			}
//			if(accountList.size()>0){
//				System.out.println("**************************日期：" + date + "************************");
//				System.out.println("###############有可能多记喽#############");
//				for(BillingItems aitems: accountList){
//					System.out.println(aitems.getRowContent());
//				}
//			}
//			
//		} else if(compare < 0){
//			//账单笔数 小于 账本笔数，多记了
//			for(Iterator<BillingItems> iterator1 = accountList.iterator(); iterator1.hasNext();){
//				BillingItems aitems = iterator1.next();
//				for(Iterator<BillingItems> iterator2 = billingList.iterator(); iterator2.hasNext();){
//					BillingItems bitems = iterator2.next();
//					if(aitems.getAmount() - bitems.getAmount() == 0){
//						//如果有消费金额一样的，就删掉
//						billingListRemove.add(bitems);
//						//即使有两笔消费金额一样的，如果记错了，也会在账单中或账本中显示
//						accountListRemove.add(aitems);
//					}
//				}
//			}
//			
//			accountList.removeAll(accountListRemove);
//			billingList.removeAll(billingListRemove);
//			if(billingList.size()>0){
//				System.out.println("**************************日期：" + date + "************************");
//				System.out.println("###############有可能少记喽#############");
//				for(BillingItems bitems: billingList){
//					System.out.println(bitems.getRowContent());
//				}
//			}
//			if(accountList.size()>0){
//				System.out.println("**************************日期：" + date + "************************");
//				System.out.println("###############有可能多记喽#############");
//				for(BillingItems aitems: accountList){
//					System.out.println(aitems.getRowContent());
//				}
//			}
//		} 
        
        
	}
	
	public static void main(String[] args) {
		try {
			Map<Integer, List<BillingItems>> accountMap1 = readExcelByPoiV2003AccountBook("F:\\BrowserDownload\\wacai_2019-06-2019-07.xls");
			Map<Integer, List<BillingItems>> billingMap1 = readExcelByPoiV2003Billing("F:\\BrowserDownload\\刘明先生201907交易明细报表.xls");
			compare("201907", billingMap1, accountMap1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
