package application;

import application.dao.DaoFactory;
import application.dao.SellerDao;
import application.entities.Seller;

public class App {

	public static void main(String[] args) {
		
		SellerDao sellerDao = DaoFactory.createSellerDao();
		
		Seller seller = sellerDao.findById(3);
		
		System.out.println(seller);
	}
}