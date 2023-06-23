package application;

import java.util.Date;

import application.dao.DaoFactory;
import application.dao.SellerDao;
import application.entities.Department;
import application.entities.Seller;

public class App {

	public static void main(String[] args) {
		
		Department obj = new Department(1, "Books");
		System.out.println(obj);
		
		Seller seller = new Seller(21, "Bob", "bob@gmail.com", new Date(), 3000.0, obj);
		
		SellerDao sellerDao = DaoFactory.createSellerDao();
		
		System.out.println(seller);
	}
}