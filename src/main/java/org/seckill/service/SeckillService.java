package org.seckill.service;

import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatSeckillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

public interface SeckillService {
	
	/**
	 * 获取秒杀明细列表
	 * @return
	 */
	List<Seckill> getSeckillList();
	
	/**
	 * 获取单个商品秒杀明细
	 * @param seckillId
	 * @return
	 */
	Seckill getSeckillById(long seckillId);
	
	/**
	 * 获取秒杀暴露地址接口
	 * @param seckillId
	 */
	Exposer exportSeckillUrl(long seckillId);
	
	/**
	 * 执行秒杀
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 */
	SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillCloseException,RepeatSeckillException,SeckillException;
}
