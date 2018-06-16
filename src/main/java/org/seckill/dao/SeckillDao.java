package org.seckill.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

public interface SeckillDao {
	/**
	 * @param seckillId
	 * @param killTime
	 */
	int reduceNum(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);
	
	/**
	 * @param seckillId
	 * @return 
	 */
	Seckill querySeckillById(long seckillId);
	
	/**
	 * @param offset
	 * @param end
	 */
	List<Seckill> queryAllSeckill(@Param("offset")int offset, @Param("end")int end);
}
