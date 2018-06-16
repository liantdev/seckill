package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;

public interface SuccessKilledDao {
	
	/**
	 * @param seckillId
	 * @param userPhone
	 * @return
	 */
	int insertSuccessKilled(@Param("seckill_id")long seckillId, @Param("user_phone") long userPhone);
	
	/**
	 * @param seckillId
	 * @return
	 */
	SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId,@Param("userPhone") long userPhone);
}
