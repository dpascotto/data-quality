package it.mapsgroup.dq.bulkwriter.mapper;

import it.mapsgroup.dq.vo.SaipemFlatDataVo;
import it.mapsgroup.dq.vo.UnitOfMeasureVo;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class SaipemFlatDataRowMapper<T> implements RowMapper<SaipemFlatDataVo> {

	@Override
	public SaipemFlatDataVo mapRow(ResultSet rs, int rowNum) throws SQLException {
		SaipemFlatDataVo sfd = null;
		if (rs != null) {
			sfd = new SaipemFlatDataVo();
			
			sfd.setItemCode(rs.getString("CODE"));
			sfd.setItemDescriptionIt(rs.getString("SHORT_DESCRIPTION_IT"));
			sfd.setItemDescriptionEn(rs.getString("SHORT_DESCRIPTION_EN"));
			sfd.setItemDescriptionEs(rs.getString("SHORT_DESCRIPTION_ES"));
			sfd.setType(rs.getString("TYPE"));
			sfd.setUmCode(rs.getString("UM_CODE"));
			sfd.setUmTechCode(rs.getString("UM_TECH_CODE"));
			sfd.setUmDescription(rs.getString("UM_DESCRIPTION"));
			sfd.setProductGroupCode(rs.getString("PG_CODE"));
			sfd.setProductGroupShortDescriptionIt(rs.getString("PG_SHORT_DES_IT"));
			sfd.setProductGroupLongDescriptionIt(rs.getString("PG_LONG_DES_IT"));
			sfd.setProductGroupShortDescriptionEn(rs.getString("PG_SHORT_DES_EN"));
			sfd.setProductGroupLongDescriptionEn(rs.getString("PG_LONG_DES_EN"));
			sfd.setProductGroupShortDescriptionEs(rs.getString("PG_SHORT_DES_ES"));
			sfd.setProductGroupLongDescriptionEs(rs.getString("PG_LONG_DES_ES"));
			sfd.setLab(rs.getString("LAB"));
			sfd.setMachineCode(rs.getString("MACHINE_CODE"));
			sfd.setMainPartNumber(rs.getString("MAIN_PART_NUMBER"));
			sfd.setGroupCode(rs.getString("GROUP_CODE"));
			sfd.setSubgroupCode(rs.getString("SUBGROUP_CODE"));
			sfd.setManufacturerCode(rs.getString("MANUFACTURER_CODE"));
		}
		return sfd;
	}

}
