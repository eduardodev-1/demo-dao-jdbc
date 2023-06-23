package application;

import application.dao.DaoFactory;
import application.dao.SellerDao;
import application.entities.Seller;

public class App {

	public static void main(String[] args) {
		
		SellerDao sellerDao = DaoFactory.createSellerDao();
		
		System.out.println("=== TEST 1: seller findById ===");
		Seller seller = sellerDao.findById(3);
		
		System.out.println(seller);
	}
}