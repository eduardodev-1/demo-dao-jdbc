package application.dao.implement;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.dao.SellerDao;
import application.entities.Department;
import application.entities.Seller;
import db.DB;
import db.exception.DbException;

public class SellerDaoJDBC implements SellerDao {

	private Connection conn;
	
	
	
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller seller) {
		PreparedStatement st = null;
		Statement st1 = null;
		try {
			String sql = "INSERT INTO seller "
					+ "(Id, Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?, ?)";
			String sqlNoId = "INSERT INTO seller "
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?)";
			
			
			Integer nextFreeId = verifyFirstFreeId();
			if (nextFreeId != null) {
				st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				st.setInt(1, nextFreeId);
				st.setString(2, seller.getName());
				st.setString(3, seller.getEmail());
				st.setDate(4, new Date(seller.getBirthDate().getTime()));
				st.setDouble(5, seller.getBaseSalary());
				st.setInt(6, seller.getDepartment().getId());
			}
			else {
				st1 = conn.createStatement();
				st1.execute("ALTER TABLE seller AUTO_INCREMENT = 1");
				DB.closeStatement(st1);
				st = conn.prepareStatement(sqlNoId, Statement.RETURN_GENERATED_KEYS);
				st.setString(1, seller.getName());
				st.setString(2, seller.getEmail());
				st.setDate(3, new Date(seller.getBirthDate().getTime()));
				st.setDouble(4, seller.getBaseSalary());
				st.setInt(5, seller.getDepartment().getId());
			}
			int rowsAffected = st.executeUpdate();
			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				rs.first();
				int id = rs.getInt(1);
				seller.setId(id);
				DB.closeResultSet(rs);
			}
			else {
				throw new DbException("Unexpected error! No rows affected!");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage())
;		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Seller seller) {
		PreparedStatement st = null;
		try {
			String sql = "UPDATE seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
					+ "WHERE Id = ?";
			
				st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				st.setString(1, seller.getName());
				st.setString(2, seller.getEmail());
				st.setDate(3, new Date(seller.getBirthDate().getTime()));
				st.setDouble(4, seller.getBaseSalary());
				st.setInt(5, seller.getDepartment().getId());
				st.setInt(6, seller.getId());
			
			st.executeUpdate();
			
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
			}
		finally {
			DB.closeStatement(st);
		}
	}
	
	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			String sql = "DELETE FROM seller WHERE Id = ?";
			st = conn.prepareStatement(sql);
			st.setInt(1, id);
			st.execute();
		}
		catch (SQLException e){
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE seller.Id = ?";
			st = conn.prepareStatement(sql);
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Department dep = instantiateDepartment(rs);
				
				Seller seller = instantiateSeller(rs, dep);
				
				return seller;
			}
		return null;
		}
		catch (SQLException e){
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller seller = new Seller();
		seller.setId(rs.getInt("Id"));
		seller.setName(rs.getString("Name"));
		seller.setEmail(rs.getString("Email"));
		seller.setBaseSalary(rs.getDouble("BaseSalary"));
		seller.setBirthDate(rs.getDate("BirthDate"));
		seller.setDepartment(dep);
		return seller;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "ORDER BY Name";
			
			st = conn.prepareStatement(sql);
			rs = st.executeQuery();
			
			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			
			while (rs.next()) {
				//verificando se o departamento ja existe na lista
				Department dep = map.get(rs.getInt("DepartmentId"));
				
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
				Seller seller = instantiateSeller(rs, dep);
				list.add(seller);
			}
		return list;
		}
		catch (SQLException e){
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE DepartmentId = ? "
					+ "ORDER BY Name";
			
			st = conn.prepareStatement(sql);
			st.setInt(1, department.getId());
			rs = st.executeQuery();
			
			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			
			while (rs.next()) {
				//verificando se o departamento ja existe na lista
				Department dep = map.get(rs.getInt("DepartmentId"));
				
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
				Seller seller = instantiateSeller(rs, dep);
				list.add(seller);
			}
		return list;
		}
		catch (SQLException e){
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
	private Integer verifyFirstFreeId() throws SQLException {
		Statement st1 = null;
		Statement st2 = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		Integer nextFreeId = null;
		
		st1 = conn.createStatement();
		st2 = conn.createStatement();

		rs1 = st1.executeQuery("select * from seller");

		while (rs1.next()) {
			int verificacao = rs1.getInt("Id") + 1;
			rs2 = st2.executeQuery("select * from seller where Id = '" + verificacao + "'");
			if (!rs2.next()) {
				nextFreeId = verificacao;
				return nextFreeId;
			}
		}
		DB.closeResultSet(rs1);
		DB.closeResultSet(rs2);
		DB.closeStatement(st1);
		DB.closeStatement(st2);
		return null;
		
	}
}
