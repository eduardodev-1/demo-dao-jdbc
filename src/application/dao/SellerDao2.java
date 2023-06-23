package application.dao;

import java.util.List;

import application.entities.Seller;

public interface SellerDao2 {

	void insert(Seller obj);
	void update(Seller obj);
	void deleteById(Integer id);
	Seller findById(Integer id);
	List<Seller> findAll();
}
