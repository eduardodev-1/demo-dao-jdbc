package application;

import java.util.Date;
import java.util.List;

import application.dao.DaoFactory;
import application.dao.SellerDao;
import application.entities.Department;
import application.entities.Seller;

public class App {

	public static void main(String[] args) {
		
		SellerDao sellerDao = DaoFactory.createSellerDao();
		
		System.out.println("=== TEST 1: seller findById ===");
		Seller seller = sellerDao.findById(3);
		System.out.println(seller);
		System.out.println();
		
		System.out.println("=== TEST 2: seller findByDepartment ===");
		Department department = new Department(2);
		List<Seller> list = sellerDao.findByDepartment(department);
		for (Seller obj : list) {
			System.out.println(obj);
		}
		System.out.println();
		
		System.out.println("=== TEST 3: seller findAll ===");
		list = sellerDao.findAll();
		for (Seller obj : list) {
			System.out.println(obj);
		}
		System.out.println();
		
		System.out.println("=== TEST 4: seller insert ===");
		//Seller newSeller = new Seller(null, "Greg", "greg@gmail.com", new Date(), 4000.0, department);
		//sellerDao.insert(newSeller);
		//System.out.println("Inserted! New id = " + newSeller.getId());
		System.out.println();
		
		System.out.println("=== TEST 5: seller seller update ===");
		seller = sellerDao.findById(9);
		seller.setName("Jorge");
		seller.setEmail("jorge@gmail.com");
		sellerDao.update(seller);
		System.out.println("Updated! id = " + seller.getId());
		System.out.println();
	}
}