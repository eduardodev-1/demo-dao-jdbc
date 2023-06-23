package application.dao;

import application.dao.implement.SellerDaoJDBC;
import db.DB;

public class DaoFactory {

	public static SellerDao createSellerDao() {
		return new SellerDaoJDBC(DB.getConnection());
	}
}
