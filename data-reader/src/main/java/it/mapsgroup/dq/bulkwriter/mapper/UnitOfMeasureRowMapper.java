package it.mapsgroup.dq.bulkwriter.mapper;

import it.mapsgroup.dq.vo.UnitOfMeasureVo;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class UnitOfMeasureRowMapper<T> implements RowMapper<UnitOfMeasureVo> {

	@Override
	public UnitOfMeasureVo mapRow(ResultSet rs, int rowNum) throws SQLException {
		UnitOfMeasureVo um = null;
		if (rs != null) {
			um = new UnitOfMeasureVo();
			
			um.setCode(rs.getString("CODE"));
			um.setTechCode(rs.getString("TECH_CODE"));
			um.setDescription(rs.getString("DESCRIPTION"));
		}
		return um;
	}

}
